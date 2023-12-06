package advent2023.day5

import advent2023.utils.headTail
import advent2023.utils.runPuzzle
import advent2023.utils.splitBy
import java.io.File

const val day = 5
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val (seedsInput, mapsInput) = input.splitBy { it.isEmpty() }.headTail()
        val seeds = seedsInput[0].substringAfter(": ").split(" ").map { it.toLong() }
        val maps = mapsInput
            .map { map -> map.drop(1).map { Conversion.toConversion(it.trim()) } }
        println(
            seeds.minOfOrNull { seed -> maps.fold(seed) { result, map -> map.convert(result) } }
        )
    }
    
    fun runPart2() {
        val (seedsInput, mapsInput) = input.splitBy { it.isEmpty() }.headTail()
        
        val seedRanges = seedsInput[0].substringAfter(": ")
            .split(" ")
            .map { it.toLong() }
            .chunked(2)
            .map { it[0]..<  (it[0] + it[1]) }
            .sortedBy { it.first }
        val maps = mapsInput
            .map { map -> map.drop(1).map { Conversion.toConversion(it.trim()) } }
        println(
            maps.fold(seedRanges) { ranges, map -> map.mapRanges(ranges) }.minOfOrNull { it.first }
        )
    }
}

fun List<Conversion>.convert(input: Long) =
    find { input in it.sourceRangeStart..<(it.sourceRangeStart + it.rangeLength) }
        ?.let { input - (it.sourceRangeStart - it.destRangeStart) } ?: input

fun List<Conversion>.mapRanges(input: List<LongRange>): List<LongRange> {
    // cut up all input to make mapping easier (no overlap)
    val starts = map { it.sourceRangeStart }
    val endsExclusive = map { it.sourceRangeStart + it.rangeLength }
    val pointsExclusive = (starts + endsExclusive).toSet().sorted()
    val cutInputRanges = mutableListOf<LongRange>()
    for (inputRange in input) {
        var currentStart = inputRange.first
        for (pointExclusive in pointsExclusive) {
            if (pointExclusive in inputRange) {
                cutInputRanges += currentStart..< pointExclusive
                currentStart = pointExclusive
            }
        }
        cutInputRanges += currentStart .. inputRange.last
    }
    val newRanges = mutableListOf<LongRange>()
    for (inputRange in cutInputRanges) {
        var converted = false
        for (map in this) {
            if (inputRange.first >= map.sourceRangeStart &&
                inputRange.last <= (map.sourceRangeStart + map.rangeLength - 1)
            ) {
                val delta = map.destRangeStart - map.sourceRangeStart
                newRanges += (inputRange.first + delta) .. (inputRange.last + delta)
                converted = true
            }
        }
        if (!converted) newRanges += inputRange
    }
    return newRanges
}

data class Conversion(val destRangeStart: Long, val sourceRangeStart: Long, val rangeLength: Long) {
    companion object {
        fun toConversion(input: String): Conversion {
            val (destRangeStart, sourceRangeStart, rangeLength) = input.split(" ").map { it.toLong() }
            return Conversion(destRangeStart, sourceRangeStart, rangeLength)
        } 
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

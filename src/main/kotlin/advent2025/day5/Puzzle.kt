package advent2025.day5

import lib.mapToPair
import lib.runPuzzle
import lib.splitBy
import java.io.File
import kotlin.math.min

const val day = 5
val file = File("src/main/resources/advent2025/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val (ranges, ingredients) = input.splitBy { it.isEmpty() }.mapToPair(
            transformLeft = { it.map { it.split("-") }.map { Range(it[0], it[1]) } },
            transformRight = { it.map { it.toLong() } }
        )
        
        println(ingredients.count { ingredient -> ranges.any { range -> range.inRange(ingredient) } })
    }

    fun runPart2() {
        val ranges = input.splitBy { it.isEmpty() }.first()
            .map { it.split("-") }.map { Range(it[0], it[1]) }

        
        val cutUpRanges = ranges.flatMap { it.cutUp(ranges) }.toSet()
        println(cutUpRanges.sumOf { it.high - it.low + 1 })
    }
}

data class Range(val low: Long, val high: Long) {
    fun inRange(point: Long): Boolean = point in low..high
    fun cutUp(allRanges: List<Range>): List<Range> {
        val lowPoints = allRanges.map { it.low }.sorted().toSet()
        val highPoints = allRanges.map { it.high }.sorted().toSet()
        
        // cut up ranges into pieces so we can later filter out overlapping ranges
        val cutUpRanges = mutableListOf<Range>()
        var currentLow = low
        do {
            val nextHigh = min(
                lowPoints.map { it - 1 }.firstOrNull { it >= currentLow } ?: Long.MAX_VALUE,
                highPoints.first { it >= currentLow }
            )
            cutUpRanges += Range(currentLow, nextHigh)
            currentLow = nextHigh + 1
        } while(nextHigh < high)
        return cutUpRanges
    }

    constructor(low: String, high: String) : this(low.toLong(), high.toLong())
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
package advent2023.day12

import advent2023.utils.mapToPair
import advent2023.utils.runPuzzle
import org.paukov.combinatorics3.Generator
import java.io.File
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToLong

const val day = 12
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        println(
            input.map { ConditionRecord.createRecord(it) }
                .sumOf { it.findNoOfArrangements() }
        )
    }

    fun runPart2() {
        val map = input.map { ConditionRecord.createRecord(it) }
            .associateWith { it.findArrangements() }
        val qFirstMap = map.keys.associateWith { it.findNoOfArrangements("?${it.record}") }
        val qLastMap = map.keys.associateWith { it.findNoOfArrangements("${it.record}?") }

        val qFirstTotal = qFirstMap.mapValues { map.getValue(it.key).size * it.value.toDouble().pow(4.0).roundToLong() }
        val qLastTotal = qLastMap.mapValues { map.getValue(it.key).size * it.value.toDouble().pow(4.0).roundToLong() }
        map.forEach {
            println("${it.key} normal: ${it.value} q-first: ${qFirstMap.get(it.key)} q-last: ${qLastMap.get(it.key)}")
        }
        map.forEach {
            println(
                "multiplying ${it.key}, simple: ${it.value} q-first ${
                    it.value.size * qFirstMap.getValue(it.key).toDouble().pow(4.0).roundToLong()
                } " +
                        "q-last: ${it.value.size * qLastMap.getValue(it.key).toDouble().pow(4.0).roundToLong()}}"
            )
        }
        println(qFirstTotal)
        println(qLastTotal)
        // first or last -> go for the high one, only exception is if extra ? is forced . (= # as last)
        // 315325593540 too low
        println(
            map.keys.sumOf { key ->
                val qFirst = qFirstTotal.getValue(key)
                val qLast = qLastTotal.getValue(key)
                val lastIsSpring = map.getValue(key).all { (it.last() == '#' || it.first() == '#') }
                if (lastIsSpring) qLast else max(qLast, qFirst)
            }
        )
    }
}

data class ConditionRecord(val record: String, val groups: List<Int>) {
    fun findNoOfArrangements(input: String = record, inputGroups: List<Int> = groups): Int {
        val questionMarks = input.count { it == '?' }
        val knownSprings = input.count { it == '#' }
        val totalSprings = inputGroups.sum()
        val unknownSprings = totalSprings - knownSprings
        val unknownEmptySpots = questionMarks - unknownSprings
        val combinations = Generator.permutation(
            List(unknownSprings) { '#' } + List(unknownEmptySpots) { '.' }
        ).simple().stream().toList()
        return combinations
            .map { input.replaceInstances('?', it) }
            .count { it.split(Regex("\\.+")).filter { it.isNotEmpty() }.map { it.length } == inputGroups }
    }

    fun findArrangements(): List<String> {
        val questionMarks = record.count { it == '?' }
        val knownSprings = record.count { it == '#' }
        val totalSprings = groups.sum()
        val unknownSprings = totalSprings - knownSprings
        val unknownEmptySpots = questionMarks - unknownSprings
        val combinations = Generator.permutation(
            List(unknownSprings) { '#' } + List(unknownEmptySpots) { '.' }
        ).simple().stream().toList()
        return combinations
            .map { record.replaceInstances('?', it) }
            .filter { it.split(Regex("\\.+")).filter { it.isNotEmpty() }.map { it.length } == groups }
    }

    companion object {
        fun createRecord(input: String): ConditionRecord {
            val (rec, groups) = input.split(" ")
                .mapToPair<String, List<Int>>(transformRight = { it.split(",").map { it.toInt() } })
            return ConditionRecord(rec, groups)
        }
    }
}

private fun String.replaceInstances(c: Char, replacements: List<Char>): String {
    var idx = 0
    return toList().map { if (it == c) replacements[idx++] else it }.joinToString("")
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

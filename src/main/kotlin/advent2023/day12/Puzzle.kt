package advent2023.day12

import lib.mapToPair
import lib.runPuzzle
import org.paukov.combinatorics3.Generator
import java.io.File

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
        println(input.map { ConditionRecord.createFullRecord(it) }
            .sumOf { findNoOfArrangements(it.record, it.groups) })
    }
}

private val cache = mutableMapOf<Pair<String, List<Int>>, Long>()
// after ash42
fun findNoOfArrangements(record: String, groups: List<Int>): Long {
    val key = Pair(record, groups)
    if (cache.containsKey(key)) return cache.getValue(key)
    
    if (record.isEmpty())
        // found a solution
        return if (groups.isEmpty()) 1 else 0
    
    var number = 0L
    when(record[0]) {
        '.' ->
            // no spring present, skip
            number = findNoOfArrangements(record.drop(1), groups)
        '?' ->
            // could be empty or spring, add both
            number = findNoOfArrangements('.' + record.drop(1), groups) + 
                findNoOfArrangements('#' + record.drop(1), groups)
        '#' -> {
            if (groups.isEmpty()) {
                number = 0
            } else {
                val currentSpringLength = groups[0]

                if (currentSpringLength > record.length || record.take(currentSpringLength).any { it == '.' }) {
                    // not a valid solution
                    number = 0
                } else {
                    val newGroups = groups.drop(1)
                    when {
                        currentSpringLength == record.length ->
                            // remaining spring length is record length, we are done
                            number = if (newGroups.isEmpty()) 1 else 0

                        record[currentSpringLength] == '.' ->
                            // we have found a group, continue and skip .
                            number = findNoOfArrangements(record.drop(currentSpringLength + 1), newGroups)

                        record[currentSpringLength] == '?' ->
                            // we have found a group, continue, ? has to be .
                            number = findNoOfArrangements('.' + record.drop(currentSpringLength + 1), newGroups)

                        else ->
                            // next character is a #, not possible
                            number = 0
                    }
                }
            }
        }
        else -> throw IllegalStateException()
    }
    cache[key] = number
    return number
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
   
    companion object {
        fun createRecord(input: String): ConditionRecord {
            val (rec, groups) = input.split(" ")
                .mapToPair<String, String, List<Int>>(transformRight = { it.split(",").map { it.toInt() } })
            return ConditionRecord(rec, groups)
        }

        fun createFullRecord(input: String): ConditionRecord {
            val (rec, groups) = input.split(" ")
                .mapToPair(
                    transformLeft = { r -> List(5) { r }.joinToString("?") },
                    transformRight = { s -> List(5) { s.split(",").map { it.toInt() } }.flatten() }
                )
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

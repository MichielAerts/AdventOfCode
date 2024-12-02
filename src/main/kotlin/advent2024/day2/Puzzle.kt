package advent2024.day2

import lib.runPuzzle
import lib.withoutItemAt
import java.io.File

const val day = 2
val file = File("src/main/resources/advent2024/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val reports = input.map { Report(it.split(" ").map { it.toInt() }) }
        println(reports.count { it.isSafe() })
    }

    fun runPart2() {
        val reports = input.map { Report(it.split(" ").map { it.toInt() }) }
        println(reports.count { it.isSafeWithToleratingOneBadLevel() })
    }
}

data class Report(val levels: List<Int>) {
    fun isSafe(): Boolean {
        val diffs = levels.zipWithNext { l, r -> r - l }
        return diffs.all { it in 1..3 } || diffs.all { it in -3 .. -1 }
    }
    
    fun isSafeWithToleratingOneBadLevel(): Boolean {
        if (isSafe()) return true
        return levels.indices.any { idx -> Report(levels.withoutItemAt(idx)).isSafe() }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
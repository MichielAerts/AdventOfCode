package advent2023.day9

import lib.runPuzzle
import java.io.File

const val day = 9
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val histories = input.map { it.split(" ").map { it.toInt() } }
        println(histories.sumOf { findNextValue(it) })
    }

    fun runPart2() {
        val histories = input.map { it.split(" ").map { it.toInt() } }
        println(histories.sumOf { findPreviousValue(it) })
    }

    private fun findNextValue(list: List<Int>): Int =
        if (list.all { it == 0 })
            0
        else
            list.last() + findNextValue(list.zipWithNext().map { it.second - it.first })

    private fun findPreviousValue(list: List<Int>): Int =
        if (list.all { it == 0 })
            0
        else
            list.first() - findPreviousValue(list.zipWithNext().map { it.second - it.first })
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

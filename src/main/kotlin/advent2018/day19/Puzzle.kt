package advent2018.day19

import lib.runPuzzle
import java.io.File

const val day = 19
val file = File("src/main/resources/advent2018/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        println(input)
    }

    fun runPart2() {
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
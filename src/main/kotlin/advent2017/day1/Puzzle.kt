package advent2017.day1

import lib.rotate
import lib.runPuzzle
import java.io.File

const val day = 1
val file = File("src/main/resources/advent2017/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val numbers = input[0].toList().map { it.digitToInt() }
        val numbersWrappedAround = numbers + numbers[0]

        println(
            numbersWrappedAround.zipWithNext()
                .filter { it.first == it.second }
                .sumOf { it.first }
        )
    }

    fun runPart2() {
        val numbers = input[0].toList().map { it.digitToInt() }

        println(
            numbers.zip(numbers.rotate(numbers.size / 2))
                .filter { it.first != it.second }
                .sumOf { it.first }
        )
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
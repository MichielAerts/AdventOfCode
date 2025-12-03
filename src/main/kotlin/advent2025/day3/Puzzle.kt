package advent2025.day3

import lib.runPuzzle
import java.io.File

const val day = 3
val file = File("src/main/resources/advent2025/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        println(input.sumOf { it.largestTwoBatteryJoltage() })
    }

    fun runPart2() {
        println(input.sumOf { it.findHighestJoltage(12).toLong() })
    }
}

private fun String.findHighestJoltage(n: Int): String {
    val joltages = this.toList().map { it.digitToInt().toLong() }
    if (n == 1) return joltages.max().toString()

    //take the first occurrence of the highest number in the first (l - n)
    val highest = joltages.dropLast(n - 1).max()
    val indexNextHighest = joltages.indexOfFirst { it == highest }
    return highest.toString() + this.substring(indexNextHighest + 1).findHighestJoltage(n - 1)
}

private fun String.largestTwoBatteryJoltage(): Int {
    var currentHighest = 11
    for (first in 0..<this.length) {
        for (second in (first + 1)..<this.length) {
            val joltage = "${this[first]}${this[second]}".toInt()
            if (joltage > currentHighest) currentHighest = joltage
        }
    }
    return currentHighest
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
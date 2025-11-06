package advent2017.day2

import lib.runPuzzle
import java.io.File

const val day = 2
val file = File("src/main/resources/advent2017/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val result = input.map { it.split(Regex("\\s+")).map { it.toInt() } }
            .sumOf { it.max() - it.min() }
        println(result)
    }

    fun runPart2() {
        val result = input.map { it.split(Regex("\\s+")).map { it.toInt() } }
            .sumOf { it.findEvenlyDivider() }
        println(result)
    }
    
}

private fun List<Int>.findEvenlyDivider(): Int {
    val result = this.firstNotNullOf { numerator ->
        this.find { denominator -> denominator != numerator && numerator % denominator == 0 }
            ?.let { numerator / it }
    }
    println("found $result in $this")
    return result
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
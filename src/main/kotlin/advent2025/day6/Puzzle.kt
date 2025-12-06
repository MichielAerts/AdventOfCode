package advent2025.day6

import lib.runPuzzle
import lib.transpose
import java.io.File

const val day = 6
val file = File("src/main/resources/advent2025/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val sheet = input.map { it.trim().split(Regex("\\s+")) }.transpose()
        println(sheet.sumOf { it.solveProblem() })
    }

    fun runPart2() {
        val pattern = Regex("([*+])\\s+")
        val sizes = pattern.findAll(input.last()).toList()
        val operations = sizes.mapIndexed { idx, s -> 
            val end = if (idx == sizes.size - 1) s.range.last else s.range.last - 1 
            IntRange(s.range.first, end) to s.value[0]
        }
        val ranges = operations.map { it.first }
        val sheet = input.map { line -> ranges.map { line.substring(it) } }.transpose()
        println(sheet.sumOf { it.solveProblemWithCephalopodMath() })
    }
}

private fun List<String>.solveProblem(): Long {
    val numbers = this.dropLast(1).map { it.toLong() }
    return when(last()) {
        "*" -> numbers.reduce { a, b -> a * b }
        "+" -> numbers.reduce { a, b -> a + b }
        else -> throw UnsupportedOperationException()
    }
}

private fun List<String>.solveProblemWithCephalopodMath(): Long {
    val numbers = this.dropLast(1).map { it.toList() }.transpose().map { it.joinToString("").trim().toLong() }
    return when(last().trim()) {
        "*" -> numbers.reduce { a, b -> a * b }
        "+" -> numbers.reduce { a, b -> a + b }
        else -> throw UnsupportedOperationException()
    }
}
fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
package advent2015.day8

import lib.runPuzzle
import java.io.File

const val day = 8
val file = File("src/main/resources/advent2015/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        println(input.sumOf { it.length } - input.sumOf { it.inMemoryLength() })
    }

    fun runPart2() {
        input.forEach { println("in: $it, l: ${it.length} encoded: ${it.encoded()}, l: ${it.encodedLength()}") }
        println(input.sumOf { it.encodedLength() } - input.sumOf { it.length })
    }
}

private fun String.encodedLength() = this.encoded().length

private fun String.encoded(): String =
    "\"" + this.replace("\\", "\\\\")
        .replace("\"", "\\\"") + "\""

private fun String.inMemory(): String {
    val actual = this
        .replace("\\\\", "X")
        .replace("\\\\x[a-f0-9]{2}".toRegex(), "Y")
        .replace("\\\"", "Z")
    return actual.substring(1, actual.length - 1)
}

private fun String.inMemoryLength(): Int {
    return this.inMemory().length
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
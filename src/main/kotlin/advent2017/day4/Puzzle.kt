package advent2017.day4

import lib.runPuzzle
import java.io.File

const val day = 4
val file = File("src/main/resources/advent2017/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        println(input.count { it.isValidPassphrase() })
    }

    fun runPart2() {
        println(input.count { it.isValidPassphraseNoAnagrams() })
    }
}

private fun String.isValidPassphrase(): Boolean {
    val words = this.split(Regex("\\s+"))
    return words.size == words.toSet().size
}

private fun String.isValidPassphraseNoAnagrams(): Boolean {
    val words = this.split(Regex("\\s+")).map { it.toList().sorted().joinToString(separator = "") }
    return words.size == words.toSet().size
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
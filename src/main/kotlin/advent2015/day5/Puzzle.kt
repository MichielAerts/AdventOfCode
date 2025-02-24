package advent2015.day5

import lib.runPuzzle
import java.io.File

const val day = 5
val file = File("src/main/resources/advent2015/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        println(input.count { it.isNice() })
    }

    fun runPart2() {
        println(input.count { it.isReallyNice() })
    }
}

val doubleLetterRegex = "(.)\\1".toRegex()
val forbiddenRegex = "(ab)|(cd)|(pq)|(xy)".toRegex()

private fun String.isNice(): Boolean {
    val threeVowels = count { it in "aeiou" } >= 3
    val doubleLetter = doubleLetterRegex.find(this) != null
    val containsForbiddenSequence = forbiddenRegex.find(this) != null
    return threeVowels && doubleLetter && !containsForbiddenSequence
}

private fun String.isReallyNice(): Boolean {
    val pairAppearsTwice = windowed(2).any { this.substringAfter(it, "").contains(it) }
    val threesomeWithDouble = windowed(3).any { it[0] == it[2] }
    return pairAppearsTwice && threesomeWithDouble
}
fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
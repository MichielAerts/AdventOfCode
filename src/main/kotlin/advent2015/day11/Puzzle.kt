package advent2015.day11

import lib.alphabet
import lib.runPuzzle
import java.io.File

const val day = 11
val file = File("src/main/resources/advent2015/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val currentPassword = input[0]
//        val currentPassword = "ghjaaaba"
        var newPassword = currentPassword
        while(newPassword == currentPassword || !newPassword.meetsRequirements()) {
            newPassword = newPassword.increment()
//            println(newPassword)
        }
        println(newPassword)
    }

    fun runPart2() {
        println(input)
    }
}

private fun String.increment(): String {
    val idx = indexOfLast { it != 'z' }
    return substring(0, idx) + (this[idx] + 1) + "a".repeat(this.length - 1 - idx)
}

val iol = "[iol]+".toRegex()
val straightOfThree = alphabet.windowed(3).map { it.joinToString("") }
val pairs = alphabet.map { "$it$it" } 
    
private fun String.meetsRequirements(): Boolean {
    val doesntHaveIOL = iol.find(this) == null
    val hasStraightOfThree = straightOfThree.any { this.contains(it) }
    val hasTwoDifferentPairs = pairs.count { this.contains(it) } >= 2 
    return doesntHaveIOL && hasStraightOfThree && hasTwoDifferentPairs
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
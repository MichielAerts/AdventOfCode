package advent2018.day5

import lib.runPuzzle
import java.io.File
import kotlin.math.absoluteValue

const val day = 5
val file = File("src/main/resources/advent2018/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val polymer = input[0]
        
        val reaction = generateSequence(polymer) { polymer ->
            val newPolymer = polymer.react()
            if (newPolymer.length != polymer.length) newPolymer else null
        }
        val result = reaction.last()
        println(result.length)
    }

    fun runPart2() {
        val polymer = input[0]
        var shortestPolymerLength = polymer.length
        for (c in 'a'..'z') {
            val cleanedPolymer = polymer.replace("$c", "", ignoreCase = true)
            val reaction = generateSequence(cleanedPolymer) { polymer ->
                val newPolymer = polymer.react()
                if (newPolymer.length != polymer.length) newPolymer else null
            }
            val result = reaction.last()
            if (result.length < shortestPolymerLength) {
                shortestPolymerLength = result.length
            }
        }
        println(shortestPolymerLength)
    }
}

private fun String.react(): String {
    val sb = StringBuilder()
    var i = 0
    while(i < (length - 1)) {
        if ((this[i] - this[i + 1]).absoluteValue == 32) {
            i += 2
        } else {
            sb.append(this[i])
            if (i == length - 2) sb.append(this[i + 1])
            i++
        }
    }
    return sb.toString()
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
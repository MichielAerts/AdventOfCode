package advent2018.day1

import lib.asRepeatedSequence
import lib.runPuzzle
import java.io.File

const val day = 1
val file = File("src/main/resources/advent2018/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val frequencyChanges = input.map { it.toInt() }
        val result = frequencyChanges.fold(0) { a, b -> a + b}
        println(result)
    }

    fun runPart2() {
        val frequencyChanges = input.map { it.toInt() }.asRepeatedSequence().iterator()
        val visitedFrequencies = mutableSetOf<Int>()
        var currentFrequency = 0
        while(true) {
            currentFrequency += frequencyChanges.next()
            if (currentFrequency in visitedFrequencies) break
            visitedFrequencies += currentFrequency
        }
        println(currentFrequency)
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
package advent2015.day10

import lib.runPuzzle
import java.io.File

const val day = 10
val file = File("src/main/resources/advent2015/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val times = 50
        var currentInput = input[0]
        for (i in 1..times) {
            var newInput = StringBuilder()
            var currentNumber = currentInput[0]
            var times = 1
            for (newNumber in currentInput.substring(1)) {
                if (newNumber == currentNumber) {
                    times += 1
                } else {
                    newInput.append("$times$currentNumber")
                    currentNumber = newNumber
                    times = 1
                }
            }
            newInput.append("$times$currentNumber")
//            println(newInput)
            currentInput = newInput.toString()
        }
        println(currentInput.length)
    }

    fun runPart2() {
        println(input)
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
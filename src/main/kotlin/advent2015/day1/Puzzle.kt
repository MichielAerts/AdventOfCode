package advent2015.day1

import lib.runPuzzle
import java.io.File

const val day = 1
val file = File("src/main/resources/advent2015/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val up = input[0].count { it == '(' }
        val down = input[0].count { it == ')' }
        println(up - down)
    }

    fun runPart2() {
        var currentFloor = 0
        for ((idx, c) in input[0].withIndex()) {
            currentFloor += if (c == '(') 1 else -1
            if (currentFloor == -1) {
                println(idx + 1)
                break
            }
        }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
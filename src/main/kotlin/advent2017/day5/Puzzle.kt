package advent2017.day5

import lib.runPuzzle
import java.io.File

const val day = 5
val file = File("src/main/resources/advent2017/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val instructions = input.map { it.toInt() }.toMutableList()
        var idx = 0
        var steps = 0
        while (idx in 0..<instructions.size) {
            val jump = instructions[idx]
            if (jump >= 3) {
                instructions[idx]--
            } else {
                instructions[idx]++
            }
            idx += jump
            steps++
//            println(instructions)
        }
        println(steps)
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
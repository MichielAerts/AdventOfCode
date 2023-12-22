package advent2023.day17

import advent2023.utils.runPuzzle
import java.io.File

const val day = 17
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        println(input)
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

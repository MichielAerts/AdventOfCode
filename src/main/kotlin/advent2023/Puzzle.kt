package advent2023

import advent2023.utils.RegexM
import advent2023.utils.runPuzzle
import java.io.File

const val day = 1
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
//        val regex = RegexM.toRegex("(.*AB((C|D*E)F)*G)")
        val regex = RegexM.toRegex("((A*B|AC)D)")
        println(regex)
        println(regex.match("AABD"))
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

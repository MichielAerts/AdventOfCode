package advent2015.day17

import lib.allSublists
import lib.runPuzzle
import java.io.File

const val day = 17
val file = File("src/main/resources/advent2015/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val eggnog = 150
        val containers = input.map { it.toInt() }
        println(containers.allSublists().count { it.sum() == eggnog })
//        println(containers.allSublists().filter { it.sum() == eggnog }.minOf { it.size } )
    }

    fun runPart2() {
        val size = 4
        val eggnog = 150
        val containers = input.map { it.toInt() }
        println(containers.allSublists().filter { it.sum() == eggnog }.count { it.size == size } )
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
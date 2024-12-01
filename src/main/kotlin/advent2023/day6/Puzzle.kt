package advent2023.day6

import lib.product
import lib.runPuzzle
import lib.solveQuadraticEquation
import java.io.File
import kotlin.math.roundToInt

const val day = 6
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val (times, distances) = input.map { it.substringAfter(":").trim().split(Regex("\\s+")).map { it.toLong() } }
        val races = times.zip(distances).map { Race(it.first, it.second) }
        println(
            races.map { it.waysToWin() }.product()
        )
    }
    
    fun runPart2() {
        val (time, distance) = input.map { it.substringAfter(":").replace(" ", "").toLong() }
        val race = Race(time, distance)
        println(
            race.waysToWin()
        )
        val (first, second) = solveQuadraticEquation(-1.0, time.toDouble(), -distance.toDouble())!!
        println(
            (second - first).roundToInt()
        )
    }
}

data class Race(val time: Long, val currentRecord: Long) {
    fun waysToWin(): Int =
        (0..time).map { (time - it) * it }.count { it > currentRecord }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

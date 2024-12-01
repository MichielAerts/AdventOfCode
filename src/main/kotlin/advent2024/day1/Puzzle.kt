package advent2024.day1

import lib.runPuzzle
import lib.transpose
import java.io.File
import kotlin.math.absoluteValue

const val day = 1
val file = File("src/main/resources/advent2024/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        //3   4
        val (left, right) = input.map { it.split(Regex("\\s+")).map { it.toInt() } }
            .transpose()
            .map { it.sorted() }
        
        println(left.zip(right).sumOf { (it.first - it.second).absoluteValue })
    }

    fun runPart2() {
        val (left, right) = input.map { it.split(Regex("\\s+")).map { it.toInt() } }
            .transpose()
            .map { it.groupingBy { it }.eachCount() }
        println(left.entries.sumOf { it.key * it.value * (right[it.key] ?: 0) })
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
package advent2024.day25

import lib.*
import java.io.File

const val day = 25
val file = File("src/main/resources/advent2024/day${day}/input")

class Puzzle(private val input: List<String>) {
    
    val size = 5
    
    fun runPart1() {
        val allSchematics = input.splitBy { it.isEmpty() }
            .map { it.to2DGridOfPointsWithValues() }
        val keys = allSchematics.filter { it.isKey() }
            .map { it.getColumns().map { it.count { it.value == '#' } - 1 } }
        val locks = allSchematics.filter { it.isLock() }
            .map { it.getColumns().map { it.count { it.value == '#' } - 1 } }
        println(keys)
        println(locks)
        println(keys.sumOf { key ->
            locks.count { lock ->
                lock.zip(key).none { it.first + it.second > size }
            }
        }
        )
    }

    fun runPart2() {
        println(input)
    }
}

private fun List<List<Point>>.isKey() =
    first().all { it.value == '.' } &&
            last().all { it.value == '#' }

private fun List<List<Point>>.isLock() =
    first().all { it.value == '#' } &&
            last().all { it.value == '.' }

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
package advent2023.day13

import advent2023.utils.*
import java.io.File
import kotlin.math.min

const val day = 13
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        println(input.splitBy { it.isEmpty() }
            .map { it.to2DGridOfPointsWithValues() }
            .sumOf { it.summarizePatternNotes() }
        )
    }

    fun runPart2() {
        println(
            input.splitBy { it.isEmpty() }
                .map { it.to2DGridOfPointsWithValues() }
                .sumOf { it.summarizeSmudgedPatternNotes() }
        )
    }
}

private fun List<List<Point>>.summarizePatternNotes(): Int =
    getReflectingRow()?.let { it * 100 } ?: transpose().getReflectingRow()
    ?: throw IllegalStateException("Couldn't find reflection")

private fun List<List<Point>>.summarizeSmudgedPatternNotes(): Int =
    getSmudgedReflectingRow()?.let { it * 100 } ?: transpose().getSmudgedReflectingRow()
    ?: throw IllegalStateException("Couldn't find reflection")

private fun List<List<Point>>.getSmudgedReflectingRow(): Int? {
    for (rowNum in 1..<this.size) {
        val reflectingRows = min(rowNum, this.size - rowNum)
        val reflectionPairs = this.subList(0, rowNum).takeLast(reflectingRows).flatten().map { it.value }.zip(
                this.subList(rowNum, this.size).take(reflectingRows).reversed().flatten().map { it.value })
        if (reflectionPairs.count { it.first != it.second } == 1) {
            return rowNum
        }
    }
    return null
}

private fun List<List<Point>>.getReflectingRow(): Int? {
    for (rowNum in 1..<this.size) {
        val reflectingRows = min(rowNum, this.size - rowNum)
        if (this.subList(0, rowNum).takeLast(reflectingRows).map { it.map { it.value } } ==
            this.subList(rowNum, this.size).take(reflectingRows).reversed().map { it.map { it.value } }
        ) {
            return rowNum
        }
    }
    return null
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

package advent2017.day10

import lib.runPuzzle
import java.io.File

const val day = 10
val file = File("src/main/resources/advent2017/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val lengths = input[0].split(",").map { it.toInt() }
        val list = (0..255).associateWith { it }.toMutableMap()
        val listSize = list.size
        var currentPosition = 0
        var skipSize = 0
        for (length in lengths) {
            val selectionIndices = (currentPosition..(currentPosition + length - 1)).map { it % listSize }
            val reversedSelection = selectionIndices.map { list.getValue(it) }.reversed()
            selectionIndices.forEachIndexed { index, selectionIndex -> list[selectionIndex] = reversedSelection[index] }
            currentPosition += length + skipSize++
        }
        println(list.getValue(0) * list.getValue(1))
    }

    fun runPart2() {
        val lengths = input[0].toList().map { it.code } + listOf(17, 31, 73, 47, 23)
        val rounds = 64

        val list = (0..255).associateWith { it }.toMutableMap()
        val listSize = list.size
        var currentPosition = 0
        var skipSize = 0
        for (i in 1..rounds) {
            for (length in lengths) {
                val selectionIndices = (currentPosition..(currentPosition + length - 1)).map { it % listSize }
                val reversedSelection = selectionIndices.map { list.getValue(it) }.reversed()
                selectionIndices.forEachIndexed { index, selectionIndex ->
                    list[selectionIndex] = reversedSelection[index]
                }
                currentPosition += length + skipSize++
            }
        }

        val sparseHash = list.values.chunked(16) { it.sparseHash() }
        val hex = sparseHash.joinToString(separator = "") { it.toHexString().takeLast(2) }
        println(hex)
    }
}

private fun List<Int>.sparseHash(): Int =
    this.reduce { acc, i -> acc.xor(i) }

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
package advent2025.day9

import lib.Point
import lib.runPuzzle
import lib.subListTillEnd
import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

const val day = 9
val file = File("src/main/resources/advent2025/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val redTiles = input.map { it.split(",") }.map { Point(it[0], it[1]) }
        val pairs = redTiles.flatMapIndexed { idx, first -> 
            redTiles.subListTillEnd(idx + 1).map { second -> Pair(first, second) } }
        println(pairs.maxOf { it.square() })
    }

    fun runPart2() {
        val redTiles = input.map { it.split(",") }.map { Point(it[0], it[1]) }
        val sortedPairs = redTiles.flatMapIndexed { idx, first ->
            redTiles.subListTillEnd(idx + 1).map { second -> Pair(first, second) } }.sortedByDescending { it.square() }
        val largestSquareWithOnlyRedAndGreenTiles = sortedPairs.first { it.hasOnlyRedAndGreenTiles(redTiles.zipWithNext() + Pair(redTiles.last(), redTiles.first())) }
        println(largestSquareWithOnlyRedAndGreenTiles.square())
    }
}

private fun Pair<Point, Point>.hasOnlyRedAndGreenTiles(lines: List<Pair<Point, Point>>): Boolean {
    // if there is a line in between, there will be uncolored tiles
    val (first, second) = this
    val minX = min(first.x, second.x)
    val minY = min(first.y, second.y)
    val maxX = max(first.x, second.x)
    val maxY = max(first.y, second.y)
    
    val horizontalLines = lines.filter { it.first.y == it.second.y }.map { if (it.first.x < it.second.x) Pair(it.first, it.second) else Pair(it.second, it.first) }
    val verticalLines = lines.filter { it.first.x == it.second.x }.map { if (it.first.y < it.second.y) Pair(it.first, it.second) else Pair(it.second, it.first) }
    val crossingHorizontalLine = horizontalLines.any { it.first.y in (minY + 1)..<maxY &&
            ((it.first.x <= minX && it.second.x > minX) || (it.first.x < maxX && it.second.x >= maxX))
    }
    val crossingVerticalLine = verticalLines.any { it.first.x in (minX + 1)..<maxX &&
            ((it.first.y <= minY && it.second.y > minY) || (it.first.y < maxY && it.second.y >= maxY))
    }
    return !crossingHorizontalLine && !crossingVerticalLine
}

private fun Pair<Point, Point>.square(): Long =
    ((first.x - second.x).absoluteValue + 1).toLong() * ((first.y - second.y).absoluteValue + 1).toLong() 


fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
package advent2024.day4

import lib.Point
import lib.allLinesInGrid
import lib.getAllThreeByThreeSquares
import lib.runPuzzle
import lib.to2DGridOfPointsWithValues
import lib.valuesAsString
import java.io.File

const val day = 4
val file = File("src/main/resources/advent2024/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val rgx = Regex("XMAS")
        val allLines = input.to2DGridOfPointsWithValues()
            .allLinesInGrid().map { it.valuesAsString() }
        val matches = allLines.sumOf { rgx.findAll(it).toList().size }
        println(matches)
    }

    fun runPart2() {
        val squares = input.to2DGridOfPointsWithValues().getAllThreeByThreeSquares()
        println(squares.count { it.isXmasSquare() })
    }
}

val firstDiagonal = listOf(Pair(0,0), Pair(1,1), Pair(2,2))
val secondDiagonal = listOf(Pair(0,2), Pair(1,1), Pair(2,0))

private fun List<List<Point>>.isXmasSquare(): Boolean {
    val first = firstDiagonal.map { this[it.first][it.second] }.valuesAsString()
    val second = secondDiagonal.map { this[it.first][it.second] }.valuesAsString()
    return (first == "MAS" || first == "SAM") && (second == "MAS" || second == "SAM") 
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
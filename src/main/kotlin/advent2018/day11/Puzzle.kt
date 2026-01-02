package advent2018.day11

import lib.*
import java.io.File

const val day = 11
val file = File("src/main/resources/advent2018/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val gridSerial = 4842
        val grid = initEmptyGrid(1, 300, 1, 300)
        val squares = grid.getAllThreeByThreeSquares()
        val squareWithMaxPower = squares.maxBy { it.allPoints().sumOf { it.powerLevel(gridSerial) } }
        println(squareWithMaxPower[0][0])
    }

    fun runPart2() {
        val gridSerial = 4842
        val grid = initEmptyGrid(1, 300, 1, 300)
        val powerGrid = grid.map { it.map { it.powerLevel(gridSerial) } }
        var maxPower = 0
        for (sq in 1..300) {
            println("checking squares with $sq")
            for (x in 1..300) {
                for (y in 1..300) {
                    var powerSquare = 0
                    if (x + sq > 300 || y + sq > 300) continue
                    for (xsq in x..<(x + sq)) {
                        for (ysq in y..<(y + sq)) {
                            powerSquare += powerGrid[ysq][xsq]
                        }
                    }
                    if (powerSquare > maxPower) {
                        println("found max power ${powerSquare} for $x,$y with square size $sq")
                        maxPower = powerSquare
                    }
                }
            }
        }
    }
}

private fun Point.powerLevel(gridSerial: Int): Int {
    val rackId = this.x + 10 
    return (((rackId * this.y + gridSerial) * rackId) / 100) % 10 - 5
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
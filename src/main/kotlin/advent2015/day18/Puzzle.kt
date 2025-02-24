package advent2015.day18

import lib.*
import java.io.File

const val day = 18
val file = File("src/main/resources/advent2015/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        var grid = input.to2DGridOfPointsWithValues()
        val steps = 100
        for (i in 1..steps) {
            val newGrid = grid.copy()
            newGrid.allPoints().forEach { 
                val neighboursOn = grid.getAdjacentNeighbours(grid.getPoint(it.x, it.y)!!)
                    .neighbours.count { it.value == '#' }
                when {
                    it.value == '#' -> {
                        if (neighboursOn != 2 && neighboursOn != 3) {
                            it.value = '.'
                        }
                    }
                    else -> {
                        if (neighboursOn == 3) {
                            it.value = '#'
                        }
                    }
                }
            }
            grid = newGrid
            grid.printV()
        }
        println(grid.allPoints().count { it.value == '#' })
    }

    fun runPart2() {
        var grid = input.to2DGridOfPointsWithValues()
        grid.allPoints().filter { grid.isCorner(it) }.forEach { it.value = '#' }
        val steps = 100
        for (i in 1..steps) {
            val newGrid = grid.copy()
            newGrid.allPoints().forEach {
                val neighboursOn = grid.getAdjacentNeighbours(grid.getPoint(it.x, it.y)!!)
                    .neighbours.count { it.value == '#' }
                when {
                    grid.isCorner(it) -> {
                        it.value = '#'
                    }
                    it.value == '#' -> {
                        if (neighboursOn != 2 && neighboursOn != 3) {
                            it.value = '.'
                        }
                    }
                    else -> {
                        if (neighboursOn == 3) {
                            it.value = '#'
                        }
                    }
                }
            }
            grid = newGrid
            grid.printV()
        }
        println(grid.allPoints().count { it.value == '#' })
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
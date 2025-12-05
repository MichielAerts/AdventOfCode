package advent2025.day4

import lib.*
import java.io.File

const val day = 4
val file = File("src/main/resources/advent2025/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val warehouse = input.to2DGridOfPointsWithValues()
        println(warehouse.allPoints().filter { it.value == '@' }
            .count { warehouse.lessThanFourRollsInAdjacentPositionsTo(it) })
    }

    fun runPart2() {
        val warehouse = input.to2DGridOfPointsWithValues()
        val initialNumberOfRolls = warehouse.countRolls()
        var currentNumberOfRolls: Int
        do {
            currentNumberOfRolls = warehouse.countRolls()
            val rollsToBeRemoved = warehouse.allPoints().filter { it.value == '@' }
                .filter { warehouse.lessThanFourRollsInAdjacentPositionsTo(it) }.toSet()
            warehouse.changePoints(rollsToBeRemoved, '.')
            val newNumberOfRolls = warehouse.countRolls()
        } while (newNumberOfRolls != currentNumberOfRolls)
        println("removed: ${initialNumberOfRolls - currentNumberOfRolls}")
    }
}

private fun List<List<Point>>.countRolls(): Int = allPoints().count { it.value == '@' }

private fun List<List<Point>>.lessThanFourRollsInAdjacentPositionsTo(p: Point): Boolean {
    return this.getAdjacentNeighbours(p).neighbours.count { it.value == '@' } < 4
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
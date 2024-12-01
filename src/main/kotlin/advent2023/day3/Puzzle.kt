package advent2023.day3

import advent2023.day3.PartNumber.Companion.toPartNumber
import lib.*
import java.io.File

const val day = 3
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val engineSchematic = input.to2DGridOfPointsWithValues()
        val numbers = engineSchematic.findNumbers()
        println(
            numbers.filter { it.isPartNumber(engineSchematic) }
                .sumOf { it.number }
        )
    }
    
    fun runPart2() {
        val engineSchematic = input.to2DGridOfPointsWithValues()
        val numbers = engineSchematic.findNumbers()

        println(
            engineSchematic.flatten()
                .filter { it.value == '*' }
                .mapNotNull { it.toGearOrNull(numbers, engineSchematic) }
                .sumOf { gear -> gear.adjacentNumbers.map { it.number }.product() }
        )      
    }
}

data class Gear(val point: Point, val adjacentNumbers: List<PartNumber>)
private fun Point.toGearOrNull(numbers: List<PartNumber>, engineSchematic: List<List<Point>>): Gear? {
    val neighbours = engineSchematic.getAdjacentNeighbours(this).neighbours
    val adjacentNumbers = numbers.filter { number -> number.points.any { it in neighbours } }
    return if (adjacentNumbers.size == 2) Gear(this, adjacentNumbers) else null
}

private fun List<List<Point>>.findNumbers(): List<PartNumber> =
    this.flatMap { row -> row.splitBy { !it.value.isDigit() }.filter { it.isNotEmpty() } }
        .map { toPartNumber(it) }

data class PartNumber(val number: Int, val points: List<Point>) {
    fun isPartNumber(engineSchematic: List<List<Point>>): Boolean =
        points.any { point ->
            engineSchematic.getAdjacentNeighbours(point).neighbours
                .any { !it.value.isDigit() && it.value != '.' } 
        }
    
    companion object {
        fun toPartNumber(points: List<Point>): PartNumber {
            val number = points.joinToString(separator = "", transform = { it.value.toString() }).toInt()
            return PartNumber(number, points)
        }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

package advent2018.day6

import lib.Point
import lib.allPoints
import lib.initEmptyGrid
import lib.runPuzzle
import java.io.File

const val day = 6
val file = File("src/main/resources/advent2018/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val coordinates = input.map { it.split(", ") }.map { Point(it[0], it[1]) }
        val maxX = coordinates.maxOf { it.x }
        val maxY = coordinates.maxOf { it.y }
        val grid = initEmptyGrid(endX = maxX + 1, endY = maxY + 1)
        val map = coordinates.associateWith { mutableListOf<Point>() }.toMutableMap()
        grid.allPoints().forEach { p ->
            val closestDistance = coordinates.minOf { coor -> coor.getManhattanDistance(p) }
            val closestCoordinate = coordinates.filter { coor -> coor.getManhattanDistance(p) == closestDistance }
            if (closestCoordinate.size == 1) {
                map[closestCoordinate[0]]!!.add(p)
            }
        }
        val result = map.filterNot { it.value.any { it.x == 0 || it.x == maxX + 1 || it.y == 0 || it.y == maxY + 1 } }
            .entries.maxByOrNull { it.value.size }!!
        println(result.value.size)
    }

    fun runPart2() {
        val coordinates = input.map { it.split(", ") }.map { Point(it[0], it[1]) }
        val maxX = coordinates.maxOf { it.x }
        val maxY = coordinates.maxOf { it.y }
        val grid = initEmptyGrid(endX = maxX + 1, endY = maxY + 1)
        val maxDistance = 10000
        val region = mutableListOf<Point>()
        grid.allPoints().forEach { p ->
            val totalDistance = coordinates.sumOf { coor -> coor.getManhattanDistance(p) }
            if (totalDistance < maxDistance) region += p
        }
        println(region.size)
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
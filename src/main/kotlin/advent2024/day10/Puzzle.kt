package advent2024.day10

import lib.*
import java.io.File

const val day = 10
val file = File("src/main/resources/advent2024/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val map = input.to2DGridOfPointsWithValues()
        val trailheads = map.findAllValuesInGrid('0')
        println(trailheads.sumOf { map.findTopsFrom(it) })
    }

    fun runPart2() {
        val map = input.to2DGridOfPointsWithValues()
        val trailheads = map.findAllValuesInGrid('0')
        println(trailheads.sumOf { map.findTrailsFrom(it) })
    }
}

private fun List<List<Point>>.findTrailsFrom(start: Point): Int {
    var count = 0
    val queue = ArrayDeque(listOf(start))

    while(queue.isNotEmpty()) {
        val point = queue.removeFirst()
        val value = point.value.digitToInt()
        if (value == 9) count++
        val newPoints = getDirectNeighbours(point).neighbours
            .filter { it.value != '.' && it.value.digitToInt() == value + 1 }
        queue.addAll(newPoints)
        println(queue)
    }
    return count
}

private fun List<List<Point>>.findTopsFrom(start: Point): Int {
    val queue = ArrayDeque(listOf(start))
    val tops = mutableSetOf<Point>()
    
    while(queue.isNotEmpty()) {
        val point = queue.removeFirst()
        val value = point.value.digitToInt()
        if (value == 9) tops.add(point)
        val newPoints = getDirectNeighbours(point).neighbours
            .filter { it.value != '.' && it.value.digitToInt() == value + 1 }
        queue.addAll(newPoints.filter { !queue.contains(it)  })
        println(queue)
    }
    return tops.size
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
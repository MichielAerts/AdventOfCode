package advent2017.day14

import advent2017.day10.knotHash
import lib.*
import java.io.File

const val day = 14
val file = File("src/main/resources/advent2017/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val key = "stpzcrnm"
        
        val inputs = (0..127).map { "$key-$it" }
            .map { knotHash(it) }
            .map { it.hexToBinary() }
            .to2DGridOfPointsWithValues()
            .groupIntoRegions()
        
        println(inputs.size)
    }
    
    fun runPart2() {
        println(input)
    }
}

private fun List<List<Point>>.groupIntoRegions(): List<Set<Point>> {
    val regions = mutableListOf<Set<Point>>()
    val coveredPoints = mutableSetOf<Point>()
    for (point in allPoints()) {
        if (point in coveredPoints || point.value == '0') continue
        val currentRegion = mutableSetOf(point)
        val queue = ArrayDeque(currentRegion)
        while (queue.isNotEmpty()) {
            val currentPoint = queue.removeFirst()
            val neighbours = getDirectNeighbours(currentPoint).neighbours
                .filter { it.value == '1' }
            neighbours.forEach { neighbour ->
                if (neighbour !in currentRegion) {
                    currentRegion += neighbour
                    queue.add(neighbour)
                }
            }
        }
        regions += currentRegion
        coveredPoints.addAll(currentRegion)
    }
    return regions
}


fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
package advent2024.day12

import lib.*
import lib.WindDirection.*
import java.io.File

const val day = 12
val file = File("src/main/resources/advent2024/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val regions = input.to2DGridOfPointsWithValues()
            .groupIntoRegions()
        println(regions.sumOf { it.fencePrice() })
    }

    fun runPart2() {
        val regions = input.to2DGridOfPointsWithValues()
            .groupIntoRegions()
        println(regions.sumOf { it.fencePriceWithBulkDiscount() })
    }
}

private fun List<List<Point>>.groupIntoRegions(): List<Set<Point>> {
    val regions = mutableListOf<Set<Point>>()
    val coveredPoints = mutableSetOf<Point>()
    for (point in allPoints()) {
        if (point in coveredPoints) continue
        val plantType = point.value
        val currentRegion = mutableSetOf<Point>(point) 
        val queue = ArrayDeque<Point>(currentRegion)
        while (queue.isNotEmpty()) {
            val currentPoint = queue.removeFirst()
            val neighbours = getDirectNeighbours(currentPoint).neighbours
                .filter { it.value == plantType }
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

private fun Set<Point>.fencePrice(): Int {
    //area * perimeter
    val area = size
    val perimeter = size * 4 - this.sumOf { p -> this.count { p.touchesDirectly(it) } }
    return area * perimeter
}

private fun Set<Point>.fencePriceWithBulkDiscount(): Int {
    //area * number of sides
    val area = size
    val allSides = this.flatMap { p -> listOf(N, S, E, W).map { Pair(p, it)} }
    val perimeter = allSides.filterNot { hasPointInDirection(it.first, it.second) }
    //all sides minus number of connected sides
    val numberOfSides = perimeter.filterNot { perimeter.hasPreviousSide(it) }
    println("${numberOfSides.size} & $numberOfSides")
    return area * numberOfSides.size
}

private fun List<Pair<Point, WindDirection>>.hasPreviousSide(side: Pair<Point, WindDirection>): Boolean {
    val (currentPoint, direction) = side
    if (direction in listOf(N, S)) {
        return any { it.first.x == currentPoint.x - 1 && it.first.y == currentPoint.y && it.second == direction}
    }
    return any { it.first.y == currentPoint.y - 1 && it.first.x == currentPoint.x && it.second == direction}
}


fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
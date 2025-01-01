package advent2024.day20

import lib.*
import java.io.File

const val day = 20
val file = File("src/main/resources/advent2024/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val map = input.to2DGridOfPointsWithValues()

        val basicPath = findPath(map)
        val timeOfBasicPath = basicPath.size - 1
        println(timeOfBasicPath)
        val cheats = mutableMapOf<Pair<Point, Point>, Int>()

        // for every point on path, check if there are points (further on the path) 
        // that can be reached by cheating (.#.) in a direction. Check path length and store
        for ((idx, point) in basicPath.withIndex()) {
            for (option in Direction.all()
                .map { map.getXPointsInDirection(point, it, 2) }
                .filter { it.valuesAsString() == "#." || it.valuesAsString() == "#E" }) {
                val idxPointAfterCheat = basicPath.indexOf(option.last())
                if (idxPointAfterCheat > idx) {
                    val timeOfCheatPath = idx + (timeOfBasicPath - idxPointAfterCheat) + 2
                    cheats[Pair(point, option.last())] = timeOfCheatPath
                }
            }
        }
        val cheatsResults = cheats.values.groupingBy({ timeOfBasicPath - it }).eachCount().toSortedMap()
        println(cheatsResults.filter { it.key >= 100 }.values.sum())
    }

    fun runPart2() {
        val map = input.to2DGridOfPointsWithValues()

        val basicPath = findPath(map)
        val timeOfBasicPath = basicPath.size - 1
        val cheats = mutableMapOf<Pair<Point, Point>, Int>()
        
        for ((idx, point) in basicPath.withIndex()) {
            for (option in basicPath
                .subListTillEnd(idx + 1)
                .filter { point.getManhattanDistance(it) <= 20 }
            ) {
                val idxPointAfterCheat = basicPath.indexOf(option)
                val timeOfCheatPath = idx + (timeOfBasicPath - idxPointAfterCheat) + point.getManhattanDistance(option)
                cheats[Pair(point, option)] = timeOfCheatPath
            }
        }
        val cheatsResults = cheats.values.groupingBy({ timeOfBasicPath - it }).eachCount().toSortedMap()
//        println(cheatsResults.filter { it.key >= 50 })
        println(cheatsResults.filter { it.key >= 100 }.values.sum())
    }

    private fun findPath(map: List<List<Point>>): MutableList<Point> {
        val start = map.findSingleValueInGrid('S')
        val end = map.findSingleValueInGrid('E')
        var currentPoint = start
        val currentPath = mutableListOf(currentPoint)
        while (currentPoint != end) {
            val nextPoint = map.getDirectNeighbours(currentPoint).neighbours
                .find { it.value != '#' && it !in currentPath }!!
            currentPath += nextPoint
            currentPoint = nextPoint
//            println(currentPath)
        }
        return currentPath
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
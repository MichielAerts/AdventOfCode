package advent2025.day7

import lib.*
import java.io.File

const val day = 7
val file = File("src/main/resources/advent2025/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val diagram = input.to2DGridOfPointsWithValues()
        for (l in 0..<diagram.size - 1) {
            diagram.propagateBeam(l)
            diagram.printV()
        }
        println(splitters)
    }

    fun runPart2() {
        val diagram = input.to2DGridOfPointsWithValues()
        var beamEnds = mapOf(diagram.findSingleValueInGrid('S') to 1L)
        for (l in 1..<diagram.size - 1) {
            beamEnds = diagram.propagateTimelines(beamEnds)
        }
        println(beamEnds.entries.sumOf { it.value })
    }
}

private fun List<List<Point>>.propagateTimelines(currentBeamEnds: Map<Point, Long>): Map<Point, Long> {
    val newBeamEnds = mutableMapOf<Point, Long>()
    currentBeamEnds.forEach { (currentEnd, numberOfTimelines) ->
        val downWardPoint = this.getPointAfterMoveSure(currentEnd, Direction.DOWN)
        when (downWardPoint.value) {
            '.' -> newBeamEnds.merge(downWardPoint, numberOfTimelines, Long::plus)
            '^' -> {
                val left = this.getPointAfterMoveSure(downWardPoint, Direction.LEFT)
                val right = this.getPointAfterMoveSure(downWardPoint, Direction.RIGHT)
                newBeamEnds.merge(left, numberOfTimelines, Long::plus)
                newBeamEnds.merge(right, numberOfTimelines, Long::plus)
            }
            else -> throw IllegalArgumentException("Unexpected Field")
        }
    }
    this.changePoints(newBeamEnds.keys.toSet(), '|')
    return newBeamEnds
}


var splitters = 0
private fun List<List<Point>>.propagateBeam(currentDepth: Int) {
    val currentBeamEnds = this[currentDepth].filter { it.value == '|' || it.value == 'S' }
    val newBeamEnds = currentBeamEnds.flatMap { currentEnd ->
        val downWardPoint = this.getPointAfterMoveSure(currentEnd, Direction.DOWN)
        when (downWardPoint.value) {
            '.' -> listOf(downWardPoint)
            '^' -> {
                splitters++
                listOf(
                    this.getPointAfterMoveSure(downWardPoint, Direction.LEFT),
                    this.getPointAfterMoveSure(downWardPoint, Direction.RIGHT)
                )
            }
            else -> throw IllegalArgumentException("Unexpected Field")
        }
    }.toSet()
    this.changePoints(newBeamEnds, '|')
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
package advent2018.day22

import advent2018.day22.EquippedTools.*
import lib.*
import java.io.File
import java.util.*

const val day = 22
val file = File("src/main/resources/advent2018/day${day}/input")

private const val ROCKY = '.'
private const val WET = '='
private const val NARROW = '|'

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val buffer = 0
        val depth = 11991
        val target = Pos(6, 797)
        val cave = initEmptyGrid(endX = target.x + buffer, endY = target.y + buffer)
        cave.allPoints().forEach { p -> 
            val geologicalIndex = p.geologicalIndex(target, cave)
            val erosionLevel = (geologicalIndex + depth) % 20183
            p.z = erosionLevel
            p.value = p.type()
        }
        cave.printV()
        println(cave.allPoints().sumOf { it.z % 3 })
    }

    fun runPart2() {
        val buffer = 50
        val depth = 11991
        val targetPos = Pos(6, 797)
        val cave = initEmptyGrid(endX = targetPos.x + buffer, endY = targetPos.y + buffer)
        val mouth = cave.getPoint(0, 0)!!
        val target = cave.getPoint(targetPos)!!
        cave.allPoints().forEach { p ->
            val geologicalIndex = p.geologicalIndex(targetPos, cave)
            val erosionLevel = (geologicalIndex + depth) % 20183
            p.z = erosionLevel
            p.value = p.type()
        }
        val (time, path) = cave.shortestTimeBetween(
            Climber(mouth, TORCH),
            Climber(target, TORCH)
        )
        println(time)
    }
    
    private fun Point.geologicalIndex(
        target: Pos,
        cave: List<List<Point>>
    ): Int = when {
        x == 0 && y == 0 -> 0
        x == target.x && y == target.y -> 0
        y == 0 -> x * 16807
        x == 0 -> y * 48271
        else -> {
            val left = cave.getPointInDirectionSure(this, Direction.LEFT)
            val above = cave.getPointInDirectionSure(this, Direction.UP)
            left.z * above.z
        }
    }
}

private fun Point.type() = when (this.z % 3) {
    0 -> ROCKY
    1 -> WET
    2 -> NARROW
    else -> throw IllegalArgumentException()
}

private fun Point.allowedTools() = when (this.type()) {
    ROCKY -> listOf(TORCH, CLIMBING_GEAR)
    WET -> listOf(CLIMBING_GEAR, NEITHER)
    NARROW -> listOf(TORCH, NEITHER)
    else -> throw IllegalArgumentException()
}

enum class EquippedTools {
    TORCH, CLIMBING_GEAR, NEITHER;
}

data class Climber(val point: Point, val equippedTools: EquippedTools)

private fun List<List<Point>>.shortestTimeBetween(source: Climber, target: Climber): Pair<Int, List<Climber>> {
    val climbersAndMinimumTimes = mutableMapOf<Climber, Int>().withDefault { Int.MAX_VALUE }
    val previous = mutableMapOf<Climber, Climber>()

    val pq = PriorityQueue<Pair<Climber, Int>>(compareBy { it.second })
    pq.add(source to 0)

    while(pq.isNotEmpty()) {
        val (climber, timeSpent) = pq.remove()
        for (neighbour in this.getDirectNeighbours(climber.point).neighbours) {
            val nextClimbersPlusTime = neighbour.allowedTools()
                .map { Climber(neighbour, it) to if (it == climber.equippedTools) 1 else 8 }
            for ((nextClimber, timeForMove) in nextClimbersPlusTime) {
                val currentMinimumTime = climbersAndMinimumTimes.getValue(nextClimber)
                val newSpentTime = timeSpent + timeForMove
                if (newSpentTime < currentMinimumTime) {
                    climbersAndMinimumTimes[nextClimber] = newSpentTime
                    pq.add(nextClimber to newSpentTime)
                    previous[nextClimber] = climber
                }
            }
        }
    }
    val shortestTime = climbersAndMinimumTimes.getValue(target)
    val path = mutableListOf(target)
    var currentClimber = target
    while (currentClimber != source) {
        val previousClimber = previous.getValue(currentClimber)
        path += previousClimber
        currentClimber = previousClimber
    }
    return shortestTime to path.reversed()
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
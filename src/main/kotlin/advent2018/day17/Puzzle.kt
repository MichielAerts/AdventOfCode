package advent2018.day17

import lib.*
import lib.Direction.*
import java.io.File

const val day = 17
val file = File("src/main/resources/advent2018/day${day}/input")

private const val EMPTY = '.'
private const val SPRING = '+'
private const val REACHABLE_BY_WATER = '|'
private const val WATER_AT_REST = '~'
private const val CLAY = '#'

class Puzzle(private val input: List<String>) {

    fun runPart1() {
        var clay = input.map { Vein.toVein(it) }.flatMap { it.points }
        val minX = clay.minOf { it.x }
        clay = clay.map { it.copy(x = it.x - minX + 1) }
        val springPos = Pos(500 - minX + 1, 0)

        val maxX = clay.maxOf { it.x }
        val minY = clay.minOf { it.y }
        val maxY = clay.maxOf { it.y }
        val ground = initEmptyGrid(startX = 0, endX = maxX + 1, startY = 0, endY = maxY + 1)
        ground.changePoints(clay.toSet(), CLAY)
        ground.changePoint(springPos.x, springPos.y, SPRING)
        val spring = ground.getPoint(springPos)!!
        
        val ends = ArrayDeque(listOf(spring))
        while (ends.isNotEmpty()) {
            val currentEnd = ends.removeFirst()
            if (currentEnd.value == SPRING || currentEnd.value == REACHABLE_BY_WATER) {
                val below = ground.getPointInDirection(currentEnd, DOWN) ?: continue
                if (below.value == EMPTY) {
                    below.value = REACHABLE_BY_WATER
                    ends.add(below)
                    continue
                }
                if (below.value == CLAY || below.value == WATER_AT_REST) {
                    val left = ground.getPointInDirection(currentEnd, LEFT)
                    val right = ground.getPointInDirection(currentEnd, RIGHT)
                    for ((neighbour, direction) in listOf(Pair(left, LEFT), Pair(right, RIGHT))) {
                        if (neighbour?.value == EMPTY) {
                            neighbour.value = REACHABLE_BY_WATER
                            ends.addFirst(neighbour)
                        } else if (neighbour?.value == CLAY || neighbour?.value == REACHABLE_BY_WATER) {
                            
                            val pointsOnClay = if (neighbour.value == CLAY) {
                                ground.getViewFrom(neighbour, direction.opposite()).takeUntil { it.value == CLAY }
                            } else {
                                ground.getViewFrom(currentEnd, direction).takeUntil { it.value == CLAY } +
                                        ground.getViewFrom(neighbour, direction.opposite()).takeUntil { it.value == CLAY }
                            }
                            if (pointsOnClay.all { it.value == REACHABLE_BY_WATER }) {
                                ground.changePoints(pointsOnClay.toSet(), WATER_AT_REST)
                                ends.addAll(pointsOnClay.map { ground.getPointInDirectionSure(it, UP) }.filter { it.value == REACHABLE_BY_WATER })
                            }
                        }
                    }
                }
            }
        }
        ground.printV()
        println(ground.allPoints().count { it.y in minY..maxY && it.value in listOf(REACHABLE_BY_WATER, WATER_AT_REST) })
        println(ground.allPoints().count { it.y in minY..maxY && it.value == REACHABLE_BY_WATER })
        println(ground.allPoints().count { it.y in minY..maxY && it.value == WATER_AT_REST })
    }

    fun runPart2() {
    }
}

data class Vein(val points: List<Point>) {
    companion object {
        val xFirst = Regex("x=(\\d+), y=(\\d+)..(\\d+)")
        val yFirst = Regex("y=(\\d+), x=(\\d+)..(\\d+)")
        fun toVein(input: String): Vein {
            //x=495, y=2..7
            //y=7, x=495..501
            val points = when {
                xFirst.matches(input) -> {
                    val (x, y1, y2) = xFirst.allGroups(input).map { it.toInt() }
                    require(y1 < y2)
                    (y1..y2).map { y -> Point(x, y) }
                }
                yFirst.matches(input) -> {
                    val (y, x1, x2) = yFirst.allGroups(input).map { it.toInt() }
                    require(x1 < x2)
                    (x1..x2).map { x -> Point(x, y) }
                }
                else -> throw IllegalStateException()
            }
            return Vein(points)
        }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
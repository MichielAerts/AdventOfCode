package advent2018.day20

import lib.Direction
import lib.Direction.UP
import lib.Point
import lib.runPuzzle
import java.io.File
import kotlin.math.min

const val day = 20
val file = File("src/main/resources/advent2018/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val regex = input[0]
        val start = Node(Point(0, 0))
        var currentPoint = start
        var currentBranchPoint = start
        var index = 1
        while (index < (regex.length - 1)) {
            val c = regex[index]
            when (c) {
                'N', 'S', 'W', 'E' -> {
                    val nextPoint = Node(currentPoint.point.next(c))
                    currentPoint.connections += nextPoint
                    nextPoint.previousBranchingPoint = currentBranchPoint
                    nextPoint.previousPoint = currentPoint
                    currentPoint = nextPoint
                }
                '(' -> {
                    currentBranchPoint = currentPoint
                }
                '|' -> {
                    while (currentPoint != currentBranchPoint) {
                        currentPoint = currentPoint.previousPoint
                    }
                }
                ')' -> {
                    currentBranchPoint = currentBranchPoint.previousBranchingPoint
                }
            }
            index++
        }
        val shortestPaths = mutableMapOf<Point, Int>()
        val queue = ArrayDeque<Pair<Node, Int>>()
        queue.add(Pair(start, 0))
        
        while (queue.isNotEmpty()) {
            val (currentEnd, currentLength) = queue.removeFirst()
            for (connection in currentEnd.connections) {
                val connectionLength = currentLength + 1
                queue.add(Pair(connection, connectionLength))
                shortestPaths.merge(connection.point, connectionLength) { a, b -> min(a, b) }
            }
        }
        println(shortestPaths.maxBy { it.value })
        println(shortestPaths.count { it.value >= 1000 })
    }

    fun runPart2() {
    }
}

private fun Point.next(c: Char) = when(c) {
    'N' -> next(UP)
    'S' -> next(Direction.DOWN)
    'E' -> next(Direction.RIGHT)
    'W' -> next(Direction.LEFT)
    else -> throw UnsupportedOperationException()
}

class Node(
    val point: Point,
    val connections: MutableList<Node> = mutableListOf(),
) {
    lateinit var previousPoint: Node
    lateinit var previousBranchingPoint: Node
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
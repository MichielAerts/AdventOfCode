package advent2024.day15

import lib.*
import lib.Direction.*
import java.io.File

const val day = 15
val file = File("src/main/resources/advent2024/day${day}/input")

class Puzzle(private val input: List<String>) {
    
    val dir = mapOf('<' to LEFT, '>' to RIGHT, '^' to UP, 'v' to DOWN)
    
    fun runPart1() {
        val (map, moves) = input.splitBy { it.isEmpty() }.mapToPair(
            transformLeft = { it.to2DGridOfPointsWithValues() },
            transformRight = { it.joinToString("") }
        )
        for (c in moves) {
            val move = dir[c]!!
            println("new move: $move")
            val robot = map.findSingleValueInGrid('@')
            val nextPoint = map.getPointAfterMove(robot, move)
            when(nextPoint!!.value) {
                '.' -> {
                    nextPoint.value = '@'
                    robot.value = '.'
                }
                '#' -> {}
                'O' -> {
                    var currentPoint = nextPoint
                    var lastPoint: Point
                    do {
                       lastPoint = map.getPointAfterMoveSure(currentPoint!!, move) 
                       currentPoint = lastPoint 
                    } while (lastPoint.value == 'O')
                    when(lastPoint.value) {
                        '.' -> {
                            lastPoint.value = 'O'
                            nextPoint.value = '@'
                            robot.value = '.'
                        }
                        '#' -> {}
                    }
                }
            }
            map.printV()
        }
        println(map.findAllValuesInGrid('O').sumOf { it.x + 100 * it.y })
    }

    fun runPart2() {
        val (map, moves) = input.splitBy { it.isEmpty() }.mapToPair(
            transformLeft = { list -> list.map { line -> line.map { c -> widen(c) }.joinToString("") }.to2DGridOfPointsWithValues() },
            transformRight = { it.joinToString("") }
        )
        for ((idx, c) in moves.withIndex()) {
            val move = dir[c]!!
            println("new move $idx: $move")
            val robot = map.findSingleValueInGrid('@')
            val nextPoint = map.getPointAfterMove(robot, move)
            when(nextPoint!!.value) {
                '.' -> {
                    nextPoint.value = '@'
                    robot.value = '.'
                }
                '#' -> {}
                '[', ']' -> {
                    // direction left, right, similar logic as before
                    when(move) {
                        RIGHT, LEFT -> {
                            val pointsToChange = mutableListOf<Point>()
                            var currentPoint = nextPoint
                            var lastPoint: Point
                            do {
                                lastPoint = map.getPointAfterMove(currentPoint!!, move)!!
                                pointsToChange.add(lastPoint)
                                currentPoint = lastPoint
                            } while (lastPoint.value == '[' || lastPoint.value == ']')
                            when(lastPoint.value) {
                                '.' -> {
                                    robot.value = '.'
                                    var edge = nextPoint.value
                                    nextPoint.value = '@'
                                    for ((idx, p) in pointsToChange.withIndex()) {
                                        p.value = if (idx % 2 == 0) edge else otherEdge(edge)                                        
                                    }
                                }
                                '#' -> {}
                                else -> throw IllegalStateException()
                            }       
                        }
                        UP, DOWN -> {
                            var currentRow = map.getPointsAfterUpOrDownMoveInclBoxes(robot, move)
                            val rowsToChange = mutableListOf(currentRow)
                            var lastRow: Set<Point>
                            var previousRow: Set<Point>
                            do {
                                lastRow = currentRow.flatMapTo(hashSetOf()) {
                                    map.getPointsAfterUpOrDownMoveInclBoxes(
                                        it,
                                        move
                                    )
                                }
                                rowsToChange.add(lastRow)
                                previousRow = currentRow
                                currentRow = lastRow
                            } while (lastRow.none { it.value == '#' } && !lastRow.all { it.value == '.' })
                            when {
                                lastRow.any { it.value == '#' } -> {}
                                lastRow.all { it.value == '.' } -> {
//                                    println("rows moved: ${rowsToChange.size}")
                                    robot.value = '.'
                                    var previousRow: Set<Point> = emptySet()
                                    for ((idx, row) in rowsToChange.withIndex()) {
                                        var currentRow = row.clone()
                                        if (idx == 0) {
                                            row.forEach { it.value = if (it.x == robot.x) '@' else '.' }
                                        } else {
                                            row.forEach {
                                                it.value =
                                                    previousRow.getPointInDirection(it, move.opposite())?.value ?: '.'
                                            }
                                        }
                                        previousRow = currentRow
                                    }
                                }

                                else -> throw IllegalStateException()
                            }
                        }
                    }
                }
            }
//            map.printV()
        }
        println(map.findAllValuesInGrid('[').sumOf { it.x + 100 * it.y })
    }

    fun otherEdge(c: Char) = when(c) {
        ']' -> '['
        '[' -> ']'
        else -> throw UnsupportedOperationException()
    }
    fun widen(c: Char): String = when(c) {
        '#' -> "##"
        'O' -> "[]"
        '.' -> ".."
        '@' -> "@."
        else -> throw UnsupportedOperationException()
    }
}

fun List<List<Point>>.getPointsAfterUpOrDownMoveInclBoxes(point: Point, move: Direction): Set<Point> {
    if (point.value == '.') return emptySet()
    val nextPoint = getPointAfterMove(point, move)
    if (nextPoint == null) return emptySet()
    return when (nextPoint.value) {
        '[' -> setOf(nextPoint, getPointAfterMoveSure(nextPoint, RIGHT))
        ']' -> setOf(nextPoint, getPointAfterMoveSure(nextPoint, LEFT))
        else -> setOf(nextPoint)
    }    
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
//    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
package advent2017.day19

import lib.*
import java.io.File

const val day = 19
val file = File("src/main/resources/advent2017/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val diagram = input.to2DGridOfPointsWithValues()
        val start = diagram.getRow(0).first { it.value == '|' }
        var direction = Direction.DOWN
        val path = mutableListOf(start)
        var currentPoint = start
        while(true) {
            val nextPoint = diagram.getPointAfterMoveSure(currentPoint, direction)
            when (nextPoint.value) {
                '+' -> {
                    val turnLeftPoint = diagram.getPointAfterMove(nextPoint, direction.turnLeft())
                    val turnRightPoint = diagram.getPointAfterMove(nextPoint, direction.turnRight())
                    if (turnLeftPoint != null && turnLeftPoint.value != ' ') {
                        direction = direction.turnLeft()
                    } else if (turnRightPoint != null && turnRightPoint.value != ' ') {
                        direction = direction.turnRight()
                    } else {
                        throw IllegalStateException("expected path")
                    }
                    currentPoint = nextPoint
                }
                ' ' -> {
                    break
                }
                else -> {
                    currentPoint = nextPoint
                }
            }
            path += currentPoint
//            println(path)
        }
        println(path.filter { it.value in 'A'..'Z' }.joinToString("") { it.value.toString() })
        println(path.size)
    }

    fun runPart2() {
        println(input)
    }
}

private fun Direction.matches(value: Char) =
    when(this) {
        Direction.UP -> value == '|'
        Direction.DOWN -> value == '|'
        Direction.RIGHT -> value == '-'
        Direction.LEFT -> value == '-'
    }

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
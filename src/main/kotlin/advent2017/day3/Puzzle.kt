package advent2017.day3

import lib.Direction.*
import lib.Point
import lib.getNextPos
import lib.runPuzzle
import java.io.File
import kotlin.math.absoluteValue

const val day = 3
val file = File("src/main/resources/advent2017/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val max = 312051
//        val max = input[0].toInt()
        val points = mutableListOf(Point(0, 0, 1),Point(1, 0, 2)) 
        var currentDirection = UP
        var switchAt = 1
        for (i in 3..max) {
            val nextPosition = points.last().pos().getNextPos(currentDirection) 
            val nextPoint = Point(nextPosition.x, nextPosition.y, i) 
            points += nextPoint
            when(currentDirection) {
                UP, LEFT, DOWN -> {
                    if (nextPoint.x.absoluteValue == switchAt && nextPoint.y.absoluteValue == switchAt) {
                        currentDirection = currentDirection.turnLeft()
                    } 
                }
                RIGHT -> if (nextPoint.x.absoluteValue == switchAt + 1 && nextPoint.y.absoluteValue == switchAt) {
                    currentDirection = currentDirection.turnLeft()
                    switchAt++                    
                }
            }
//            println(points)
        }
        println("manhatten distance of $max: ${points.last().x.absoluteValue + points.last().y.absoluteValue}")
    }

    fun runPart2() {
        val max = 312051
//        val max = 800
//        val max = input[0].toInt()
        val points = mutableListOf(Point(0, 0, 1),Point(1, 0, 1))
        var currentDirection = UP
        var switchAt = 1
        for (i in 3..max) {
            val nextPosition = points.last().pos().getNextPos(currentDirection)
            val value = points.filter { it.pos().isTouching(nextPosition)}.sumOf { it.z }
            if (value > max) {
                println("found value > $max: $value at $nextPosition")
                break
            }
            val nextPoint = Point(nextPosition.x, nextPosition.y, value)
            points += nextPoint
            when(currentDirection) {
                UP, LEFT, DOWN -> {
                    if (nextPoint.x.absoluteValue == switchAt && nextPoint.y.absoluteValue == switchAt) {
                        currentDirection = currentDirection.turnLeft()
                    }
                }
                RIGHT -> if (nextPoint.x.absoluteValue == switchAt + 1 && nextPoint.y.absoluteValue == switchAt) {
                    currentDirection = currentDirection.turnLeft()
                    switchAt++
                }
            }
//            println(points)
        }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
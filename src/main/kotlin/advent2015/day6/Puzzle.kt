package advent2015.day6

import advent2015.day6.Action.*
import lib.Point
import lib.getSquare
import lib.initEmptyGrid
import lib.runPuzzle
import java.io.File

const val day = 6
val file = File("src/main/resources/advent2015/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        // . is off, # is on
        val grid = initEmptyGrid(endX = 999, endY = 999)
        val instructions = input.map { Instruction.toInstruction(it) }
        for (instruction in instructions) {
            val (action, cornerOne, cornerTwo) = instruction
            val points = grid.getSquare(cornerOne.x, cornerTwo.x, cornerOne.y, cornerTwo.y).flatten()
            points.forEach { 
                when (action) {
                    ON -> it.value = '#'
                    TOGGLE -> it.value = if (it.value == '#') '.' else '#' 
                    OFF -> it.value = '.'
                }
            }
        }
        println(grid.flatten().count { it.value == '#' })
    }

    fun runPart2() {
        val grid = initEmptyGrid(endX = 999, endY = 999)
        val instructions = input.map { Instruction.toInstruction(it) }
        for (instruction in instructions) {
            val (action, cornerOne, cornerTwo) = instruction
            val points = grid.getSquare(cornerOne.x, cornerTwo.x, cornerOne.y, cornerTwo.y).flatten()
            points.forEach {
                when (action) {
                    ON -> it.z += 1
                    TOGGLE -> it.z += 2
                    OFF -> it.z -= if (it.z == 0) 0 else 1
                }
            }
        }
        println(grid.flatten().sumOf { it.z })
    }
}

data class Instruction(val action: Action, val cornerOne: Point, val cornerTwo: Point) {
    companion object {
        val rgx = "(.*) (\\d+,\\d+) through (\\d+,\\d+)".toRegex()
        
        fun toInstruction(input: String): Instruction {
            val (_, action, first, second) = rgx.find(input)?.groupValues!!
            return Instruction(
                action = when (action) {
                    "toggle" -> TOGGLE
                    "turn off" -> OFF
                    "turn on" -> ON
                    else -> throw UnsupportedOperationException()
                },
                cornerOne = Point(first.split(",")[0], first.split(",")[1]),
                cornerTwo = Point(second.split(",")[0], second.split(",")[1])
            )
        }
    }
}
enum class Action { ON, TOGGLE, OFF }
fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
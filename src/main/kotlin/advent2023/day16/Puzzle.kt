package advent2023.day16

import advent2023.day16.Optic.*
import advent2023.utils.*
import advent2023.utils.Direction.*
import java.io.File

const val day = 16
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val contraption = input.to2DGridOfPointsWithValues()
        val entryPoint = BeamPoint(contraption[0][0], RIGHT)
        println(contraption.calculateEnergizedTiles(entryPoint))
    }


    fun runPart2() {
        val contraption = input.to2DGridOfPointsWithValues()
        val entryPoints = listOf(
            contraption.getRow(0).map { BeamPoint(it, DOWN) },
            contraption.getRow(contraption.size - 1).map { BeamPoint(it, UP) },
            contraption.getColumn(0).map { BeamPoint(it, RIGHT) },
            contraption.getColumn(contraption[0].size - 1).map { BeamPoint(it, LEFT) },
        ).flatten()
        println(entryPoints.maxOf { contraption.calculateEnergizedTiles(it) })
    }
    
    private fun List<List<Point>>.calculateEnergizedTiles(
        entryPoint: BeamPoint
    ): Int {
        val beam = mutableSetOf(entryPoint)
        val beamHeads = ArrayDeque(beam)
        while (beamHeads.isNotEmpty()) {
            val (currentHead, currentInDirection) = beamHeads.removeFirst()
            val nextDirections = getNextDirections(currentInDirection, currentHead.value)
            for (nextDirection in nextDirections) {
                val nextPoint = getPointAfterMove(currentHead, nextDirection)
                nextPoint?.let {
                    val newHead = BeamPoint(nextPoint, nextDirection)
                    if (newHead !in beam) {
                        beam += newHead
                        beamHeads.add(newHead)
                    }
                }
            }
        }
        return beam.map { it.point }.toSet().size
    }

    private fun getNextDirections(inDirection: Direction, c: Char): List<Direction> {
        val optic = Optic.toOptic(c)
        return when (optic) {
            EMPTY -> listOf(inDirection)
            // /
            FORWARD -> listOf(
                when (inDirection) {
                    UP -> RIGHT
                    DOWN -> LEFT
                    RIGHT -> UP
                    LEFT -> DOWN
                }
            )
            // \
            BACKWARD -> listOf(
                when (inDirection) {
                    UP -> LEFT
                    DOWN -> RIGHT
                    RIGHT -> DOWN
                    LEFT -> UP
                }
            )
            SPLITH -> when (inDirection) {
                UP, DOWN -> listOf(RIGHT, LEFT)
                RIGHT, LEFT -> listOf(inDirection)
            }
            SPLITV -> when (inDirection) {
                UP, DOWN -> listOf(inDirection)
                RIGHT, LEFT -> listOf(UP, DOWN)
            }
        }
    }
}

enum class Optic(val c: Char) { 
    EMPTY('.'), FORWARD('/'), BACKWARD('\\'), SPLITH('-'), SPLITV('|');
    companion object {
        fun toOptic(c: Char): Optic = entries.find { c == it.c } ?: throw IllegalArgumentException()
    }
}

data class BeamPoint(val point: Point, val inDirection: Direction)

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

package advent2018.day10

import lib.*
import java.io.File
import kotlin.math.absoluteValue

const val day = 10
val file = File("src/main/resources/advent2018/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val lights = input.map { Light.toLight(it) }
        var minTotalDistance = Long.MAX_VALUE
        for (t in 10500L..10520L) {
            println(t)
            val movedLights = lights.map { it.positionAfter(t) }
            val totalDistance = movedLights.sumOf { first -> movedLights.sumOf { second -> second.distance(first) } }
            if (totalDistance < minTotalDistance) {
                println("new minimum: $totalDistance at time $t")
                minTotalDistance = totalDistance
            }

            val grid = initEmptyGrid(endX = 1000, endY = 1000)
            movedLights.forEach { 
                grid.changePoint(Point(it.px.toInt(), it.py.toInt()), '#')
            }
            grid.printV()
        }
        println(lights)
    }

    fun runPart2() {
    }
}


data class Light(val p0: Position, val v: Velocity) {
    
    fun positionAfter(time: Long): Position = Position(
        px = (p0.px + time * v.vx),
        py = (p0.py + time * v.vy),
    )
    
    companion object {
        val regex = Regex("position=<\\s*(-?\\d+),\\s*(-?\\d+)> velocity=<\\s*(-?\\d+),\\s*(-?\\d+)>")
        fun toLight(input: String): Light {
            //position=<10, -3> velocity=<-1,  1>
            val (px, py, vx, vy) = regex.allGroups(input).map { it.toLong() }
            return Light(Position(px, py), Velocity(vx, vy))
        }
    }
}
data class Position(val px: Long, val py: Long) {
    fun distance(other: Position) =
        (this.px - other.px).absoluteValue + (this.py - other.py).absoluteValue
}
data class Velocity(val vx: Long, val vy: Long)

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
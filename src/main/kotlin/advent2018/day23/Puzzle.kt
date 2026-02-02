package advent2018.day23

import lib.allGroups
import lib.runPuzzle
import java.io.File
import kotlin.math.absoluteValue

const val day = 23
val file = File("src/main/resources/advent2018/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val bots = input.map { Nanobot.toNanobot(it) }
        val strongestBot = bots.maxBy { it.radius }
        println(bots.count { strongestBot.isInRange(it) })
    }

    fun runPart2() {
        val origin = Position(0, 0, 0)
        val bots = input.map { Nanobot.toNanobot(it) }
        
        // start with a radius containing all other bots
        var currentRadius = bots.maxOf { it.p.manhattanDistance(origin) + it.radius } + 10
        var currentVolumes = setOf(Nanobot(origin, currentRadius))
        while(currentRadius > 0) {
            //divide current volume into X parts with a smaller radius
            currentRadius = (currentRadius / 2) + if (currentRadius > 2) 1 else 0
            
            val newVolumes = currentVolumes.flatMap { currentVolume ->
                currentVolume.p.centersAt(currentRadius).map { 
                    Nanobot(it, currentRadius)
                }
            }
            
            val newVolumesAndCountOfIntersections = newVolumes.associateWith { bots.countIntersectsWith(it) }
            val maxIntersections = newVolumesAndCountOfIntersections.maxOfOrNull { it.value } ?: 0
            //new set is volumes with maximum number of intersections
            currentVolumes = newVolumesAndCountOfIntersections.filter { it.value == maxIntersections }.map { it.key }.toSet()
        }
        println(currentVolumes.minBy { it.p.manhattanDistance(origin) }.p.manhattanDistance(origin))
    }
}

fun List<Nanobot>.countIntersectsWith(other: Nanobot) =
    count { bot -> bot.intersectsWith(other) }

data class Nanobot(val p: Position, val radius: Long) {
    fun isInRange(other: Nanobot): Boolean =
        isInRange(other.p)

    fun isInRange(position: Position): Boolean {
        val distance = this.p.manhattanDistance(position)
        return distance <= radius
    }

    fun intersectsWith(other: Nanobot): Boolean =
        this.p.manhattanDistance(other.p) <= (this.radius + other.radius)

    companion object {
        //pos=<0,0,0>, r=4
        val regex = Regex("pos=<(-?\\d+),(-?\\d+),(-?\\d+)>, r=(\\d+)")
        fun toNanobot(input: String): Nanobot {
            val (x, y, z, r) = regex.allGroups(input).map { it.toLong() }
            return Nanobot(p = Position(x, y, z), radius = r)
        }
    }
}

data class Position(val x: Long, val y: Long, val z: Long) {
    fun manhattanDistance(other: Position): Long =
        (this.x - other.x).absoluteValue +
                (this.y - other.y).absoluteValue +
                (this.z - other.z).absoluteValue

    fun centersAt(currentRadius: Long): List<Position> =
        (-1L..1L).flatMap { xd ->
            (-1L..1L).flatMap { yd ->
                (-1L..1L).map { zd ->
                    this.copy(
                        x = this.x + xd * currentRadius,
                        y = this.y + yd * currentRadius,
                        z = this.z + zd * currentRadius
                    )
                }
            }
        }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

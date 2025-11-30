package advent2017.day20

import lib.allGroups
import lib.runPuzzle
import lib.solveQuadraticEquationWithZeroA
import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.pow

const val day = 20
val file = File("src/main/resources/advent2017/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val particles = input.mapIndexed { idx, input -> Particle.toParticle(idx, input) }
        val times = (1..8).map { 10.0.pow(it.toDouble()).toLong() }
        for (time in times) {
            val closestParticle = particles.minBy { it.distanceAfter(time) }
            println("closest: ${closestParticle.id}: ${closestParticle.distanceAfter(time, true)}")
        }
    }

    fun runPart2() {
        val particles = input.mapIndexed { idx, input -> Particle.toParticle(idx, input) }
        val collisionTimes = particles.flatMap { p1 ->
            particles.filter { p2 -> p1 != p2 }.mapNotNull { p2 -> p1.collisionTimeWith(p2) }
        }.toList().sorted().toSet()
        var remainingParticles = particles.toList()
        for (time in collisionTimes) {
            remainingParticles = remainingParticles.filter { p1 -> remainingParticles.filter { p2 -> p2 != p1 }.none { p2 -> p1.collidesWith(p2, time) } }
        }
        println(remainingParticles.size)
    }
}
data class Particle(val id: Int, val position: Position, val velocity: Velocity, val acceleration: Acceleration) {
    fun distanceAfter(time: Long, log: Boolean = false): Long {
        val newPosition = positionAfter(time)
        if (log) println("position after $time: $newPosition")
        return newPosition.manhattenDistance()
    }

    private fun positionAfter(time: Long): Position = Position(
        px = (position.px + time * velocity.vx + 0.5 * acceleration.ax * (time + 1) * time).toLong(),
        py = (position.py + time * velocity.vy + 0.5 * acceleration.ay * (time + 1) * time).toLong(),
        pz = (position.pz + time * velocity.vz + 0.5 * acceleration.az * (time + 1) * time).toLong(),
    )

    fun collidesWith(other: Particle, time: Long): Boolean {
        val collision = this.positionAfter(time) == other.positionAfter(time)
        if (collision) println("collision of $this with $other at $time seconds, positions: ${this.positionAfter(time)} and ${other.positionAfter(time)}")
        return collision
    }
    
    fun collisionTimeWith(other: Particle): Long? {
//        px = (position.px + time * velocity.vx + 0.5 * acceleration.ax * (time + 1) * time).toLong(),
        // calculate time for x collision, enter into y and z, if all match then it collides
        val a = 0.5 * (this.acceleration.ax - other.acceleration.ax)
        val b = this.velocity.vx - other.velocity.vx + 0.5 * (this.acceleration.ax - other.acceleration.ax)
        val c = this.position.px - other.position.px
        val solX = solveQuadraticEquationWithZeroA(a, b, c.toDouble())
        if (solX != null) {
            val (left, right) = solX
            if (left > 0 && left < Long.MAX_VALUE && this.positionAfter(left.toLong()) == other.positionAfter(left.toLong())) {
                return left.toLong()
            }
            if (right > 0 && right < Long.MAX_VALUE && this.positionAfter(right.toLong()) == other.positionAfter(right.toLong())) {
                return right.toLong()
            }
            return null
        }
        return null
    }

    companion object {
        val vector = Regex("\\w=<(-?\\d+),(-?\\d+),(-?\\d+)>")
        fun toParticle(id: Int, input: String): Particle {
            //p=<3,0,0>, v=<2,0,0>, a=<-1,0,0>
            //p=<4,0,0>, v=<0,0,0>, a=<-2,0,0>
            val (p, v, a) = input.split(", ")
            val (px, py, pz) = vector.allGroups(p).map { it.toLong() }
            val (vx, vy, vz) = vector.allGroups(v).map { it.toLong() }
            val (ax, ay, az) = vector.allGroups(a).map { it.toLong() }
            return Particle(
                id = id,
                position = Position(px, py, pz),
                velocity = Velocity(vx, vy, vz),
                acceleration = Acceleration(ax, ay, az),
            )
        }
        
    }
}
data class Position(val px: Long, val py: Long, val pz: Long) {
    fun manhattenDistance() = px.absoluteValue + py.absoluteValue + pz.absoluteValue
}
data class Velocity(val vx: Long, val vy: Long, val vz: Long)
data class Acceleration(val ax: Long, val ay: Long, val az: Long)

fun Acceleration.sum() = ax.absoluteValue + ay.absoluteValue + az.absoluteValue

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
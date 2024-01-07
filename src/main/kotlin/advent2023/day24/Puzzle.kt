package advent2023.day24

import advent2023.utils.mapToPair
import advent2023.utils.runPuzzle
import org.paukov.combinatorics3.Generator
import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.sign

const val day = 24
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val hailstones = input.map { Hailstone.createHailstone(it) }
        val allPairs = Generator.combination(hailstones).simple(2).stream().toList()
        val range = 200000000000000L..400000000000000L
        println(allPairs.count { it.first().intersectsWithIn(it.last(), range, range) })
    }

    fun runPart2() {
        val hailstones = input.map { Hailstone.createHailstone(it) }
        val range = 7L..27L
    }
}

data class Hailstone(val p0: Position, val v: Velocity, val yFunc: (Long) -> Double, val xFunc: (Long) -> Double) {
    fun intersectsWithIn(other: Hailstone, xRange: LongRange, yRange: LongRange): Boolean {
        val dy1 = this.yFunc(xRange.first) - other.yFunc(xRange.first)
        val dy2 = this.yFunc(xRange.last) - other.yFunc(xRange.last)
        val dx1 = this.xFunc(yRange.first) - other.xFunc(yRange.first)
        val dx2 = this.xFunc(yRange.last) - other.xFunc(yRange.last)
        if (!(dy1.sign != dy2.sign) || !(dx1.sign != dx2.sign)) return false
        val crossX =
            xRange.first + (xRange.last - xRange.first) * dy1.absoluteValue / (dy1.absoluteValue + dy2.absoluteValue)
        return this.crossesInFutureX(crossX) && other.crossesInFutureX(crossX)
    }
    
    private fun crossesInFutureX(crossX: Double) = if (this.v.vx > 0) this.p0.px < crossX else this.p0.px > crossX

    companion object {
        //19, 13, 30 @ -2,  1, -2
        fun createHailstone(input: String): Hailstone {
            val (p, v) = input.split(" @ ").map { it.split(Regex(",")).map { it.trim().toLong() } }
                .mapToPair(
                    transformLeft = { Position(it[0], it[1], it[2]) },
                    transformRight = { Velocity(it[0], it[1], it[2]) }
                )
            // y = ax + b
            val a = v.vy.toDouble() / v.vx
            val b = p.py - a * p.px
            // x = cy + d
            val c = v.vx.toDouble() / v.vy
            val d = p.px - c * p.py
            return Hailstone(p, v, { x -> a * x + b }, { y -> c * y + d } )
        }
    }
}

data class Position(val px: Long, val py: Long, val pz: Long)
data class Velocity(val vx: Long, val vy: Long, val vz: Long)

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

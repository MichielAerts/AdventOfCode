package advent2023.day24

import advent2023.utils.mapToPair
import advent2023.utils.runPuzzle
import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.LUDecomposition
import org.paukov.combinatorics3.Generator
import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.roundToLong
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
        val (a, b, c) = input.map { Hailstone.createHailstone(it) }
        // lots of algebra, leading to system of linear equations with 6 unknowns (p0 + v of rock)

        // hailstones a & b and rock r
        // (vyb - vya) xr + (vxa - vxb) yr + 0 * zr + (ya - yb) vxr + (xb - xa) vyr + 0 vzr
        val coefficients = arrayOf(
            longArrayOf(b.v.vy - a.v.vy, a.v.vx - b.v.vx, 0, a.p0.py - b.p0.py, b.p0.px - a.p0.px, 0).map { it.toDouble() }.toDoubleArray(),
            longArrayOf(b.v.vz - a.v.vz, 0, a.v.vx - b.v.vx, a.p0.pz - b.p0.pz, 0, b.p0.px - a.p0.px).map { it.toDouble() }.toDoubleArray(),
            longArrayOf(0, b.v.vz - a.v.vz, a.v.vy - b.v.vy, 0, a.p0.pz - b.p0.pz, b.p0.py - a.p0.py).map { it.toDouble() }.toDoubleArray(),
            longArrayOf(c.v.vy - a.v.vy, a.v.vx - c.v.vx, 0, a.p0.py - c.p0.py, c.p0.px - a.p0.px, 0).map { it.toDouble() }.toDoubleArray(),
            longArrayOf(c.v.vz - a.v.vz, 0, a.v.vx - c.v.vx, a.p0.pz - c.p0.pz, 0, c.p0.px - a.p0.px).map { it.toDouble() }.toDoubleArray(),
            longArrayOf(0, c.v.vz - a.v.vz, a.v.vy - c.v.vy, 0, a.p0.pz - c.p0.pz, c.p0.py - a.p0.py).map { it.toDouble() }.toDoubleArray()
        )
        val matrix = Array2DRowRealMatrix(coefficients, false)
        val solver = LUDecomposition(matrix).solver

        // right side 
        // xb vyb - yb vxb + ya vxa - xa vya 
        val rhs = doubleArrayOf(
            (b.p0.px * b.v.vy - b.p0.py * b.v.vx + a.p0.py * a.v.vx - a.p0.px * a.v.vy).toDouble(),
            (b.p0.px * b.v.vz - b.p0.pz * b.v.vx + a.p0.pz * a.v.vx - a.p0.px * a.v.vz).toDouble(),
            (b.p0.py * b.v.vz - b.p0.pz * b.v.vy + a.p0.pz * a.v.vy - a.p0.py * a.v.vz).toDouble(),
            (c.p0.px * c.v.vy - c.p0.py * c.v.vx + a.p0.py * a.v.vx - a.p0.px * a.v.vy).toDouble(),
            (c.p0.px * c.v.vz - c.p0.pz * c.v.vx + a.p0.pz * a.v.vx - a.p0.px * a.v.vz).toDouble(),
            (c.p0.py * c.v.vz - c.p0.pz * c.v.vy + a.p0.pz * a.v.vy - a.p0.py * a.v.vz).toDouble(),
        )
        val constants = ArrayRealVector(rhs, false)
        val solution = solver.solve(constants)
        println(solution.toArray().take(3).sum().roundToLong())
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

package advent2018.day25

import lib.runPuzzle
import java.io.File
import kotlin.math.absoluteValue

const val day = 25
val file = File("src/main/resources/advent2018/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val points = input.map { SpaceTimePoint.createPoint(it) }
        var constellations = points.map { Constellation(mutableListOf(it)) }
        constellations = constellations.consolidate()
        println(constellations.size)
    }

    fun runPart2() {
    }
}

private fun List<Constellation>.consolidate(): List<Constellation> {
    var consolidating = true
    var currentList = this.toMutableList()
    outer@ while(consolidating) {
        val newList = mutableListOf<Constellation>()
        consolidating = false
        for (i in 0..<currentList.size) {
            val newConstellation = currentList[i]
            for (j in (i + 1)..<currentList.size) {
                val otherConstellation = currentList[j]
                if (otherConstellation.points.any { o -> newConstellation.points.any { n -> n.distanceTo(o) <= 3 } }) {
                    newConstellation.points.addAll(otherConstellation.points)
                    currentList.remove(otherConstellation)
                    newList.add(newConstellation)
                    consolidating = true
                    continue@outer
                }
            }
            newList.add(newConstellation)
        }
        currentList = newList.filter { it.points.isNotEmpty() }.toMutableList()
    }
    return currentList
}

data class Constellation(val points: MutableList<SpaceTimePoint>)

data class SpaceTimePoint(val x: Int, val y: Int, val z: Int, val t: Int) {
    
    fun distanceTo(other: SpaceTimePoint): Int =
        (this.x - other.x).absoluteValue +
                (this.y - other.y).absoluteValue +
                (this.z - other.z).absoluteValue +
                (this.t - other.t).absoluteValue
    
    companion object {
        fun createPoint(input: String): SpaceTimePoint {
            val (x, y, z, t) = input.split(",").map { it.toInt() }
            return SpaceTimePoint(x, y, z, t)
        }
    }
}
fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
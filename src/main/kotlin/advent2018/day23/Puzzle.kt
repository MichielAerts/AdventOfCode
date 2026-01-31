package advent2018.day23

import lib.*
import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.roundToLong

const val day = 23
val file = File("src/main/resources/advent2018/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val bots = input.map { Nanobot.toNanobot(it) }
        val strongestBot = bots.maxBy { it.radius }
        println(bots.count { strongestBot.isInRange(it) })
    }

    fun runPart2() {

        val zero = Position(0, 0, 0)
        val bots = input.map { Nanobot.toNanobot(it) }
        var extremes = bots.flatMap { it.extremes() }
        val maxPoint = Position(11949478L, 7561797L, 13927540L)
        val buffer = 50
        val xs = (maxPoint.x - buffer)..(maxPoint.x + buffer)
        val ys = (maxPoint.y - buffer)..(maxPoint.y + buffer)
        val zs = (maxPoint.z - buffer)..(maxPoint.z + buffer)
        val all = xs.flatMap { x -> ys.flatMap { y -> zs.map { z -> Position(x, y, z) } } }
        println(all.size)
        extremes = all
        val extremesWithBotsInRange = extremes.associateWith { p -> bots.countInRangeOf(p) }
        extremesWithBotsInRange.entries.sortedByDescending { it.value }.take(20).onEach { println(it) }
        val maxInRange = extremesWithBotsInRange.maxOf { it.value }
        val allMaxInRange = extremesWithBotsInRange.filter { it.value == maxInRange }
        println(allMaxInRange)
        val closest = allMaxInRange.minBy { it.key.manhattanDistance(zero) }
        println(closest)
        println(closest.key.manhattanDistance(zero))
//        val others = listOf(
//            Position(11949478L, 7561797L, 13927540L),
////            Position(11949477L, 7561797L, 13927540L),
////            Position(11949478L, 7561796L, 13927540L),
////            Position(11949478L, 7561797L, 13927539L),
////            Position(11949479L, 7561797L, 13927540L),
////            Position(11949479L, 7561797L, 13927540L),
////            Position(11949479L, 7561796L, 13927539L),
////            Position(11949479L, 7561797L, 13927540L),
//            
//            Position(11949479L, 7561796L, 13927539L),
//        )
//        others.associateWith { bots.countInRangeOf(it) }.forEach { println(it) }
        //33438815 too low
        //Position(x=11949478, y=7561797, z=13927540)=865
//        Position(x=11949479, y=7561796, z=13927537)=867
//        Position(x=11949479, y=7561796, z=13927539)=868
    }
    
    fun runPart2Old() {
        
        val zero = Position(0, 0, 0)
        val bots = input.map { Nanobot.toNanobot(it) }
        var extremes = bots.flatMap { it.extremes() }
        val maxPoint = Position(11949478L, 7561797L, 13927540L)
        val buffer = 50
        val xs = (maxPoint.x - buffer)..(maxPoint.x + buffer)
        val ys = (maxPoint.y - buffer)..(maxPoint.y + buffer)
        val zs = (maxPoint.z - buffer)..(maxPoint.z + buffer)
        val all = xs.flatMap { x -> ys.flatMap { y -> zs.map { z -> Position(x, y, z) } } }
        println(all.size)
        extremes = all
        val extremesWithBotsInRange = extremes.associateWith { p -> bots.countInRangeOf(p) }
        extremesWithBotsInRange.entries.sortedByDescending { it.value }.take(20).onEach { println(it) }
        val maxInRange = extremesWithBotsInRange.maxOf { it.value }
        val allMaxInRange = extremesWithBotsInRange.filter { it.value == maxInRange }
        println(allMaxInRange)
        val closest = allMaxInRange.minBy { it.key.manhattanDistance(zero) }
        println(closest)
        println(closest.key.manhattanDistance(zero))
//        val others = listOf(
//            Position(11949478L, 7561797L, 13927540L),
////            Position(11949477L, 7561797L, 13927540L),
////            Position(11949478L, 7561796L, 13927540L),
////            Position(11949478L, 7561797L, 13927539L),
////            Position(11949479L, 7561797L, 13927540L),
////            Position(11949479L, 7561797L, 13927540L),
////            Position(11949479L, 7561796L, 13927539L),
////            Position(11949479L, 7561797L, 13927540L),
//            
//            Position(11949479L, 7561796L, 13927539L),
//        )
//        others.associateWith { bots.countInRangeOf(it) }.forEach { println(it) }
        //33438815 too low
        //Position(x=11949478, y=7561797, z=13927540)=865
//        Position(x=11949479, y=7561796, z=13927537)=867
//        Position(x=11949479, y=7561796, z=13927539)=868
    }
    
    fun List<Nanobot>.countInRangeOf(p: Position) =
        count { bot -> bot.isInRange(p) }
}

// (x-x1)^2 + (y-y1)^2 + (z-z1)^2 = r1^2
fun Triple<Nanobot, Nanobot, Nanobot>.intersectionPoints(): List<Position> {
    val (sphere1, sphere2, sphere3) = this
    val (x1, y1, z1) = sphere1.p
    val r1 = sphere1.radius
    val (x2, y2, z2) = sphere2.p
    val r2 = sphere2.radius
    val (x3, y3, z3) = sphere3.p
    val r3 = sphere3.radius
    println("trying: $this")
    val s1 = Sphere(IntPoint3D(x1, y1, z1), r1)
    val s2 = Sphere(IntPoint3D(x2, y2, z2), r2)
    val s3 = Sphere(IntPoint3D(x3, y3, z3), r3)
    
    return when (val result = findSphereIntersection(s1, s2, s3)) {
        is IntersectionResult.TwoPoints -> {
            println("Two intersection points found:")
            println("Point 1: (${result.point1.x}, ${result.point1.y}, ${result.point1.z})")
            println("Point 2: (${result.point2.x}, ${result.point2.y}, ${result.point2.z})")
            listOf(
                Position(result.point1.x.roundToLong(), result.point1.y.roundToLong(), result.point1.z.roundToLong()),
                Position(result.point2.x.roundToLong(), result.point2.y.roundToLong(), result.point2.z.roundToLong()),
            )
        }
        is IntersectionResult.OnePoint -> {
            println("Single intersection point (tangent):")
            println("Point: (${result.point.x}, ${result.point.y}, ${result.point.z})")
            listOf(
                Position(result.point.x.roundToLong(), result.point.y.roundToLong(), result.point.z.roundToLong()),
            )
        }
        is IntersectionResult.NoIntersection -> {
            println("No intersection: ${result.reason}")
            emptyList()
        }
    }
}

data class Nanobot(val p: Position, val radius: Long) {
    fun isInRange(other: Nanobot): Boolean =
        isInRange(other.p)

    fun isInRange(position: Position): Boolean {
        val distance = this.p.manhattanDistance(position)
        return distance <= radius
    }

    fun extremes(): List<Position> =
        listOf(
            Position(p.x + radius, p.y, p.z),
            Position(p.x - radius, p.y, p.z),
            Position(p.x, p.y + radius, p.z),
            Position(p.x, p.y - radius, p.z),
            Position(p.x, p.y, p.z + radius),
            Position(p.x, p.y, p.z - radius),
        )
    
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
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
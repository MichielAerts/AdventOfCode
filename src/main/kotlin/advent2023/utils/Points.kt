package advent2023.utils

import advent2023.utils.WindDirection.*
import kotlin.math.absoluteValue

enum class WindDirection {
    N, NE, E, SE, S, SW, W, NW;
    companion object {
        fun northwards() = setOf(N, NE, NW)
        fun southwards() = setOf(S, SE, SW)
        fun eastwards() = setOf(E, NE, SE)
        fun westwards() = setOf(W, NW, SW)
        fun getXwards(d: WindDirection) = when(d) {
            N -> northwards()
            S -> southwards()
            W -> westwards()
            E -> eastwards()
            else -> throw IllegalArgumentException()
        }
    }
}


enum class Direction {
    UP, DOWN, RIGHT, LEFT;

    fun turnRight(): Direction = when(this) {
        UP -> RIGHT
        DOWN -> LEFT
        RIGHT -> DOWN
        LEFT -> UP
    }

    fun opposite(): Direction = when(this) {
        UP -> DOWN
        DOWN -> UP
        RIGHT -> LEFT
        LEFT -> RIGHT
    }

    fun turnLeft(): Direction = when(this) {
        UP -> LEFT
        DOWN -> RIGHT
        RIGHT -> UP
        LEFT -> DOWN
    }

    companion object {
        fun getDirectionFromFirstLetter(input: String): Direction {
            return when (input) {
                "U" -> UP
                "D" -> DOWN
                "R" -> RIGHT
                "L" -> LEFT
                else -> throw IllegalArgumentException("No")
            }
        }
    }
}

open class Point(val x: Int, val y: Int, var z: Int = 0, var value: Char = '.') {
    constructor(x: String, y: String) : this(x.toInt(), y.toInt())
    constructor(x: String, y: String, z: String) : this(x.toInt(), y.toInt(), z.toInt())

    private fun rotateX(): Point = Point(x, -z, y)
    private fun rotateY(): Point = Point(-z, y, x)
    private fun rotateZ(): Point = Point(-y, x, z)

    fun rotate(xyz: Triple<Int, Int, Int>): Point {
        var newP = this
        repeat(xyz.first) { newP = newP.rotateX() }
        repeat(xyz.second) { newP = newP.rotateY() }
        repeat(xyz.third) { newP = newP.rotateZ() }
        return newP
    }

    fun getDistanceTo(other: Point): Distance = Distance(other.x - x, other.y - y, other.z - z)

    fun getDistanceToAll(others: List<Point>): List<Distance> = others.map { Distance(it.x - x, it.y - y, it.z - z) }

    operator fun minus(d: Distance) = Point(x - d.dx, y - d.dy, z - d.dz)

    override fun toString(): String = "Point(x: $x, y: $y, z: $z, c: $value)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Point

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + z
        return result
    }

    operator fun plus(d: Distance): Point = Point(x + d.dx, y + d.dy, z + d.dz)
    operator fun component1(): Int = x
    operator fun component2(): Int = y
    operator fun component3(): Int = z
    fun getViews(grid: List<List<Point>>): List<List<Point>> = listOf(
        grid.getColumn(x).subList(0, y).reversed(), //up    
        grid.getColumn(x).subListTillEnd(y + 1), //down
        grid.getRow(y).subList(0, x).reversed(), //left    
        grid.getRow(y).subListTillEnd(x + 1) //right
    )

    fun getView(direction: Direction, grid: List<List<Point>>): List<Point> = when(direction) {
        Direction.UP -> grid.getColumn(x).subList(0, y).reversed()
        Direction.DOWN -> grid.getColumn(x).subListTillEnd(y + 1)
        Direction.RIGHT -> grid.getRow(y).subListTillEnd(x + 1)
        Direction.LEFT -> grid.getRow(y).subList(0, x).reversed()
    }

    fun getPointsInLineTo(end: Point): List<Point> = when {
        this.x == end.x && this.y < end.y -> (this.y..end.y).map { Point(this.x, it) }
        this.x == end.x && this.y > end.y -> (end.y..this.y).map { Point(this.x, it) }
        this.y == end.y && this.x < end.x -> (this.x..end.x).map { Point(it, this.y) }
        this.y == end.y && this.x > end.x -> (end.x..this.x).map { Point(it, this.y) }
        else -> throw IllegalArgumentException("shouldn't")
    }

    fun unconnectedSides(cubes: List<Point>): Int {
        val potentialNeighbours = listOf(
            Point(this.x - 1, this.y, this.z),
            Point(this.x + 1, this.y, this.z),
            Point(this.x, this.y - 1, this.z),
            Point(this.x, this.y + 1, this.z),
            Point(this.x, this.y, this.z - 1),
            Point(this.x, this.y, this.z + 1),
        )
        return 6 - potentialNeighbours.count { cubes.contains(it) }
    }

    fun connectedTo(grid: List<List<List<Point>>>, c: Char): Int {
        val potentialNeighbours = listOf(
            Point(this.x - 1, this.y, this.z),
            Point(this.x + 1, this.y, this.z),
            Point(this.x, this.y - 1, this.z),
            Point(this.x, this.y + 1, this.z),
            Point(this.x, this.y, this.z - 1),
            Point(this.x, this.y, this.z + 1),
        )
        return potentialNeighbours.mapNotNull { grid.getPoint(it.x, it.y, it.z) }.count { it.value == c }
    }

    fun getManhattanDistance(other: Point) = (this.x - other.x).absoluteValue + (this.y - other.y).absoluteValue

    open fun copy() = Point(x, y, z, value)
}

data class Distance(val dx: Int, val dy: Int, val dz: Int) {
    operator fun minus(other: Distance): Distance = Distance(dx - other.dx, dy - other.dy, dz - other.dz)
    operator fun plus(other: Distance): Distance = Distance(dx + other.dx, dy + other.dy, dz + other.dz)
    fun getManhattanDistance(other: Distance): Int =
        (dx - other.dx).absoluteValue + (dy - other.dy).absoluteValue + (dz - other.dz).absoluteValue
}

data class Pos(val x: Int, val y: Int) {
    constructor(x: String, y: String) : this(x.toInt(), y.toInt())

    fun isTouching(other: Pos): Boolean = other in setOf(
        Pos(x - 1, y - 1),
        Pos(x, y - 1),
        Pos(x + 1, y - 1),
        Pos(x - 1, y),
        Pos(x, y),
        Pos(x + 1, y),
        Pos(x - 1, y + 1),
        Pos(x, y + 1),
        Pos(x + 1, y + 1)
    )

    fun getManhattanDistance(other: Pos): Int = (this.x - other.x).absoluteValue + (this.y - other.y).absoluteValue
}

fun Pos.move(d: Direction): Pos = when (d) {
    Direction.UP -> Pos(this.x, this.y + 1)
    Direction.DOWN -> Pos(this.x, this.y - 1)
    Direction.RIGHT -> Pos(this.x + 1, this.y)
    Direction.LEFT -> Pos(this.x - 1, this.y)
}

fun Pos.getNextPos(d: Direction): Pos = when (d) {
    Direction.UP -> Pos(this.x, this.y - 1)
    Direction.DOWN -> Pos(this.x, this.y + 1)
    Direction.RIGHT -> Pos(this.x + 1, this.y)
    Direction.LEFT -> Pos(this.x - 1, this.y)
}

fun List<List<Point>>.getDirectNeighbours(p: Point): PointAndNeighbours {
    val potentialNeighbours = listOf(Pos(p.x - 1, p.y), Pos(p.x + 1, p.y), Pos(p.x, p.y - 1), Pos(p.x, p.y + 1))
    return PointAndNeighbours(p, potentialNeighbours.mapNotNull { this.getPoint(it.x, it.y) })
}

fun List<List<List<Point>>>.getDirectNeighbours3D(p: Point): PointAndNeighbours {
    val potentialNeighbours =
        listOf(
            Point(p.x - 1, p.y, p.z), Point(p.x + 1, p.y, p.z),
            Point(p.x, p.y - 1, p.z), Point(p.x, p.y + 1, p.z),
            Point(p.x, p.y, p.z - 1), Point(p.x, p.y, p.z + 1)
        )
    return PointAndNeighbours(p, potentialNeighbours.mapNotNull { this.getPoint(it.x, it.y, it.z) })
}

fun List<List<Point>>.getPoint(p: Point, direction: WindDirection) =
    this.getPoint(getSurroundingPositions(p).getValue(direction))

fun List<List<Point>>.getSurroundingPoints(p: Point): Map<WindDirection, Point> {
    val points = getSurroundingPositions(p)
    return points.map { it.key to this.getPoint(it.value)!! }.toMap()
}

private fun getSurroundingPositions(p: Point): Map<WindDirection, Pos> {
    val x = p.x
    val y = p.y
    return mapOf(
        N to Pos(x, y - 1),
        NE to Pos(x + 1, y - 1),
        E to Pos(x + 1, y),
        SE to Pos(x + 1, y + 1),
        S to Pos(x, y + 1),
        SW to Pos(x - 1, y + 1),
        W to Pos(x - 1, y),
        NW to Pos(x - 1, y - 1)
    )
}

fun List<List<Point>>.getSquare(minX: Int, maxX: Int, minY: Int, maxY: Int): List<List<Point>> =
    this.filter { it[0].y in minY..maxY }.map { it.filter { it.x in minX..maxX } }

fun List<List<Point>>.getPoint(x: Int, y: Int): Point? {
    if (x < 0 || x > (this[0].size - 1) || y < 0 || y > (this.size - 1)) return null
    return this[y][x]
}

fun List<List<Point>>.getPoint(pos: Pos): Point? = getPoint(pos.x, pos.y)

fun List<List<List<Point>>>.getPoint(x: Int, y: Int, z: Int): Point? {
    if (x < 0 || x > (this[0][0].size - 1) || y < 0 || y > (this[0].size - 1) || z < 0 || z > (this.size - 1)) return null
    return this[z][y][x]
}

fun List<List<Point>>.getHighestRowContaining(c: Char): Int = this.indexOfLast { it.any { it.value == c } }

fun List<List<Point>>.getRow(y: Int): List<Point> = this[y]

fun List<List<Point>>.getColumn(x: Int): List<Point> = this.map { it[x] }

fun List<List<Point>>.printZ() = this.forEach { println(it.map { it.z }.joinToString("")) }

fun List<List<Point>>.printV() = this.forEach { println(it.map { it.value }.joinToString("")) }

fun List<List<List<Point>>>.printV3D() = this.forEach {
    it.forEach {
        println(it.map { it.value }.joinToString(""))
    }
    println()
}

data class PointAndNeighbours(val point: Point, val neighbours: List<Point>)
package advent2023.utils

import advent2023.utils.WindDirection.*
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

fun List<String>.to2DGridOfPoints(): List<List<Point>> = this.mapIndexed { y, r ->
    r.toList().mapIndexed { x, v -> Point(x, y, v.digitToInt()) }
}

fun List<String>.to2DGridOfPointsWithValues(): List<List<Point>> = this.mapIndexed { y, r ->
    r.toList().mapIndexed { x, v -> Point(x, y, value = v) }
}

fun initEmptyGrid(startX: Int = 0, endX: Int, startY: Int = 0, endY: Int): List<List<Point>> =
    (startY..endY).map { y -> (startX..endX).map { Point(it, y) } }

fun initEmpty3DGrid(startX: Int = 0, endX: Int, startY: Int = 0, endY: Int, startZ: Int = 0, endZ: Int): List<List<List<Point>>> =
    (startZ..endZ).map { z -> (startY..endY).map { y -> (startX..endX).map { x -> Point(x, y, z) } } }

fun List<List<Point>>.findSingleValueInGrid(v: Char): Point = this.flatten().find { it.value == v }!!

fun List<List<Point>>.findAllValuesInGrid(v: Char): List<Point> = this.flatten().filter { it.value == v }

fun List<List<Point>>.changePoint(pointToBeChanged: Point, c: Char) {
    this.getPoint(pointToBeChanged.x, pointToBeChanged.y)?.value = c
}

fun List<List<List<Point>>>.changePoint3D(pointToBeChanged: Point, c: Char) {
    this.getPoint(pointToBeChanged.x, pointToBeChanged.y, pointToBeChanged.z)?.value = c
}

fun List<List<List<Point>>>.allPoints(): List<Point> = flatten().flatten()

fun List<List<Point>>.changePoints(points: Set<Point>, c: Char) {
    for (pointToBeChanged in points) {
        this.changePoint(pointToBeChanged, c)
    }
}

fun List<List<List<Point>>>.changePoints3D(points: Set<Point>, c: Char) {
    for (pointToBeChanged in points) {
        this.changePoint3D(pointToBeChanged, c)
    }
}
fun List<List<Point>>.floodFromOutside(empty: Char = '.', replacement: Char = 'O'): List<List<Point>> {
    // every tile on outside which is not part of loop is O
    val ySize = this.size
    val xSize = this[0].size
    var currentGrid: List<List<Point>>
    var newGrid = this.copy()
    
    newGrid.changePoints( flatten().filter {
        (it.x == 0 || it.x == (xSize - 1) || it.y == 0 || it.y == (ySize - 1)) && it.value == empty
    }.toSet(), replacement)
    var tilesChanged = 1
    
    // let the O spread till all tiles outside the loop are covered
    while (tilesChanged != 0) {
        currentGrid = newGrid.copy()
        newGrid = newGrid.copy()
        val pointsToChange = currentGrid.flatten().filter {
            it.value == empty && currentGrid.getDirectNeighbours(it).neighbours.any { it.value == replacement }
        }.toSet()
        tilesChanged = pointsToChange.size
        newGrid.changePoints(pointsToChange, replacement)
    }
    return newGrid
}

fun List<List<Point>>.copy(): List<List<Point>> = this.map { it.map { it.copy() }.toList() }.toList()

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

    fun getPointsInLineTo3D(end: Point): List<Point> = when {
        this.x != end.x -> (min(this.x, end.x)..max(this.x, end.x)).map { Point(it, this.y, this.z) }
        this.y != end.y -> (min(this.y, end.y)..max(this.y, end.y)).map { Point(this.x, it, this.z) }
        this.z != end.z -> (min(this.z, end.z)..max(this.z, end.z)).map { Point(this.x, this.y, it) }
        this.x == end.x && this.y == end.y && this.z == end.z -> listOf(Point(this.x, this.y, this.z))
        else -> throw IllegalArgumentException("shouldn't happen: start $this, end $end")
    }
    
    fun lookDown(grid: List<List<List<Point>>>): List<Point> = 
        // TODO solve case of standing up brick
        grid.getPillar(this.x, this.y).subList(0, this.z).reversed()
        
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

    open fun copy(x: Int = this.x, y: Int = this.y, z: Int = this.z, value: Char = this.value) = Point(x, y, z, value)
    fun isInSameDirectionAs(otherFourPoints: List<Point>): Boolean =
        otherFourPoints.size == 4 && (otherFourPoints.all { it.x == this.x } || otherFourPoints.all { it.y == this.y })
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
    operator fun plus(o: Pos): Pos = Pos(x + o.x, y + o.y)
}

fun Pos.getNextPos(d: Direction, amount: Int = 1): Pos = when (d) {
    Direction.UP -> Pos(this.x, this.y - amount)
    Direction.DOWN -> Pos(this.x, this.y + amount)
    Direction.RIGHT -> Pos(this.x + amount, this.y)
    Direction.LEFT -> Pos(this.x - amount, this.y)
}

fun List<List<Point>>.getDirectNeighbours(p: Point): PointAndNeighbours {
    val potentialNeighbours = listOf(Pos(p.x - 1, p.y), Pos(p.x + 1, p.y), Pos(p.x, p.y - 1), Pos(p.x, p.y + 1))
    return PointAndNeighbours(p, potentialNeighbours.mapNotNull { this.getPoint(it.x, it.y) })
}

fun List<List<Point>>.getNeighboursAndDirection(p: Point): Map<Direction, Point> {
    val potentialNeighbours = mapOf(
        Direction.UP to Pos(p.x, p.y - 1),
        Direction.RIGHT to Pos(p.x + 1, p.y),
        Direction.DOWN to Pos(p.x, p.y + 1),
        Direction.LEFT to Pos(p.x - 1, p.y)
    )
    return potentialNeighbours
        .filter { this.getPoint(it.value.x, it.value.y) != null }
        .map { it.key to this.getPoint(it.value.x, it.value.y)!! }.toMap()
}

fun Point.findDirectionOfThisTo(o: Point) =
    when {
        (this.x == o.x && this.y - o.y == 1) -> Direction.DOWN
        (this.x == o.x && o.y - this.y == 1) -> Direction.UP
        (this.y == o.y && this.x - o.x == 1) -> Direction.RIGHT
        (this.y == o.y && o.x - this.x == 1) -> Direction.LEFT
        else -> throw IllegalStateException("not a direct neightbour")
    }

fun List<List<Point>>.getAdjacentNeighbours(p: Point): PointAndNeighbours {
    val potentialNeighbours = listOf(
        Pos(p.x - 1, p.y - 1),
        Pos(p.x, p.y - 1),
        Pos(p.x + 1, p.y - 1),
        Pos(p.x - 1, p.y),
        Pos(p.x + 1, p.y),
        Pos(p.x - 1, p.y + 1),
        Pos(p.x, p.y + 1),
        Pos(p.x + 1, p.y + 1)
    )
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
fun List<List<Point>>.getPointAfterMoveSure(current: Point, direction: Direction): Point = 
    getPointAfterMove(current, direction)!!

fun List<List<Point>>.getPointAfterMove(current: Point, direction: Direction): Point? = when(direction) {
    Direction.UP -> this.getPoint(current.x, current.y - 1)
    Direction.DOWN -> this.getPoint(current.x, current.y + 1)
    Direction.RIGHT -> this.getPoint(current.x + 1, current.y)
    Direction.LEFT -> this.getPoint(current.x - 1, current.y)
}

fun List<List<List<Point>>>.getPoint(x: Int, y: Int, z: Int): Point? {
    if (x < 0 || x > (this[0][0].size - 1) || y < 0 || y > (this[0].size - 1) || z < 0 || z > (this.size - 1)) return null
    return this[z][y][x]
}

fun List<List<Point>>.getHighestRowContaining(c: Char): Int = this.indexOfLast { it.any { it.value == c } }

fun Pair<Point, Point>.getXRange(): IntRange =
    min(first.x, second.x) .. max(first.x, second.x)

fun Pair<Point, Point>.getYRange(): IntRange =
    min(first.y, second.y) .. max(first.y, second.y)

fun List<List<Point>>.getRow(y: Int): List<Point> = this[y]

fun List<List<Point>>.getColumn(x: Int): List<Point> = this.map { it[x] }

fun List<List<List<Point>>>.getPillar(x: Int, y: Int): List<Point> = this.map { it[y][x] }

fun List<List<Point>>.getColumns(): List<List<Point>> = this.transpose()

fun List<List<Point>>.printZ() = this.forEach { println(it.map { it.z }.joinToString("")) }

fun List<List<Point>>.printV() = this.forEach { println(it.map { it.value }.joinToString("")) }

fun List<List<List<Point>>>.printV3D() = this.forEach {
    it.forEach {
        println(it.map { it.value }.joinToString(""))
    }
    println()
}

data class PointAndNeighbours(val point: Point, val neighbours: List<Point>)
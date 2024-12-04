package advent2023.day22

import lib.*
import java.io.File
import java.util.*

const val day = 22
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val bricks = input.map { Brick.createBrick(it) }.sortedBy { it.fullBrick[0].z }
        val space = initEmpty3DGrid(
            endX = bricks.maxOf { it.end.x },
            endY = bricks.maxOf { it.end.y },
            endZ = bricks.maxOf { it.end.z }
        )
        space.changePoints3D(space.allPoints3D().filter { it.z == 0 }.toSet(), '#')
        
        for (brick in bricks) {
            val zValueObjectBelow = if (brick.isHorizontal()) {
                brick.fullBrick.map { it.lookDown(space) }
                    .transpose().first { it.any { it.value == '#' } }[0].z
            } else {
                brick.fullBrick[0].lookDown(space).first { it.value == '#' }.z
            }
            brick.moveDown(brick.fullBrick[0].z - (zValueObjectBelow + 1))
            space.changePoints3D(brick.fullBrick.toSet(), '#')
        }
        println(bricks.count { it.isSafeToDisintegrate(bricks) })
    }

    fun runPart2() {
        val bricks = input.map { Brick.createBrick(it) }.sortedBy { it.fullBrick[0].z }
        val space = initEmpty3DGrid(
            endX = bricks.maxOf { it.end.x },
            endY = bricks.maxOf { it.end.y },
            endZ = bricks.maxOf { it.end.z }
        )
        space.changePoints3D(space.allPoints3D().filter { it.z == 0 }.toSet(), '#')

        for (brick in bricks) {
            val zValueObjectBelow = if (brick.isHorizontal()) {
                brick.fullBrick.map { it.lookDown(space) }
                    .transpose().first { it.any { it.value == '#' } }[0].z
            } else {
                brick.fullBrick[0].lookDown(space).first { it.value == '#' }.z
            }
            brick.moveDown(brick.fullBrick[0].z - (zValueObjectBelow + 1))
            space.changePoints3D(brick.fullBrick.toSet(), '#')
        }
        
        val bricksByLowLevel = bricks.groupBy { it.fullBrick[0].z }
        val bricksByHighLevel = bricks.groupBy { it.fullBrick.last().z }
        val supports = mutableMapOf<Brick, Set<Brick>>()
        val pq = PriorityQueue<Brick>( compareBy { it.fullBrick[0].z })
        for (brick in bricks) {
            val currentlyFallingBricks = mutableSetOf(brick)
            pq.add(brick)
            // go over block, add new blocks to queue
            while (pq.isNotEmpty()) {
                val currentBrick = pq.remove()
                val currentLevel = currentBrick.fullBrick.last().z
                // get bricks on top of the current brick
                val supportedBricks = currentBrick.findSupportedBricks(bricksByLowLevel.getOrDefault(currentLevel + 1, emptyList()))
                // get all the bricks that support the bricks on top, they should all be falling as well
                val newFallingBricks = supportedBricks
                    .filter { it.isSupportedBy(bricksByHighLevel.getValue(currentLevel)).all { it in currentlyFallingBricks } }
                currentlyFallingBricks.addAll(newFallingBricks)
                pq.addAll(newFallingBricks)
            }
            supports[brick] = currentlyFallingBricks - brick
        }
        println(
            supports.values.sumOf { it.size }
        )
    }
}

data class Brick(val start: Point, val end: Point, val fullBrick: List<Point> = start.getPointsInLineTo3D(end)) {
    fun moveDown(dz: Int) {
        this.start.z -= dz
        this.end.z -= dz
        this.fullBrick.forEach { it.z -= dz }
    }

    fun isSupportedBy(bricks: List<Brick>): List<Brick> =
        bricks.filter { this.isOnTopOf(it) }

    fun isSafeToDisintegrate(bricks: List<Brick>): Boolean {
        val supportedBricks = findSupportedBricks(bricks)
        // is not supporting anything
        if (supportedBricks.isEmpty()) return true
        // all bricks it supports are supported by multiple bricks
        return supportedBricks.all { it.isSupportedBy(bricks).size > 1 }
    }

    fun findSupportedBricks(bricks: List<Brick>) =
        bricks.minusElement(this).filter { it.isOnTopOf(this) }

    fun isOnTopOf(other: Brick) = other.fullBrick.any { it in this.fullBrick.map { it.copy(z = this.start.z - 1) } }
    
    fun isHorizontal(): Boolean = start.z == end.z
    
    fun findFallingBricksOnTop(
        bricksByLowLevel: Map<Int, List<Brick>>,
        bricksByHighLevel: Map<Int, List<Brick>>,
        currentlyFallingBricks: Set<Brick>
    ): Set<Brick> {
        val currentLevel = fullBrick.last().z
        val supportedBricks = findSupportedBricks(bricksByLowLevel.getOrDefault(currentLevel + 1, emptyList()))
        // if none on top, return set of this?
        if (supportedBricks.isEmpty()) return setOf()

        // else return set of this + bricks on top that are supported only by already falling bricks 
        val newFallingBricks = supportedBricks
            .filter { it.isSupportedBy(bricksByHighLevel.getValue(currentLevel)).all { it in currentlyFallingBricks } }
        return newFallingBricks.toSet() + newFallingBricks
            .flatMap { it.findFallingBricksOnTop(
                bricksByLowLevel,
                bricksByHighLevel,
                currentlyFallingBricks + newFallingBricks
            ) }
            .toSet()
    }

    companion object {
        //1,0,1~1,2,1
        fun createBrick(input: String): Brick {
            val (start, end) = input.split("~").map { it.split(",") }.mapToPair(
                transformLeft = { Point(it[0], it[1], it[2]) },
                transformRight = { Point(it[0], it[1], it[2]) }
            )
            return Brick(start, end)
        }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
//    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

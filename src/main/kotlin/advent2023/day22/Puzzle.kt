package advent2023.day22

import advent2023.utils.*
import java.io.File

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
        space.changePoints3D(space.allPoints().filter { it.z == 0 }.toSet(), '#')
        
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
        space.changePoints3D(space.allPoints().filter { it.z == 0 }.toSet(), '#')

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
        
        val supports = mutableMapOf<Brick, Set<Brick>>()
        for (brick in bricks.sortedByDescending { it.fullBrick.last().z }) {
            val supportedBricks = brick.findSupportedBricks(bricks)
            supports[brick] = supportedBricks.flatMap { setOf(it) + supports.getValue(it) }.toSet()
//            supports.forEach { println("${it.key.start}: ${it.value.map { it.start }.size }" ) }
//            println()
        }
        println(
//            supports.filterNot { it.key.isSafeToDisintegrate(bricks) }.size
            supports.filterNot { it.key.isSafeToDisintegrate(bricks) }.values.sumOf { it.size }

//            supports.filterNot { it.key.isSafeToDisintegrate(bricks) }.forEach { println("${it.key.start}: ${it.value.size}") }
//            supports.forEach { println("${it.key.fullBrick}: ${it.value.size} ${it.value.map { it.fullBrick }}") }

//            supports.filterNot { it.key.isSafeToDisintegrate(bricks) }
//                .forEach { println("${it.key.fullBrick}: ${it.value.size} ${it.value.map { it.fullBrick }}") }
        )
        //118846 too high?
    }
}

data class Brick(val start: Point, val end: Point, val fullBrick: List<Point> = start.getPointsInLineTo3D(end)) {
    fun moveDown(dz: Int) {
        this.start.z -= dz
        this.end.z -= dz
        this.fullBrick.forEach { it.z -= dz }
    }

    private fun isSupportedBy(bricks: List<Brick>): List<Brick> =
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
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

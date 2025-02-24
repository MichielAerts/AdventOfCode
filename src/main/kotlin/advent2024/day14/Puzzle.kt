package advent2024.day14

import lib.groupAsInt
import lib.product
import lib.runPuzzle
import java.io.File

const val day = 14
val file = File("src/main/resources/advent2024/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val roomSizeX = 101
        val roomSizeY = 103
        val robots = input.map { Robot.toRobot(it) }
        val room = Room(roomSizeX, roomSizeY, robots)
        val time = 100
        room.moveRobots(time)
        println(room.calcSafetyFactor())
    }

    fun runPart2() {
        val roomSizeX = 101
        val roomSizeY = 103
        val robots = input.map { Robot.toRobot(it) }
        val room = Room(roomSizeX, roomSizeY, robots)
        val prints = 100
        val points = (0..prints).map { 40 + 103 * it }.toMutableList()
        points.addAll((0..prints).map { 99 + 101 * it }.toList())
        for (i in points) {
            println("time: $i")
            room.moveRobots(i)
            room.printRoom()
        }
    }
}

data class Room(val xSize: Int, val ySize: Int, val robots: List<Robot>) {
    fun moveRobots(time: Int) {
        robots.forEach {
            with(it) {
                val x = (p0.x + v.vx * time) % xSize
                val y = (p0.y + v.vy * time) % ySize
                p = Position(
                    x = if (x >= 0) x else x + xSize,
                    y = if (y >= 0) y else y + ySize
                )
            }
        }
    }

    fun calcSafetyFactor(): Int {
        return robots.mapNotNull { inQuadrant(it.p) }.groupingBy { it }.eachCount().values.toList().product()
    }
    
    fun inQuadrant(p: Position): Int? {
        val xQ = when(p.x) {
            in 0..(xSize / 2 - 1) -> 1
            in (xSize / 2 + 1) ..< xSize -> 2
            else -> null
        }
        val yQ = when(p.y) {
            in 0..(ySize / 2 - 1) -> 0
            in (ySize / 2 + 1) ..< ySize -> 2
            else -> null
        }
        if (xQ == null || yQ == null) return null
        return xQ + yQ
    }

    fun printRoom() {
        val robotPositions = robots.map { it.p }.toSet()
        
        for (y in 0..< ySize) {
            for (x in 0 ..< xSize) {
                val p = Position(x, y)     
                if (p in robotPositions) {
                    print('X')
                } else {
                    print('.')
                }
            }
            print("\n")
        }
    }

}
data class Robot(val p0: Position, val v: Velocity, var p: Position = p0) {
    
    companion object {
        val regex = "\\w=(?<x>-?\\d+),(?<y>-?\\d+)".toRegex()
        fun toRobot(input: String): Robot {
            //p=9,5 v=-3,-3
            val (p, v) = input.split(" ")
            return Robot(
                p0 = Position(regex.groupAsInt(p, "x"), regex.groupAsInt(p, "y")),
                v = Velocity(regex.groupAsInt(v, "x"), regex.groupAsInt(v, "y"))
            )
        }
    }
}
data class Position(val x: Int, val y: Int)
data class Velocity(val vx: Int, val vy: Int)
fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
package advent2018.day3

import lib.*
import java.io.File

const val day = 3
val file = File("src/main/resources/advent2018/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val claims = input.map { Claim.toClaim(it) }
        val fabric = initEmptyGrid(endX = 1000, endY = 1000)
        claims.forEach { claim -> 
            val (tlX, tlY) = claim.topLeft
            val area = fabric.getSquare(tlX, tlX + claim.wide - 1, tlY, tlY + claim.tall - 1).allPoints()
            area.forEach { p ->
                p.value = when(p.value) {
                    '.' -> '1'
                    '1' -> 'X'
                    else -> p.value
                }
            }
        }
        println(fabric.allPoints().count { it.value == 'X' })
        claims.forEach { claim ->
            val (tlX, tlY) = claim.topLeft
            val area = fabric.getSquare(tlX, tlX + claim.wide - 1, tlY, tlY + claim.tall - 1).allPoints()
            if (area.all { it.value == '1' }) {
                println(claim.id)
            }
        }
    }

    fun runPart2() {
        println(input)
    }
}

data class Claim(val id: Int, val topLeft: Point, val wide: Int, val tall: Int) {
    companion object {
        val regex = Regex("#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)")
        fun toClaim(input: String): Claim {
            //#123 @ 3,2: 5x4
            val (id, x, y, w, t) = regex.allGroups(input).map { it.toInt() }
            return Claim(id, Point(x, y), w, t)
        }
    }
}
fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
package advent2023.day21

import lib.*
import java.io.File

const val day = 21
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val garden = input.to2DGridOfPointsWithValues()
        val tilesToReach = findTilesToReach(garden, 6)

        println(tilesToReach.getValue(64).size)
    }

    private fun findTilesToReach(garden: List<List<Point>>, steps: Int): MutableMap<Int, Set<Point>> {
        val size = garden.size
        val tilesToReach = mutableMapOf(0 to setOf(garden[size / 2][size / 2]))
        for (step in 1..steps) {
            val currentPositions = tilesToReach.getValue(step - 1)
            val newPositions = currentPositions
                .flatMap { garden.getDirectNeighbours(it).neighbours.filter { it.value != '#' } }.toSet()
            tilesToReach[step] = newPositions
//            println("step $step: tiles reached = ${newPositions.size}")
        }
//        println(tilesToReach)
        return tilesToReach
    }

    fun runPart2() {
        val copyFactor = 7
        val garden = (input.map { it.repeat(copyFactor) } ).repeat(copyFactor).to2DGridOfPointsWithValues()
//        garden.printV()
        val steps = 65 + 3 * 131 
        val tilesToReach = findTilesToReach(garden, steps)

        val points = tilesToReach.getValue(steps)
        garden.map { row -> row.map { if (it in points) 'O' else it.value } }.forEach { println(it.joinToString("")) }

        println(points.size)
        val counts = mutableMapOf<Pair<Int, Int>, Int>()
        for (x in 0..<copyFactor) {
            for (y in 0..<copyFactor) {
                val number = garden.flatten().count {
                    it in points && it.x / 131 == x && it.y / 131 == y }
                counts[Pair(x, y)] = number
            }
        }
        println(counts)
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
//    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

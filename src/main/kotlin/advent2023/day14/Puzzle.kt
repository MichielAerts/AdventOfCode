package advent2023.day14

import advent2023.utils.*
import advent2023.utils.WindDirection.*
import java.io.File

const val day = 14
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        println(
            input.to2DGridOfPointsWithValues()
                .tilt(N)
                .mapIndexed { index, row -> row.count { it.value == 'O' } * (input.size - index) }
                .sum()
        )
    }
    
    fun runPart2() {
        var platform = input.to2DGridOfPointsWithValues()
        val tilts = listOf(N, W, S, E)
        val originalCycles = 1_000_000_000
        val cycles = 10000
        platform.printV()
        println()
        (1..cycles).forEach { 
            tilts.forEach {
                platform = platform.tilt(it)
            }
//            println("after cycle $it")
//            platform.printV()
            println("cycle $it ${platform.mapIndexed { index, row -> row.count { it.value == 'O' } * (input.size - index) }.sum()}")
        }
        println((originalCycles - 10000) % 42)
    }
}

private fun List<List<Point>>.tilt(dir: WindDirection): List<List<Point>> {
    for (num in indices) {
        val line = if (dir in listOf(N, S)) getColumn(num) else getRow(num)
        val sections = line.splitBy { it.value == '#' }
        for (section in sections) {
            val values = section.map { it.value }
            val toBe = if (dir in listOf(N, W)) values.sortedDescending() else values.sorted()
            for (idx in section.indices) {
                changePoint(section[idx], toBe[idx])
            }
        }
    }
    return this
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
//    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

package advent2024.day19

import lib.mapToPair
import lib.runPuzzle
import lib.splitBy
import java.io.File

const val day = 19
val file = File("src/main/resources/advent2024/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val (towels, designs) = input.splitBy { it.isEmpty() }
            .mapToPair(transformLeft = { it[0].split(", ") }, transformRight = { it })
        println(designs.count { it.canBeMadeWith(towels) })
    }

    fun runPart2() {
        val (towels, designs) = input.splitBy { it.isEmpty() }
            .mapToPair(transformLeft = { it[0].split(", ") }, transformRight = { it })
//        println(designs.forEach { println("$it: ${it.waysToBeMadeWith(towels)} ways") })
        println(designs.sumOf { it.waysToBeMadeWith(towels) })
    }
}

val canBeMadeCache = mutableMapOf<String, Boolean>()

private fun String.canBeMadeWith(towels: List<String>): Boolean {
    if (this.isEmpty()) return true

    return canBeMadeCache.getOrPut(this) {
        towels.any { this.startsWith(it) && this.substringAfter(it).canBeMadeWith(towels) }
    }
}

val waysToBeMadeCache = mutableMapOf<String, Long>()
    
private fun String.waysToBeMadeWith(towels: List<String>): Long {
    if (this.isEmpty()) return 1L

    return waysToBeMadeCache.getOrPut(this) {
        towels.filter { this.startsWith(it) }.sumOf { this.substringAfter(it).waysToBeMadeWith(towels) }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
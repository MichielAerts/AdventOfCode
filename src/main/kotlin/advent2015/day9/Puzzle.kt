package advent2015.day9

import lib.allPermutations
import lib.runPuzzle
import java.io.File

const val day = 9
val file = File("src/main/resources/advent2015/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val distances = input.map { Distance.toDistance(it) }.flatMap { listOf(it, Distance(it.city2, it.city1, it.distance))}
        val cities = distances.flatMap { listOf(it.city1) }.toSet()
        val map = distances.groupBy({ it.city1 }).mapValues { it.value.associate { it.city2 to it.distance } }
        val permutations = allPermutations(cities)
        println(permutations.minOf { it.zipWithNext().sumOf { map.getValue(it.first).getValue(it.second) } })
    }

    fun runPart2() {
        val distances = input.map { Distance.toDistance(it) }.flatMap { listOf(it, Distance(it.city2, it.city1, it.distance))}
        val cities = distances.flatMap { listOf(it.city1) }.toSet()
        val map = distances.groupBy({ it.city1 }).mapValues { it.value.associate { it.city2 to it.distance } }
        val permutations = allPermutations(cities)
        println(permutations.maxOf { it.zipWithNext().sumOf { map.getValue(it.first).getValue(it.second) } })
    }
}

data class Distance(val city1: String, val city2: String, val distance: Int) {
    companion object {
        val rgx = "(\\w+) to (\\w+) = (\\d+)".toRegex()
        fun toDistance(input: String): Distance {
            val (_, city1, city2, distance) = rgx.find(input)!!.groupValues
            return Distance(city1, city2, distance.toInt())
        }       
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
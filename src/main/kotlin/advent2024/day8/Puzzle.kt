package advent2024.day8

import lib.*
import java.io.File

const val day = 8
val file = File("src/main/resources/advent2024/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val map = input.to2DGridOfPointsWithValues()
        val antennas = map.allPoints().filter { it.value != '.' && it.value != '#' }
        val antinodes = antennas.groupBy { it.value }
            .flatMap { it.value.allPairs() }
            .flatMap { map.getAntinodes(it) }
            .toSet()
        println(antinodes.size)
    }

    fun runPart2() {
        val map = input.to2DGridOfPointsWithValues()
        val antennas = map.allPoints().filter { it.value != '.' && it.value != '#' }
        val antinodes = antennas.groupBy { it.value }
            .flatMap { it.value.allPairs() }
            .flatMap { map.getAntinodesWithResonantHarmonics(it) }
            .toSet()
        println(antinodes.size)
    }
}

private fun List<List<Point>>.getAntinodes(pair: Pair<Point, Point>): List<Point> {
    val distance = pair.first.getDistanceTo(pair.second)
    return listOfNotNull(
        getPointFrom(pair.second, distance),
        getPointFrom(pair.first, distance.negate())
    )
}

private fun List<List<Point>>.getAntinodesWithResonantHarmonics(pair: Pair<Point, Point>): List<Point> {
    val distance = pair.first.getDistanceTo(pair.second)
    
    val antiNodes = mutableListOf<Point>(pair.first, pair.second)
    var newDistance = distance
    while(getPointFrom(pair.second, newDistance) != null) {
        antiNodes += getPointFrom(pair.second, newDistance)!!
        newDistance += distance 
    }
    newDistance = distance.negate()
    while(getPointFrom(pair.first, newDistance) != null) {
        antiNodes += getPointFrom(pair.first, newDistance)!!
        newDistance -= distance
    }
    return antiNodes
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
package advent2023.day11

import lib.*
import org.paukov.combinatorics3.Generator
import java.io.File

const val day = 11
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val universe = input.to2DGridOfPointsWithValues()
        val galaxies = universe.flatten().filter { it.value == '#' }
        val rowsWithoutGalaxies = universe.mapIndexedNotNull { idx, row -> if (row.none { it.value == '#' }) idx else null }
        val columnsWithoutGalaxies = universe.transpose().mapIndexedNotNull { idx, row -> if (row.none { it.value == '#' }) idx else null }
        val pairs = Generator.combination(galaxies).simple(2).stream().toList().map { it.toPair() }
        val expansionFactor = 1
        println(pairs.sumOf { galaxyPair ->
            galaxyPair.first.getManhattanDistance(galaxyPair.second) +
                    rowsWithoutGalaxies.count { it in galaxyPair.getYRange() } * expansionFactor +
                    columnsWithoutGalaxies.count { it in galaxyPair.getXRange() } * expansionFactor
        })
    }
    
    fun runPart2() {
        val universe = input.to2DGridOfPointsWithValues()
        val galaxies = universe.flatten().filter { it.value == '#' }
        val rowsWithoutGalaxies = universe.mapIndexedNotNull { idx, row -> if (row.none { it.value == '#' }) idx else null }
        val columnsWithoutGalaxies = universe.transpose().mapIndexedNotNull { idx, row -> if (row.none { it.value == '#' }) idx else null }
        val pairs = Generator.combination(galaxies).simple(2).stream().toList().map { it.toPair() }
        val expansionFactor = 1000000L - 1
        println(pairs.sumOf { galaxyPair ->
            galaxyPair.first.getManhattanDistance(galaxyPair.second) +
                    rowsWithoutGalaxies.count { it in galaxyPair.getYRange() } * expansionFactor +
                    columnsWithoutGalaxies.count { it in galaxyPair.getXRange() } * expansionFactor
        })
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

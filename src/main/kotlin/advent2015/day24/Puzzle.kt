package advent2015.day24

import lib.product
import lib.runPuzzle
import org.paukov.combinatorics3.Generator
import java.io.File

const val day = 24
val file = File("src/main/resources/advent2015/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val packages = input.map { it.toInt() }.toSet()
        val weight = packages.sum() / 4
        var number = 1
        val firstGroups = mutableSetOf<List<Int>>()
        while (firstGroups.isEmpty()) {
            //find combinations of size number, add to first groups
            firstGroups.addAll(Generator.combination(packages).simple(number).stream()
                .filter { it.sum() == weight }.toList())
            number++
        }
        println(firstGroups.minOf { it.map { it.toLong() }.product()})
    }

    fun runPart2() {
        println(input)
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
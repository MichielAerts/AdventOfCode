package advent2017.day21

import lib.*
import java.io.File

const val day = 21
val file = File("src/main/resources/advent2017/day${day}/input")

class Puzzle(private val input: List<String>) {

    val cache = mutableMapOf<List<List<Point>>, List<List<Point>>>()

    fun runPart1() {
        val rules = input.map { Rule.toRule(it) }
        var pixels = """
            .#.
            ..#
            ###
        """.trimIndent().lines().to2DGridOfPointsWithValues()
        
        val iterations = 18
        for (i in 1..iterations) {
            val size = pixels.size
            val splitSize = size.split()
            val oldSquares = pixels.inSquares(splitSize)
            val newSquares = oldSquares.map { row -> row.map { sq -> rules.findSquare(sq) } }
            pixels = newSquares.expandFromSquares(splitSize + 1)
        }
        println(pixels.allPoints().count { it.value == '#' })
    }

    private fun List<Rule>.findSquare(
        sq: List<List<Point>>
    ): List<List<Point>> = cache.getOrPut(sq) { first { rule -> rule.matches(sq) }.output }

    private fun Int.split(): Int {
        val splitSize = when {
            this % 2 == 0 -> 2
            this % 3 == 0 -> 3
            else -> throw IllegalStateException()
        }
        return splitSize
    }

    fun runPart2() {
        println(input)
    }
}

data class Rule(val input: List<List<Point>>, val output: List<List<Point>>) {
    fun matches(inputSquare: List<List<Point>>): Boolean = 
        input.allRotations().any { it == inputSquare }

    companion object {
        fun toRule(input: String): Rule {
            //##/## => ##./..#/#.#
            val (input, output) = input.split(" => ").map { it.split("/").to2DGridOfPointsWithValues() }
            return Rule(input, output)
        }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
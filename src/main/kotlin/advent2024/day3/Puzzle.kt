package advent2024.day3

import lib.runPuzzle
import java.io.File

const val day = 3
val file = File("src/main/resources/advent2024/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val rgx = Regex("mul\\((\\d+),(\\d+)\\)")
        val sum = input.sumOf { line ->
            rgx.findAll(line).sumOf {
                val (f, s) = it.destructured
                f.toInt() * s.toInt()
            }
        }
        println(sum)
    }

    fun runPart2() {
        val rgx = Regex("(mul\\((\\d+),(\\d+)\\))|(do\\(\\))|(don't\\(\\))")
        val groups = input.flatMap { line -> rgx.findAll(line) }
        var enabled = true
        var count = 0
        for (grp in groups) {
            when(grp.value) {
                "do()" -> enabled = true
                "don't()" -> enabled = false
                else -> {
                    if (enabled) {
                        val (_, f, s) = grp.destructured
                        count += f.toInt() * s.toInt()
                    }
                } 
            }
        }
        println(count)
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
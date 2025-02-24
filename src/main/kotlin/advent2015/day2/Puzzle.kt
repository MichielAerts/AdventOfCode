package advent2015.day2

import lib.runPuzzle
import java.io.File

const val day = 2
val file = File("src/main/resources/advent2015/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        println(input.map { Box.toBox(it) }.sumOf { it.wrappingPaper() })
    }

    fun runPart2() {
        println(input.map { Box.toBox(it) }.sumOf { it.ribbon() })
    }
}

data class Box(val l: Int, val w: Int, val h: Int) {

    fun wrappingPaper(): Int =
        2 * l * w + 2 * w * h + 2 * h * l + minOf(l * w, l * h, w * h)


    fun ribbon(): Int {
        val smallestPerimeter = minOf(l + w, l + h, w + h) * 2
        val bow = l * w * h
        return smallestPerimeter + bow
    }

    companion object {
        fun toBox(input: String): Box {
            val (l, w, h) = input.split("x").map { it.toInt() }
            return Box(l, w, h)
        }
    }
}
fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
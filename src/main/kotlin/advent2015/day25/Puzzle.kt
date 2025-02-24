package advent2015.day25

import lib.runPuzzle
import java.io.File

const val day = 25
val file = File("src/main/resources/advent2015/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        //To continue, please consult the code grid in the manual.  Enter the code at row 2978, column 3083.
        val r = 2978L
        val c = 3083L
//        val c = 6
//        val r = 5
        val no = (1..c).sum() + (c..<(c + r - 1)).sum()
        println(no)
        var result = 20151125L
        for (i in 2.. no) {
            result = (result * 252533) % 33554393
        }
        println(result)
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
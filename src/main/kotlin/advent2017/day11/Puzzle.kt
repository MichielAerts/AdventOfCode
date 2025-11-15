package advent2017.day11

import advent2017.day11.HexDirection.*
import lib.runPuzzle
import java.io.File

const val day = 11
val file = File("src/main/resources/advent2017/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val counts = input[0].split(",").groupingBy { it }.eachCount()
        val s = counts.getValue("s") - counts.getValue("n")
        val se = counts.getValue("se") - counts.getValue("nw")
        val ne = counts.getValue("ne") - counts.getValue("sw")
        //s + ne = se
        val steps = se + ne
        println("steps: $steps")
    }
    
    fun runPart2() {
        val moves = input[0].split(",")
        val visitedHexagons = mutableListOf(HexPoint(0, 0))
        for (move in moves) {
            val direction = HexDirection.valueOf(move.uppercase())
            visitedHexagons += visitedHexagons.last().next(direction)             
        }
        val max = visitedHexagons.maxBy { it.q + it.r }
        
        println(max.q + max.r)
    }
}

data class HexPoint(val q: Int, val r: Int) {
    fun next(direction: HexDirection) = 
        when(direction) {
            N -> HexPoint(q + 1, r - 1)
            NE -> HexPoint(q + 1, r)
            NW -> HexPoint(q, r - 1)
            S -> HexPoint(q - 1, r + 1)
            SE -> HexPoint(q, r + 1)
            SW -> HexPoint(q - 1, r)
        }
}

enum class HexDirection {
    N, NE, NW, S, SE, SW
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
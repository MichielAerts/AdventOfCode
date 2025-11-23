package advent2017.day16

import lib.*
import java.io.File

const val day = 16
val file = File("src/main/resources/advent2017/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        var line = ('a'..'p').toList().associateBy { it - 'a' }.toMutableMap()
        val moves = input[0].split(",")
        val times = 1_000_000_000
        val interval = 36
        //line repeats with 36 interval
        for (i in 1..(times % interval)) {
            line = line.dance(moves)
        }

        println(line.values.joinToString(separator = ""))
    }

    
    fun runPart2() {
        println(input)
    }
}

val swapPositions = Regex("x(\\d+)/(\\d+)")
val swapPrograms = Regex("p(\\w+)/(\\w+)")

private fun MutableMap<Int, Char>.dance(move: String): MutableMap<Int, Char> {
    when(move[0]) {
        's' -> {
            val spinSize = this.size - move.drop(1).toInt()
            val newOrder = values.toList().rotate(spinSize)
            return newOrder.mapIndexed { idx, c -> idx to c }.toMap().toMutableMap()
        }
        'x' -> {
            val (firstPos, secondPos) = swapPositions.allGroups(move).map { it.toInt() }
            return swapKeys(firstPos, secondPos)
        }
        'p' -> {
            val (firstProgram, secondProgram) = swapPrograms.allGroups(move).map { it[0] }
            val firstPos = findEntryWithValue(firstProgram).key
            val secondPos = findEntryWithValue(secondProgram).key
            return swapKeys(firstPos, secondPos)
        }
        else -> throw UnsupportedOperationException()
    }
}

private fun MutableMap<Int, Char>.dance(moves: List<String>): MutableMap<Int, Char> {
    var line = this
    for (move in moves) {
        line = line.dance(move)
    }
    return line
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
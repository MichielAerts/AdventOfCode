package advent2018.day2

import lib.runPuzzle
import java.io.File

const val day = 2
val file = File("src/main/resources/advent2018/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val map = input.map { it.toList().groupingBy { it }.eachCount() }
        println(map.count { 2 in it.values } * map.count { 3 in it.values })
    }

    fun runPart2() {
        val pairs = input.flatMapIndexed { 
            idx, first -> ((idx + 1)..<input.size).map { input[it] }.map { second -> Pair(first, second) } 
        }
        val (first, second) = pairs.first { it.first.zip(it.second).count { it.first != it.second } == 1 }
        println(first.zip(second).filter { it.first == it.second }.joinToString("") { it.first.toString() })
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
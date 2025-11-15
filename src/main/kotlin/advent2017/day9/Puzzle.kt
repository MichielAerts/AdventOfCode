package advent2017.day9

import lib.runPuzzle
import java.io.File

const val day = 9
val file = File("src/main/resources/advent2017/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val record = input[0]
        val recordWithoutCancelChars = record.replace(Regex("!."), "")
        val recordWithoutGarbage = recordWithoutCancelChars.replace(Regex("<.*?>"), "")
        println(recordWithoutGarbage)
        val chars = recordWithoutGarbage.toList()
        var score = 0
        var nesting = 1
        for (c in chars) {
            when (c) {
                '{' -> {
                    score += nesting
                    nesting++
                }

                '}' -> nesting--
                ',' -> {}
                else -> throw IllegalStateException("unexpected char $c")
            }
        }
        println(score)
    }

    fun runPart2() {
        val record = input[0]
        val recordWithoutCancelChars = record.replace(Regex("!."), "")
        val regex = Regex("<.*?>")
        val recordWithoutGarbage = recordWithoutCancelChars.replace(Regex("<.*?>")) { "0".repeat(it.value.length - 2) }
        println(recordWithoutGarbage)
        println(recordWithoutGarbage.toList().count { it == '0' })
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
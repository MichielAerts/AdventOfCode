package advent2017.day6

import lib.runPuzzle
import java.io.File

const val day = 6
val file = File("src/main/resources/advent2017/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val banks = input[0].split(Regex("\\s+")).map { it.toInt() }.toMutableList()
        val size = banks.size
        
        val allConfigurations = mutableMapOf(0 to banks.toList())
        val tries = 10000
        for (i in 1..tries) {
            var index = banks.indexOf(banks.max())
            val blocks = banks[index]
            banks[index] = 0
            index = index.nextInLoop(size) 
            for (block in 1..blocks) {
                banks[index]++
                index = index.nextInLoop(size)
            }
            if (allConfigurations.containsValue(banks)) {
                println("found double at ${i}")
                val previousEntry = allConfigurations.entries.find { it.value == banks }
                println("configuration found at $previousEntry, diff = ${i - previousEntry!!.key}")
                break
            } else {
                allConfigurations[i] = banks.toList()
            }
        }
    }

    private fun Int.nextInLoop(size: Int): Int = if (this == size - 1) 0 else this + 1

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
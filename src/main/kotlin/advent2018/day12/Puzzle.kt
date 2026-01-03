package advent2018.day12

import lib.runPuzzle
import java.io.File

const val day = 12
val file = File("src/main/resources/advent2018/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val buffer = 50
        var plants = ".".repeat(buffer) + input[0].substringAfter("initial state: ") + ".".repeat(buffer)
        val rules = input.drop(2).map { Rule.toRule(it) }.associate { it.input to it.output } 
        for (g in 1..20) {
            plants = plants.windowed(5)
                .map { slice -> rules[slice] ?: '.' }
                .joinToString("", prefix = "..", postfix = "..")
        }
        println("${plants.mapIndexed { idx, c -> if (c == '#') idx - buffer else 0 }.sum()}")
    }

    fun runPart2() {
        val buffer = 21000
        var plants = ".".repeat(buffer) + input[0].substringAfter("initial state: ") + ".".repeat(buffer)
        val rules = input.drop(2).map { Rule.toRule(it) }.associate { it.input to it.output }
        val results = mutableMapOf<Int, Int>()
        for (g in 1..(buffer - 1000)) {
            plants = plants.windowed(5)
                .map { slice -> rules[slice] ?: '.' }
                .joinToString("", prefix = "..", postfix = "..")
            if (g % 1000 == 0) {
                val sum = plants.mapIndexed { idx, c -> if (c == '#') idx - buffer else 0 }.sum()
                println("$g: $sum")
                results[g] = sum
            }
        }
        results.entries.zipWithNext().forEach { println("${it.second.key}: ${it.second.value}, delta = ${it.second.value - it.first.value}") }
        val result = ((50_000_000_000 - 20_000) / 1000) * 88000 + 1760304
        println(result)
    }
}

data class Rule(val input: String, val output: Char) {
    companion object {
        fun toRule(input: String): Rule {
            val (pattern, out) = input.split(" => ")
            return Rule(pattern, out[0])
        }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
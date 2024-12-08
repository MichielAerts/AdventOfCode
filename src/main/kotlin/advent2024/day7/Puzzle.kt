package advent2024.day7

import lib.runPuzzle
import java.io.File
import java.math.BigInteger

const val day = 7
val file = File("src/main/resources/advent2024/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val equations = input.map { Equation.toEquation(it) }
        val operations = listOf<(Long, Long) -> Long>(
            { acc, e -> acc + e },
            { acc, e -> acc * e },
        )
        println(equations.filter { it.canBeMadeTrue(operations) }.sumOf { it.result })
    }

    fun runPart2() {
        val equations = input.map { Equation.toEquation(it) }
        val operations = listOf<(Long, Long) -> Long>(
            { acc, e -> acc + e },
            { acc, e -> acc * e },
            { acc, e -> "$acc$e".toLong() }
        )
        println(equations.filter { it.canBeMadeTrue(operations) }.sumOf { it.result })

    }
}

data class Equation(val result: Long, val numbers: List<Long>) {

    fun canBeMadeTrue(operations: List<(Long, Long) -> Long>): Boolean {
        val numberOfOperations = operations.size
        val combinations = BigInteger.valueOf(numberOfOperations.toLong()).pow(numbers.size - 1).toInt()
        var currentResult = 0L
        for (i in 0..<combinations) {
            val currentCombination = i.toString(numberOfOperations).padStart(numbers.size - 1, '0')
            var currentNumber = numbers.first()
            currentResult = currentNumber
            for (idx in currentCombination.indices) {
                val nextNumber = numbers[idx + 1]
                currentResult = operations[currentCombination[idx].digitToInt()](currentResult, nextNumber)
            }
            if (currentResult == result) return true
        }
        return false
    }

    companion object {

        fun toEquation(input: String): Equation {
            //21037: 9 7 18 13
            val (result, numbers) = input.split(": ")
            return Equation(result.toLong(), numbers.split(" ").map { it.toLong() })
        }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
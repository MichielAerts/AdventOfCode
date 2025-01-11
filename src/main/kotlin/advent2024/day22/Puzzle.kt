package advent2024.day22

import lib.runPuzzle
import java.io.File

const val day = 22
val file = File("src/main/resources/advent2024/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        println(input.associateWith { generateSecretNumber(it.toInt()) }.values.sum())
    }

    fun runPart2() {
        val numberOfSecretNumbers = 2000
        val secretNumbers = input
            .map { (1.. numberOfSecretNumbers).fold(listOf(it.toLong())) { all, _ -> all + nextSecretNumber(all.last()) } }
            .map { it.map { it % 10 } }
        val changes = secretNumbers.map { it.zipWithNext { a, b -> b - a }.windowed(4) }
        val priceChangesToBananasSold =
            changes.mapIndexed { index, lists -> lists.zip(secretNumbers[index].subList(4, secretNumbers[index].size)).distinctBy { it.first } }
                .map { it.associate { it.first to it.second  } }
        val uniqueSequences = changes.flatten().toSet()
        
        println(uniqueSequences.maxOf { sequence ->
            priceChangesToBananasSold.sumOf { it[sequence] ?: 0L }
        })
    }

    private fun generateSecretNumber(initialSecretNumber: Int): Long {
        val numberOfSecretNumbers = 2000
        var currentSecretNumber = initialSecretNumber.toLong()
        for (i in 1..numberOfSecretNumbers) {
            currentSecretNumber = nextSecretNumber(currentSecretNumber)
        }
        return currentSecretNumber
    }

    private fun nextSecretNumber(initialSecretNumber: Long): Long {
        var currentSecretNumber = initialSecretNumber
        currentSecretNumber = ((currentSecretNumber * 64) xor currentSecretNumber) % 16777216
        currentSecretNumber = ((currentSecretNumber / 32) xor currentSecretNumber) % 16777216
        currentSecretNumber = ((currentSecretNumber * 2048) xor currentSecretNumber) % 16777216
        return currentSecretNumber
    }
}
            
fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
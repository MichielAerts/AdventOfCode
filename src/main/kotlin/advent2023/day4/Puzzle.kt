package advent2023.day4

import advent2023.utils.findGroupAsInt
import advent2023.utils.runPuzzle
import java.io.File
import kotlin.math.pow

const val day = 4
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        println(input
            .map { Card.createCard(it) }
            .map { it.countWinningNumbers() }
            .sumOf { winningNumbers -> getScore(winningNumbers) }
        )
    }

    private fun getScore(it: Int) = if (it == 0) 0 else 2.0.pow((it - 1)).toInt()

    fun runPart2() {
        val cardsWithWinningNumbers = input
            .map { Card.createCard(it) }
            .associate { it.number to it.countWinningNumbers() }
        val amounts = cardsWithWinningNumbers.mapValues { 1 }.toMutableMap()
        for ((cardNo, wins) in cardsWithWinningNumbers) {
            val amount = amounts.getValue(cardNo)
            for (win in 0..< wins) {
                amounts.merge(cardNo + 1 + win, amount) { current, new -> current + new }
            }
        }
        println(
            amounts.values.sum()
        )  
    }
}

data class Card(val number: Int, val winningNumbers: List<Int>, val ownNumbers: List<Int>) {
    fun countWinningNumbers(): Int =
        ownNumbers.intersect(winningNumbers.toSet()).size

    companion object {
        private val cardRegex = Regex("Card\\s+(?<no>\\d+)")
        //Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
        fun createCard(input: String): Card {
            val (cardInput, numbersInput) = input.split(":")
            val no = cardRegex.findGroupAsInt(cardInput, "no")
            val (winningNumbers, ownNumbers) = numbersInput.split("|").map { it.trim().split("\\s+".toRegex()).map { it.toInt() } }
            return Card(no, winningNumbers, ownNumbers)
        }
    }
}
fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

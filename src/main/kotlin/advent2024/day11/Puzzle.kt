package advent2024.day11

import lib.runPuzzle
import java.io.File

const val day = 11
val file = File("src/main/resources/advent2024/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val initialStones = input[0].split(" ").map { it.toLong() }
        var currentStones = initialStones.toList()
        val blinks = 25
        for (blink in 1..blinks) {
            println(blink)
            val newStones = mutableListOf<Long>()
            for (stone in currentStones) {
                when {
                    stone == 0L -> {
                        newStones.add(1L)
                    }

                    "$stone".length % 2 == 0 -> {
                        val l = "$stone".length
                        val (left, right) = "$stone".chunked(l / 2).map { it.toLong() }
                        newStones.addAll(listOf(left, right))
                    }

                    else -> {
                        newStones.add(stone * 2024)
                    }
                }
            }
            println("size: ${newStones.size}")
            currentStones = newStones
        }
        println()
    }

    fun runPart2() {
        val initialStones = input[0].split(" ").map { it.toLong() }
            .groupingBy { it }
            .eachCount()
            .mapValues { (_, v) -> v.toLong() }
        var currentStones = initialStones
        val blinks = 75
        for (blink in 1..blinks) {
            println(blink)
            val newStones = mutableMapOf<Long, Long>()
            for ((stone, number) in currentStones.entries) {
                when {
                    stone == 0L -> {
                        newStones.merge(1, number) { acc, n -> acc + n }
                    }

                    "$stone".length % 2 == 0 -> {
                        val l = "$stone".length
                        val (left, right) = "$stone".chunked(l / 2).map { it.toLong() }
                        newStones.merge(left, number) { acc, n -> acc + n }
                        newStones.merge(right, number) { acc, n -> acc + n }
                    }

                    else -> {
                        newStones.merge(stone * 2024, number) { acc, n -> acc + n }
                    }
                }
            }
            println(newStones.values.sum())
            currentStones = newStones
        }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
package advent2025.day2

import lib.runPuzzle
import java.io.File

const val day = 2
val file = File("src/main/resources/advent2025/day${day}/input")

class Puzzle(private val input: List<String>) {
    
    fun runPart1() {
        val ranges = input[0].split(",").map { it.split("-") }.map { Range(it[0], it[1]) }
        println(ranges.filter { it.low.toString().length != it.high.toString().length }) //split those
        println(ranges.sumOf { it.findInvalidIds().sum() })
    }

    fun runPart2() {
        val ranges = input[0].split(",").map { it.split("-") }.map { Range(it[0], it[1]) }
        println(ranges.sumOf { it.findMoreInvalidIds().sum() })
    }
}

data class Range(val low: Long, val high: Long) {
    fun findInvalidIds(): List<Long> {
        val length = low.toString().length
        if (length % 2 == 1) return emptyList()
        
        val ids = mutableListOf<Long>()
        var candidate = low.toString().take(length / 2).toLong()
        while (candidate.id() <= high) {
            if (candidate.id() >= low) ids += candidate.id()
            candidate++
        }
        return ids
    }

    fun findMoreInvalidIds(): List<Long> =
        (low..high).toList().filter { it.isInvalidId() }
    
    private fun Long.isInvalidId(): Boolean {
        val str = this.toString()
        for (l in 1..str.length / 2) {
            if (str.length % l != 0) continue
            val repeatingPart = str.take(l)
            if (repeatingPart.repeat(str.length / l) == str) return true
        }
        return false
    }
    
    private fun Long.id(): Long = "$this$this".toLong()

    constructor(low: String, high: String) : this(low.toLong(), high.toLong()) 
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
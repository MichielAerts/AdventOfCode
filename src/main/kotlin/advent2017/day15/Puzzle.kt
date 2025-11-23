package advent2017.day15

import lib.runPuzzle
import java.io.File

const val day = 15
val file = File("src/main/resources/advent2017/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val a = Generator(16807)
        val b = Generator(48271)
        var resultA = 65L
        var resultB = 8921L
        var judgeCount = 0
        val mask = 0b1111111111111111L
        repeat(40_000_000) {
            resultA = a.next(resultA)
            resultB = b.next(resultB)
            if (resultA and mask == resultB and mask) judgeCount++
        }
        println(judgeCount)
    }

    fun runPart2() {
        val a = Generator(16807)
        val b = Generator(48271)
        val valuesA = a.getValues(116L, 4)
        val valuesB = b.getValues(299L, 8)
        var judgeCount = 0
        val mask = 0b1111111111111111L
        for (i in 0..< valuesA.size) {
            if (valuesA[i] and mask == valuesB[i] and mask) judgeCount++
        }
        println(judgeCount)
    }
}

data class Generator(val factor: Long, val divisor: Long = 2147483647) {
    fun next(input: Long): Long =
        input * factor % divisor
    
    fun getValues(start: Long, div: Int): List<Long> {
        var resultA = start
        val values = mutableListOf<Long>()
        while(values.size < 5_000_000) {
            resultA = next(resultA)
            if (resultA % div == 0L) {
                values += resultA
            }
        }
        return values
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
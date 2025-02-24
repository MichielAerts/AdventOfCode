package advent2015.day4

import lib.runPuzzle
import java.io.File
import java.security.MessageDigest

const val day = 4
val file = File("src/main/resources/advent2015/day${day}/input")

class Puzzle(private val input: List<String>) {
    @OptIn(ExperimentalStdlibApi::class)
    fun runPart1() {
        val secret = input[0]
        var i = 0
        var hash = ""
        while(!hash.startsWith("00000")) {
            val candidate = secret + ++i
            val md = MessageDigest.getInstance("MD5")
            md.update(candidate.toByteArray())
            hash = md.digest().toHexString()
        }
        println(i)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun runPart2() {
        val secret = input[0]
        var i = 0
        var hash = ""
        val md = MessageDigest.getInstance("MD5")
        while(!hash.startsWith("000000")) {
            md.reset()
            val candidate = secret + ++i
            md.update(candidate.toByteArray())
            hash = md.digest().toHexString()
        }
        println(i)
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
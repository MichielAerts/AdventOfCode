package advent2018.day9

import lib.runPuzzle
import java.io.File

const val day = 9
val file = File("src/main/resources/advent2018/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val players = 447
        val scores = (1..players).associateWith { 0L }.toMutableMap()
        val lastMarble = 7151000
        var currentMarble = Marble(0).apply { 
            next = this
            previous = this
        } 
        for (no in 1..lastMarble) {
            val currentPlayer = if (no % players == 0) players else no % players 
            val newMarble = Marble(no)
            if (no % 23 == 0) {
                val seventhMarbleCounterClockwise = currentMarble
                    .previous.previous.previous.previous.previous.previous.previous
                seventhMarbleCounterClockwise.previous.next = seventhMarbleCounterClockwise.next
                seventhMarbleCounterClockwise.next.previous = seventhMarbleCounterClockwise.previous
                scores[currentPlayer] = scores.getValue(currentPlayer) + no + seventhMarbleCounterClockwise.no
                currentMarble = seventhMarbleCounterClockwise.next
            } else {
                val nextMarbleClockwise = currentMarble.next
                val secondMarbleClockwise = nextMarbleClockwise.next
                nextMarbleClockwise.next = newMarble
                newMarble.next = secondMarbleClockwise
                secondMarbleClockwise.previous = newMarble
                newMarble.previous = nextMarbleClockwise
                currentMarble = newMarble
            }
//            println("currentMarble: ${currentMarble.no}, previous: ${currentMarble.previous.no}, next: ${currentMarble.next.no}")
        }
        println("winning score is ${scores.maxOf { it.value }}")
    }

    fun runPart2() {
    }
}

class Marble(val no: Int) {
    lateinit var previous: Marble
    lateinit var next: Marble
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
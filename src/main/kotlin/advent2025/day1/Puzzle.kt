package advent2025.day1

import lib.runPuzzle
import java.io.File
import kotlin.properties.Delegates

const val day = 1
val file = File("src/main/resources/advent2025/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val dial = Dial()
        input.forEach { rotation ->
            val amount = rotation.drop(1).toInt()
            when (rotation[0]) {
                'L' -> dial.turnLeft(amount)
                'R' -> dial.turnRight(amount)
                else -> throw UnsupportedOperationException()
            }
        }
        println(dial.zeroes)
    }

    fun runPart2() {
        val dial = Dial()
        input.forEach { rotation ->
            val amount = rotation.drop(1).toInt()
            when (rotation[0]) {
                'L' -> repeat(amount) { dial.turnLeft(1) }
                'R' -> repeat(amount) { dial.turnRight(1) }
                else -> throw UnsupportedOperationException()
            }
        }
        println(dial.zeroes)
    }
}

class Dial {
    var zeroes: Int = 0
    var pointsAt: Int by Delegates.observable(50) { _, _, newValue ->
        if (newValue % 100 == 0) zeroes++
    }
    
    fun turnLeft(amount: Int) {
        pointsAt -= amount
    }

    fun turnRight(amount: Int) {
        pointsAt += amount
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
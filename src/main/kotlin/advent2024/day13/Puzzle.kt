package advent2024.day13

import lib.groupAsLong
import lib.runPuzzle
import lib.splitBy
import java.io.File

const val day = 13
val file = File("src/main/resources/advent2024/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val machines = input.splitBy { it.isEmpty() }.map { Machine.toMachine(it) }
        println(machines.sumOf { it.winPrize() ?: 0 })
    }

    fun runPart2() {
        val machines = input.splitBy { it.isEmpty() }.map { Machine.toMachineWithCorrectPrizePosition(it) }
        println(machines.sumOf { it.winFarAwayPrize() ?: 0 })
    }
}

data class Machine(val a: Button, val b: Button, val prize: Prize) {
    
    fun winPrize(): Long? {
        val options = (0..100).flatMap { a -> (0..100).map { b -> Pair(a, b) } }
        return options.filter { (pushesA, pushesB) ->
            pushesA * a.dx + pushesB * b.dx == prize.x &&
                    pushesA * a.dy + pushesB * b.dy == prize.y }
        .minOfOrNull { (pushesA, pushesB) -> pushesA * a.costToPush + pushesB * b.costToPush }
    }

    fun winFarAwayPrize(): Long? {
        val aResult: Long = (b.dx * prize.y - b.dy * prize.x) / (b.dx * a.dy - b.dy * a.dx)
        val bResult: Long = (prize.x - a.dx * aResult) / b.dx

        val xCheck = aResult * a.dx + bResult * b.dx
        val yCheck = aResult * a.dy + bResult * b.dy
        if (xCheck != prize.x || yCheck != prize.y) return null
        return aResult * a.costToPush + bResult * b.costToPush
    }
    
    companion object {
        val buttonRegex = "Button \\w: X\\+(?<x>\\d+), Y\\+(?<y>\\d+)".toRegex()
        val prizeRegex = "Prize: X=(?<x>\\d+), Y=(?<y>\\d+)".toRegex()
        fun toMachine(input: List<String>): Machine {
            /*
            Button A: X+94, Y+34
            Button B: X+22, Y+67
            Prize: X=8400, Y=5400
             */
            val (a, b, p) = input

            return Machine(
                a = Button(buttonRegex.groupAsLong(a, "x"), buttonRegex.groupAsLong(a, "y"), 3),
                b = Button(buttonRegex.groupAsLong(b, "x"), buttonRegex.groupAsLong(b, "y"), 1),
                prize = Prize(prizeRegex.groupAsLong(p, "x"), prizeRegex.groupAsLong(p, "y"))
            )
        }
        fun toMachineWithCorrectPrizePosition(input: List<String>): Machine {
            val (a, b, p) = input

            return Machine(
                a = Button(buttonRegex.groupAsLong(a, "x").toLong(), buttonRegex.groupAsLong(a, "y").toLong(), 3),
                b = Button(buttonRegex.groupAsLong(b, "x").toLong(), buttonRegex.groupAsLong(b, "y").toLong(), 1),
                prize = Prize(prizeRegex.groupAsLong(p, "x") + 10000000000000L, prizeRegex.groupAsLong(p, "y") + 10000000000000L)
            )
        }
    }
}

data class Button(val dx: Long, val dy: Long, val costToPush: Long)
data class Prize(val x: Long, val y: Long)

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
package advent2015.day14

import lib.runPuzzle
import java.io.File
import kotlin.math.min

const val day = 14
val file = File("src/main/resources/advent2015/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val reindeers = input.map { Reindeer.toReindeer(it) }
        val time = 1000
        reindeers.forEach { println("${it.name}: ${it.distance(time)}") }
    }

    fun runPart2() {
        val reindeers = input.map { Reindeer.toReindeer(it) }
        val scores = reindeers.associateWith { 0 }.toMutableMap()
        val time = 2503
        for (t in 1..time) {
            val distances = reindeers.associateWith { it.distance(t) }
            val max = distances.values.max()
            for ((r, d) in distances) {
                if (d == max) {
                    scores[r] = scores.getValue(r) + 1
                }
            }
        }
        scores.forEach { println(it) }
    }
}

data class Reindeer(val name: String, val speed: Int, val flightTime: Int, val restTime: Int) {
    
    fun distance(time: Int): Int {
        val fullCycles = time / (flightTime + restTime)
        val theRest = time % (flightTime + restTime)
        return speed * fullCycles * flightTime + min(theRest, flightTime) * speed
    }
    
    companion object {
        val regex = "(\\w+) can fly (\\d+) km/s for (\\d+) seconds, but then must rest for (\\d+) seconds.".toRegex()
        fun toReindeer(input: String): Reindeer {
            val (_, n, s, ft, rt) = regex.find(input)?.groupValues!!
            return Reindeer(name = n, speed = s.toInt(), flightTime = ft.toInt(), restTime = rt.toInt())
        }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
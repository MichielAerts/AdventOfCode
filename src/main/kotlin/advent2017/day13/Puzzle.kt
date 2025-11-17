package advent2017.day13

import lib.runPuzzle
import java.io.File

const val day = 13
val file = File("src/main/resources/advent2017/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val layers = input.map { Layer.createLayer(it) }.associateBy { it.depth }
        val maxTime = layers.keys.max()
        val totalSeverity = (0..maxTime)
            .mapNotNull { layers.get(it) }
            .filter { it.catchesPacket() }
            .sumOf { it.depth * it.range }
        println(totalSeverity)
    }

    fun runPart2() {
        val layers = input.map { Layer.createLayer(it) }.associateBy { it.depth }
        val maxTime = layers.keys.max()
        for (delay in 0..10000000) {
            val timeCaught = (0..maxTime)
                .filter { 
                    val layer = layers.get(it)
                    layer?.catchesPacket(it + delay) ?: false 
                }
                .map { layers.getValue(it) }
            if (timeCaught.isEmpty()) {
                println("found happy path at $delay")
            }
        }
    }
}

data class Layer(val depth: Int, val range: Int) {
    
    fun catchesPacket(time: Int = depth): Boolean =
        time % ((range - 1) * 2) == 0

    companion object {
        fun createLayer(input: String): Layer {
            val (depth, range) = input.split(": ")
            return Layer(depth.toInt(), range.toInt())
        }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
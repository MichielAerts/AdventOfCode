package advent2015.day12

import kotlinx.serialization.json.*
import lib.runPuzzle
import java.io.File

const val day = 12
val file = File("src/main/resources/advent2015/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val regex = "(-?\\d+)".toRegex()
        println(regex.findAll(input[0]).sumOf { it.value.toInt() })
    }

    fun runPart2() {
        val obj = Json.parseToJsonElement(input[0])
        println(sumIgnoringRedObjects(obj))
    }

    private fun sumIgnoringRedObjects(element: JsonElement): Int {
        return when(element) {
            is JsonArray -> element.sumOf { sumIgnoringRedObjects(it) }
            is JsonObject -> {
                val hasRed = element.values.any { it is JsonPrimitive && it.contentOrNull == "red" }
                if (hasRed) 0 else element.values.sumOf { sumIgnoringRedObjects(it) }
            }
            is JsonPrimitive -> element.intOrNull ?: 0
            JsonNull -> 0
        }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
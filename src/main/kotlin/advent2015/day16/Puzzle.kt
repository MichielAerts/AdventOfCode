package advent2015.day16

import lib.runPuzzle
import java.io.File

const val day = 16
val file = File("src/main/resources/advent2015/day${day}/input")

class Puzzle(private val input: List<String>) {
    val allProps = """
        children: 3
        cats: 7
        samoyeds: 2
        pomeranians: 3
        akitas: 0
        vizslas: 0
        goldfish: 5
        trees: 3
        cars: 2
        perfumes: 1
    """.trimIndent().lines().associate { it.split(": ")[0] to it.split(": ")[1].toInt() }
    fun runPart1() {
        println(input.map { Sue.toSue(it) }.first { it.props.all { 
            allProps.getValue(it.key) == it.value
        } })
    }

    fun runPart2() {
        println(input.map { Sue.toSue(it) }.first { it.props.all {
            when(it.key) {
                "cats", "trees" -> allProps.getValue(it.key) < it.value
                "pomeranians", "goldfish" -> allProps.getValue(it.key) > it.value
                else -> allProps.getValue(it.key) == it.value
            }
        } })
    }
}

data class Sue(val name: String, val props: Map<String, Int>) {
    companion object {
        fun toSue(input: String): Sue {
            val name = input.substringBefore(": ")
            val props = input.substringAfter(": ").split(", ").associate { it.split(": ")[0] to it.split(": ")[1].toInt()}
            return Sue(name, props)
        }
    }
}
fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
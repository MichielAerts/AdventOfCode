package advent2015.day19

import lib.mapToPair
import lib.runPuzzle
import lib.splitBy
import java.io.File

const val day = 19
val file = File("src/main/resources/advent2015/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val (replacements, molecule) = input.splitBy { it.isEmpty() }.mapToPair(
            transformLeft = { it.map { Replacement(Regex(it.split(" => ")[0]), it.split(" => ")[1]) } },
            transformRight = { it[0] }
        )
        val newMolecules = mutableSetOf<String>()
        for (replacement in replacements) {
            val occurences = replacement.input.findAll(molecule).map { it.range }.toList()
            occurences.forEach { newMolecules.add(molecule.replaceRange(it, replacement.output)) }
        }
        println(newMolecules.size)
    }

    fun runPart2() {
        val start = "e"
        val (replacements, target) = input.splitBy { it.isEmpty() }.mapToPair(
            transformLeft = { it.map { ReplacementWithCount(it.split(" => ")[0], it.split(" => ")[1]) }.sortedByDescending { it.outputLength } },
            transformRight = { it[0] }
        )
        val atomsInTarget = target.count { it.isUpperCase() }
        println(target)
        println(target.count { it.isUpperCase() })
        println(target.count { it == 'R' })
        println(target.count { it == 'Y' })
        println()
        val map = replacements.onEach { println("${it.input} => ${it.output}, l: ${it.outputLength}")}
        
        val two = ReplacementWithCount("A", "ASi")
        val four = ReplacementWithCount("A", "ARnASi")
        val six = ReplacementWithCount("A", "ARnAYASi")
        var newTarget = "A"
        for (t in 1..171) {
            newTarget = newTarget.replaceFirst(two.input, two.output)        
        }
        for (f in 1 .. 30) {
            newTarget = newTarget.replaceFirst(four.input, four.output)
        }
        for (s in 1 .. 6) {
            newTarget = newTarget.replaceFirst(six.input, six.output)
        }
        println(newTarget)
        println(newTarget.count { it.isUpperCase() })
        println(newTarget.count { it == 'R' })
        println(newTarget.count { it == 'Y' })
    }
}
data class ReplacementWithCount(val input: String, val output: String, val inputLenght: Int = input.count { it.isUpperCase() }, val outputLength: Int = output.count { it.isUpperCase() })

data class Replacement(val input: Regex, val output: String)
fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
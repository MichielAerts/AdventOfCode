package advent2023.day15

import advent2023.utils.runPuzzle
import java.io.File

const val day = 15
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        println(
            input[0].split(",")
                .sumOf {it.hash() }
        )
    }
    
    fun runPart2() {
        val steps = input[0].split(",").map { Step.createStep(it) }
        val boxes = (0..255).associateWith { mutableListOf<Lens>() }
        for ((label, operation, focalLength, boxNo) in steps) {
            val box = boxes.getValue(boxNo)
            when (operation) {
                '=' -> {
                    val lens = Lens(label, focalLength!!)
                    val index = box.indexOfFirst { it.label == label }
                    if (index >= 0) box[index] = lens else box.add(lens)
                }
                '-' -> {
                    box.removeIf { it.label == label }
                }
            }
        }
        println(
            boxes.entries.sumOf { 
                box -> box.value.mapIndexed { idx, lens -> (box.key + 1) * (idx + 1) * lens.focalLength }.sum() 
            }
        )
    }
}

data class Lens(val label: String, val focalLength: Int)
data class Step(val label: String, val operation: Char, val focalLength: Int?, val box: Int = label.hash().toInt()) {
    companion object {
        //rn=1,cm-
        val regex = Regex("(\\w+)([=-])(\\d*)")
        fun createStep(input: String): Step {
            val groups = regex.matchEntire(input)?.groupValues!!
            val (_, label, operation) = groups
            return Step(label, operation[0], if (operation == "=") groups.last().toInt() else null) 
        } 
    }
}

private fun String.hash(): Long =
    toList().fold(0L) { acc, char -> (acc + char.code) * 17 % 256 }

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

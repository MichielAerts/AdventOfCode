package advent2025.day10

import lib.runPuzzle
import lib.transpose
import org.paukov.combinatorics3.Generator
import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.streams.asSequence

const val day = 10
val file = File("src/main/resources/advent2025/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val machines = input.map { Machine.toMachine(it) }
        println(machines.sumOf { it.fewestButtonPressesToTurnOn() })
    }

    fun runPart2() {
        val machines = input.map { LeveredMachine.toMachine(it) }
        println(machines.sumOf { it.fewestButtonPressesToConfigureJoltage() })
    }
}

data class Machine(val indicatorLights: Int, val wiredButtons: List<Int>) {
    fun fewestButtonPressesToTurnOn(): Int {
        println(this)
        var buttonPresses = 1
        while(buttonPresses < 10) {
            val option = Generator.permutation(wiredButtons)
                .withRepetitions(buttonPresses)
                .stream().asSequence().firstOrNull { option ->
                    val result = option.fold(0, { a, b -> a xor b })
                    result == indicatorLights
                }
            if (option != null) {
                println("found a match: $option")
                return buttonPresses
            }
            buttonPresses++
        }
        throw IllegalStateException("couldn't find a solution")
    }

    companion object {
        fun toMachine(input: String): Machine {
            //[...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}
            val indicatorLights = input.substringBefore(" ")
                .drop(1).dropLast(1)
                .map { if (it == '#') '1' else '0' }.joinToString("").toInt(2)
            val length = input.substringBefore(" ").length - 2
            val wiredButtons = input.substringAfter(" ").substringBeforeLast(" ").split(" ")
                .map { it.drop(1).dropLast(1).split(",").map { it.toInt() } }
                .map { switches -> (0..<length).map { if (it in switches) '1' else '0' }.joinToString("").toInt(2) }
            
            return Machine(indicatorLights, wiredButtons)
        }
    }
}

data class LeveredMachine(val wiredButtons: List<List<Int>>, val joltageRequirements: List<Int>) {

    fun fewestButtonPressesToConfigureJoltage(): Double {
        val max = 100
        val matrix = (0..<joltageRequirements.size).map { counterIndex ->
            ((0..<wiredButtons.size)
                .map { if (counterIndex in wiredButtons[it]) 1.0 else 0.0 } + joltageRequirements[counterIndex].toDouble()).toMutableList()
        }.toMutableList()
        
        matrix.toReducedRowEchelonFormV2()
        
        val presses = matrix.calculateSolutionV2()
        val pivotVariablePositions = matrix.mapNotNull { it.indexOfFirstOrNull { it == 1.0 } }
        val freeVariablePositions = (0..<(matrix[0].size - 1)).filter { it !in pivotVariablePositions }

        val numberOfFreeVariables = freeVariablePositions.size
        var minimum = Double.MAX_VALUE
        val freeVariableSpace = Generator.permutation((0..max).toList()).withRepetitions(numberOfFreeVariables).stream().asSequence()
        for (option in freeVariableSpace) {
            val (result, solution) = presses(option)
            if (result < minimum) {
                minimum = result
            }
        }
        return minimum
    }

    companion object {
        fun toMachine(input: String): LeveredMachine {
            //[...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}
            val wiredButtons = input.substringAfter(" ").substringBeforeLast(" ").split(" ")
                .map { it.drop(1).dropLast(1).split(",").map { it.toInt() } }

            val joltageRequirements = input.substringAfterLast(" ").drop(1).dropLast(1)
                .split(",").map { it.toInt() }
            return LeveredMachine(wiredButtons, joltageRequirements)
        }
    }
}

private fun List<List<Double>>.calculateSolutionV2(): (List<Int>) -> Pair<Double, DoubleArray> {
    //assume RREF
    val transposed = this.transpose()
    val lastColumn = transposed.last()
    val pivotVariablePositions = this.mapNotNull { it.indexOfFirstOrNull { it == 1.0 } }
    val numberOfPivotVariables = pivotVariablePositions.size
    val freeVariablePositions = (0..<(this[0].size - 1)).filter { it !in pivotVariablePositions }

    val numberOfFreeVariables = freeVariablePositions.size
    val numberOfVariables = numberOfPivotVariables + numberOfFreeVariables
    val solution = DoubleArray(numberOfVariables) { 0.0 }
    var pivotIndex = 0
    for (i in 0..< numberOfVariables) {
        if (i in pivotVariablePositions) {
            solution[i] = lastColumn[pivotIndex++]
        } 
    }
    val vectors = mutableListOf(solution)
    for (fv in freeVariablePositions) {
        val freeVariableVector = DoubleArray(numberOfVariables) { 0.0 }
        val freeVariableColumn = transposed[fv]
        var pivotRowCount = 0
        for (i in 0..< numberOfVariables) {
            if (i in pivotVariablePositions) {
                freeVariableVector[i] = -1.0 * freeVariableColumn[pivotRowCount++]
            }
        }
        vectors += freeVariableVector
    }
    return { input -> 
        val result = vectors.reduceIndexed { idx, acc, vector -> acc.addVector(input[idx - 1].toDouble(), vector) }
        if (result.all { it >= -0.0 } && result.all { it.isInt() }) {
            Pair(result.sum() + input.sum(), result)    
        } else {
            Pair(Double.MAX_VALUE, result)
        }
    }
}

private fun Double.isInt() =
    (this - this.roundToInt()).absoluteValue < 0.01

private fun <R> List<R>.indexOfFirstOrNull(function: (R) -> Boolean): Int? {
    val index = this.indexOfFirst { function(it) }
    return if (index != -1) index else null
}

private fun DoubleArray.addVector(factor: Double, other: DoubleArray): DoubleArray {
    return this.zip(other) { a, b -> a + factor * b }.toDoubleArray()
}

private fun MutableList<MutableList<Double>>.swapRows(a: Int, b: Int) {
    val temp = this[a]
    this[a] = this[b]
    this[b] = temp
}

private fun MutableList<MutableList<Double>>.divideRow(factor: Double, rowIndex: Int) {
    for (i in 0..<this[rowIndex].size) {
        this[rowIndex][i] = this[rowIndex][i] / factor
    }
}

private fun MutableList<MutableList<Double>>.addRow(factor: Double, targetRowIndex: Int, sourceRowIndex: Int) {
    for (i in 0..<this[targetRowIndex].size) {
        this[targetRowIndex][i] = this[targetRowIndex][i] + factor * this[sourceRowIndex][i]
    }
}

private fun MutableList<MutableList<Double>>.toReducedRowEchelonFormV2() {
    var pivotColumn = 0
    for (pivotIndex in 0..<this.size) {
        val pivotRow = pivotIndex
        var pivotElement = 0.0
        while (pivotElement == 0.0 && pivotColumn <= (this[0].size - 2)) {
            pivotElement = this[pivotRow][pivotColumn]
            if (pivotElement == 0.0) {
                //find first non-zero element under pivotElement
                for (i in pivotRow + 1..<this.size) {
                    val candidate = this[i][pivotColumn]
                    if (candidate != 0.0) {
                        this.swapRows(pivotRow, i)
                        pivotElement = candidate
                        break
                    }
                }
                if (pivotElement == 0.0) {
                    pivotColumn++
                }
            }
        }
        if (pivotElement == 0.0) continue
        if (pivotElement != 1.0) {
            this.divideRow(pivotElement, pivotRow)
        }
        for (otherRowIndex in 0..<this.size) {
            if (otherRowIndex == pivotRow) continue
            val otherRow = this[otherRowIndex]
            if (otherRow[pivotColumn] != 0.0) {
                val factor = -otherRow[pivotColumn]
                this.addRow(factor, otherRowIndex, pivotRow)
            }
        }
        pivotColumn++
    }
    this.removeIf { it.all { it == 0.0 } }

}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
//    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
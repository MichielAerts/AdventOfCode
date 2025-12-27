package advent2025.day10

import lib.runPuzzle
import lib.transpose
import org.paukov.combinatorics3.Generator
import java.io.File
import kotlin.streams.asSequence

const val day = 10
val file = File("src/main/resources/advent2025/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val machines = input.map { Machine.toMachine(it) }
        println(machines.sumOf { it.fewestButtonPressesToTurnOn() })
    }

    fun runPart2() {
        val machines = listOf(
//            "[.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}"
//            "[#..#.] (3,4) (2,3) (0,2) (1,2,4) (1,2,3) (1) {103,34,138,16,19}"
//            "[.#...#...] (0,3,4,5,6,7) (0,1,2,3,5,7,8) (0,2,3,5,6,7,8) (1,5,6,7) (0,1,3,4,5,6,7) (1,2,3,4,5,7) (2,4,6,7,8) {24,13,34,30,32,31,37,46,28}"
        "[..#.####] (0,4,5,6) (2,3) (3,7) (1,3,4,5,6,7) (0,1,2,3,5,7) (1,2,3,6) (1,2,3,5,7) {6,33,30,57,18,25,28,34}"
                ).map { LeveredMachine.toMachine(it) }
//        val machines = input.map { LeveredMachine.toMachine(it) }
        println(machines.sumOf { it.fewestButtonPressesToConfigureJoltage(true) })
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

private fun List<Int>.hasPivotVariable() = this.count { it == 1 } == 1 && this.count { it == 0 } == size - 1

data class LeveredMachine(val wiredButtons: List<List<Int>>, val joltageRequirements: List<Int>) {

    fun fewestButtonPressesToConfigureJoltage(log: Boolean = false): Int {
        println("calculating solution for $this")
        val matrix = (0..<joltageRequirements.size).map { counterIndex ->
            ((0..<wiredButtons.size)
                .map { if (counterIndex in wiredButtons[it]) 1 else 0 } + joltageRequirements[counterIndex]).toMutableList()
        }.toMutableList()
        
        if (log) println("initial matrix:")
        if (log) matrix.printLines()
//        val result = matrix.toReducedRowEchelonForm()
        matrix.toReducedRowEchelonFormV2(log)
        if (log) println("RREF:")
//        result.forEach { println(it) }
        if (log) matrix.printLines()
        
//        val presses = matrix.calculateSolution()
        val presses = matrix.calculateSolutionV2()
        val max = 10
        val pivotVariablePositions = matrix.mapNotNull { it.indexOfFirstOrNull { it == 1 } }
        val freeVariablePositions = (0..<(matrix[0].size - 1)).filter { it !in pivotVariablePositions }
        if (log) println("pivot variables: $pivotVariablePositions, free variables: $freeVariablePositions")

        val numberOfFreeVariables = freeVariablePositions.size
        var minimum = Integer.MAX_VALUE
        val freeVariableSpace = Generator.permutation((0..max).toList()).withRepetitions(numberOfFreeVariables).stream().toList()
        for (option in freeVariableSpace) {
//            println("trying $option")
            val (result, solution) = presses(option)
            if (result < minimum) {
                if (log) println("found something low: $result for ${solution.joinToString()} with input: $option")
                minimum = result
            }
        }
        println("found minimum: $minimum")
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

private fun MutableList<MutableList<Int>>.printLines() {
    this.forEach { println(it) }
}


private fun List<List<Int>>.calculateSolutionV2(): (List<Int>) -> Pair<Int, IntArray> {
    //assume RREF
    val transposed = this.transpose()
    val lastColumn = transposed.last()
//    val pivotVariablePositions = (0..<(this[0].size - 1)).filter { transposed[it].hasPivotVariable() }
    val pivotVariablePositions = this.mapNotNull { it.indexOfFirstOrNull { it == 1 } }
    val numberOfPivotVariables = pivotVariablePositions.size
    val freeVariablePositions = (0..<(this[0].size - 1)).filter { it !in pivotVariablePositions }
    
//    val freeVariablePositions = (0..<(this[0].size - 1)).filter { !transposed[it].hasPivotVariable() }
    val numberOfFreeVariables = freeVariablePositions.size
    val numberOfVariables = numberOfPivotVariables + numberOfFreeVariables
    val solution = IntArray(numberOfVariables) { 0 }
    var pivotIndex = 0
    for (i in 0..< numberOfVariables) {
        if (i in pivotVariablePositions) {
            solution[i] = lastColumn[pivotIndex++]
        } 
    }
    val vectors = mutableListOf(solution)
    for (fv in freeVariablePositions) {
        val freeVariableVector = IntArray(numberOfVariables) { 0 }
        val freeVariableColumn = transposed[fv]
        var pivotRowCount = 0
        for (i in 0..< numberOfVariables) {
            if (i in pivotVariablePositions) {
                freeVariableVector[i] = -1 * freeVariableColumn[pivotRowCount++]
            }
        }
        vectors += freeVariableVector
    }
    return { input -> 
        val result = vectors.reduceIndexed { idx, acc, vector -> acc.addVector(input[idx - 1], vector) }
        if (result.all { it >= 0 }) {
            Pair(result.sum() + input.sum(), result)    
        } else {
            Pair(Int.MAX_VALUE, solution)
        }
    }
}

private fun <R> List<R>.indexOfFirstOrNull(function: (R) -> Boolean): Int? {
    val index = this.indexOfFirst { function(it) }
    return if (index != -1) index else null
}

private fun List<List<Int>>.calculateSolution(): (List<Int>) -> Pair<Int, IntArray> = { input ->
    //assume RREF
    val transposed = this.transpose()
    val lastColumn = transposed.last()
    val pivotVariablePositions = (0..<(this[0].size - 1)).filter { transposed[it].hasPivotVariable() }
    val numberOfPivotVariables = pivotVariablePositions.size
    val freeVariablePositions = (0..<(this[0].size - 1)).filter { !transposed[it].hasPivotVariable() }
    val numberOfFreeVariables = freeVariablePositions.size
    val numberOfVariables = numberOfPivotVariables + numberOfFreeVariables
    var solution = IntArray(numberOfVariables) { 0 }
    var pivotIndex = 0
    var inputColumn = 0
    for (i in 0..< numberOfVariables) {
        if (i in pivotVariablePositions) {
            solution[i] = lastColumn[pivotIndex++]
        } else {
            solution[i] = input[inputColumn++]
        }
    }
    inputColumn = 0
    for (fv in freeVariablePositions) {
        val freeVariableVector = IntArray(numberOfVariables) { 0 }
        val freeVariableColumn = transposed[fv]
        var pivotRowCount = 0
        for (i in 0..< numberOfVariables) {
            if (i in pivotVariablePositions) {
                freeVariableVector[i] = -1 * freeVariableColumn[pivotRowCount++]
            }
        }
        solution = solution.addVector(input[inputColumn++], freeVariableVector)
    }
    if (solution.all { it >= 0 }) {
//        println("solution: ${solution.joinToString()}: ${solution.sum()}")
        Pair(solution.sum(), solution)
    } else Pair(Int.MAX_VALUE, solution)
}

private fun IntArray.addVector(factor: Int, other: IntArray): IntArray {
    return this.zip(other) { a, b -> a + factor * b }.toIntArray()
}

private fun MutableList<MutableList<Int>>.swapRows(a: Int, b: Int) {
    val temp = this[a]
    this[a] = this[b]
    this[b] = temp
}

private fun MutableList<MutableList<Int>>.divideRow(factor: Int, rowIndex: Int) {
    for (i in 0..<this[rowIndex].size) {
        this[rowIndex][i] = this[rowIndex][i] / factor
    }
}

private fun MutableList<MutableList<Int>>.addRow(factor: Int, targetRowIndex: Int, sourceRowIndex: Int) {
    for (i in 0..<this[targetRowIndex].size) {
        this[targetRowIndex][i] = this[targetRowIndex][i] + factor * this[sourceRowIndex][i]
    }
}

private fun MutableList<MutableList<Int>>.toReducedRowEchelonFormV2(log: Boolean = false) {
    var pivotColumn = 0
    for (pivotIndex in 0..<this.size) {
        val pivotRow = pivotIndex
        var pivotElement = 0
        while(pivotElement == 0 && pivotColumn <= (this[0].size - 2)) {
            pivotElement = this[pivotRow][pivotColumn]
            if (pivotElement == 0) {
                //find first non-zero element under pivotElement
                for (i in pivotRow + 1..<this.size) {
                    val candidate = this[i][pivotColumn]
                    if (candidate != 0) {
                        if (log) println("swapping row ${pivotRow + 1} ${i + 1}")
                        this.swapRows(pivotRow, i)
                        if (log) this.printLines()
                        pivotElement = candidate
                        break
                    }
                }
                if (pivotElement == 0) {
                    pivotColumn++
                }
            }
        }
        if (pivotElement == 0) continue
        if (pivotElement != 1) {
            if (log) println("Dividing row ${pivotRow + 1} by $pivotElement")
            this.divideRow(pivotElement, pivotRow)
            if (log) this.printLines()
        }
        for (otherRowIndex in 0..<this.size) {
            if (otherRowIndex == pivotRow) continue
            val otherRow = this[otherRowIndex]
            if (otherRow[pivotColumn] != 0) {
                val factor = -otherRow[pivotColumn]
                if (log) println("adding $factor * row ${pivotRow + 1} to ${otherRowIndex + 1}")
                this.addRow(factor, otherRowIndex, pivotRow)
                if (log) this.printLines()
            }
        }
        if (log) println("finished actions for pivot ${pivotRow + 1}")
        pivotColumn++
        if (log) this.printLines()
    }
    // now in row echelon form
    this.removeIf { it.all { it == 0 } }
    
//    for (pivotRowIndex in (matrix.size - 1) downTo 1) {
//        var pivotRow = matrix[pivotRowIndex]
//        println(pivotRow)
//        val idxFirstNonZeroValue = pivotRow.indexOfFirst { it != 0 }
//        if (idxFirstNonZeroValue == -1) continue
//        var firstNonZeroValue = pivotRow[idxFirstNonZeroValue]
//        if (firstNonZeroValue != 1) {
//            println("scaling pivot row $pivotRow ")
//            matrix[pivotRowIndex] = pivotRow.map { it / firstNonZeroValue }
//            pivotRow = matrix[pivotRowIndex]
//            firstNonZeroValue = pivotRow[idxFirstNonZeroValue]
//            println("scaled pivot row $pivotRow ")
//        }
//        for (otherRowIndex in 0..<pivotRowIndex) {
//            val otherRow = matrix[otherRowIndex]
//            val factor = if (otherRow[idxFirstNonZeroValue] != 0) otherRow[idxFirstNonZeroValue] / firstNonZeroValue else 0
//            println("adjusting row $otherRowIndex $otherRow with factor $factor")
//            matrix[otherRowIndex] = otherRow.mapIndexed { idx, current ->
//                current - factor * pivotRow[idx]
//            }
//            println(matrix)
//        }
//        matrix = matrix.sort().toMutableList()
//        println(matrix)
//    }
//    return matrix.filter { it.count { it != 0 } > 0 }
}


private fun List<List<Int>>.toReducedRowEchelonForm(): List<List<Int>> {
    var matrix = this.sort().toMutableList()
    println(matrix)
    for (pivotRowIndex in 0..<(matrix.size - 1)) {
        var pivotRow = matrix[pivotRowIndex]
        println(pivotRow)
        val idxFirstNonZeroValue = pivotRow.indexOfFirst { it != 0 }   
        if (idxFirstNonZeroValue == -1) continue
        var firstNonZeroValue = pivotRow[idxFirstNonZeroValue]
        if (firstNonZeroValue != 1) {
            println("scaling pivot row $pivotRow ")
            matrix[pivotRowIndex] = pivotRow.map { it / firstNonZeroValue }
            pivotRow = matrix[pivotRowIndex]
            firstNonZeroValue = pivotRow[idxFirstNonZeroValue]
            println("scaled pivot row $pivotRow ")
        }
        for (otherRowIndex in (pivotRowIndex + 1)..<matrix.size) {
            val otherRow = matrix[otherRowIndex]
            val factor = if (otherRow[idxFirstNonZeroValue] != 0) otherRow[idxFirstNonZeroValue] / firstNonZeroValue else 0
            println("adjusting row $otherRowIndex $otherRow with factor $factor")
            matrix[otherRowIndex] = otherRow.mapIndexed { idx, current ->
                current - factor * pivotRow[idx]
            }
            println(matrix)
        }
        matrix.forEach { println(it) }
        matrix = matrix.sort().toMutableList()
        println("sort:")
//        println(matrix)
        matrix.forEach { println(it) }
    }
    // now in row echelon form
    println("REF:")
    matrix.forEach { println(it) }
    for (pivotRowIndex in (matrix.size - 1) downTo 1) {
        var pivotRow = matrix[pivotRowIndex]
        println(pivotRow)
        val idxFirstNonZeroValue = pivotRow.indexOfFirst { it != 0 }
        if (idxFirstNonZeroValue == -1) continue
        var firstNonZeroValue = pivotRow[idxFirstNonZeroValue]
        if (firstNonZeroValue != 1) {
            println("scaling pivot row $pivotRow ")
            matrix[pivotRowIndex] = pivotRow.map { it / firstNonZeroValue }
            pivotRow = matrix[pivotRowIndex]
            firstNonZeroValue = pivotRow[idxFirstNonZeroValue]
            println("scaled pivot row $pivotRow ")
        }
        for (otherRowIndex in 0..<pivotRowIndex) {
            val otherRow = matrix[otherRowIndex]
            val factor = if (otherRow[idxFirstNonZeroValue] != 0) otherRow[idxFirstNonZeroValue] / firstNonZeroValue else 0
            println("adjusting row $otherRowIndex $otherRow with factor $factor")
            matrix[otherRowIndex] = otherRow.mapIndexed { idx, current ->
                current - factor * pivotRow[idx]
            }
            println(matrix)
        }
        matrix = matrix.sort().toMutableList()
        println(matrix)
    }
    return matrix.filter { it.count { it != 0 } > 0 }
}

private fun List<List<Int>>.sort() =
    sortedBy { if (it.indexOfFirst { it != 0 } == -1) 999 else it.indexOfFirst { it != 0 } }

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
//    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
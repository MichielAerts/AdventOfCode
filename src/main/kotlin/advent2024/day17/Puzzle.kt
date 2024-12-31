package advent2024.day17

import lib.runPuzzle
import lib.splitBy
import java.io.File
import kotlin.math.pow

const val day = 17
val file = File("src/main/resources/advent2024/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val (registers, program) = input.splitBy { it.isEmpty() }
        val computer = Computer.createComputer(registers, program[0])
        computer.run()
        println(computer.output.joinToString(","))
    }

    fun runPart2() {
        val program = listOf(2, 4, 1, 7, 7, 5, 4, 1, 1, 4, 5, 5, 0, 3, 3, 0)
     
        var currentInputs = mutableListOf<String>("")
        var newInputs = currentInputs
        val max = 7L
        for (o in 0..<program.size) { 
            currentInputs = newInputs.toMutableList()
            newInputs = mutableListOf<String>()
            for (input in currentInputs) {
                for (i in 0..max) {
                    val potentialInput = input + i.toString(2).padStart(3, '0')
                    val computer = Computer(a = potentialInput.toLong(2), program = program)
                    computer.run()
                    val output = computer.output
                    if ((o < (program.size - 1) && output == program.takeLast(output.size))
                        || (o == (program.size - 1) && output == program)) {
                        newInputs += potentialInput
                    }
                }
            }
        }
        println(newInputs.minOfOrNull { it.toLong(2) })
    }
}

data class Computer(
    var a: Long = 0,
    var b: Long = 0,
    var c: Long = 0,
    val program: List<Int>,
    var instructionPointer: Int = 0,
    val output: MutableList<Int> = mutableListOf()
) {

    fun run() {
        while (instructionPointer in 0..<program.size) {
            val instruction = program[instructionPointer]
            val operand = program[instructionPointer + 1]
            processInstruction(instruction, operand)
        }
    }

    fun processInstruction(instruction: Int, operand: Int) {
        when (instruction) {
            0 -> {
                // adv
                a = (a / 2.0.pow(comboOperand(operand).toInt())).toLong()
                instructionPointer += 2
            }

            1 -> {
                // bxl
                b = b xor literalOperand(operand).toLong()
                instructionPointer += 2
            }

            2 -> {
                //bst
                b = comboOperand(operand) % 8
                instructionPointer += 2
            }

            3 -> {
                //jnz
                if (a != 0L) {
                    instructionPointer = literalOperand(operand)
                } else {
                    instructionPointer += 2
                }
            }

            4 -> {
                //bxc
                b = b xor c
                instructionPointer += 2
            }

            5 -> {
                //out
                output += (comboOperand(operand) % 8).toInt()
                instructionPointer += 2
            }

            6 -> {
                //bvd
                b = (a / 2.0.pow(comboOperand(operand).toInt())).toLong()
                instructionPointer += 2
            }

            7 -> {
                //cvd
                c = (a / 2.0.pow(comboOperand(operand).toInt())).toLong()
                instructionPointer += 2
            }
        }
    }

    fun literalOperand(operand: Int) = operand

    fun comboOperand(operand: Int): Long = when (operand) {
        0, 1, 2, 3 -> operand.toLong()
        4 -> a
        5 -> b
        6 -> c
        7 -> throw IllegalStateException()
        else -> throw UnsupportedOperationException()
    }

    companion object {
        fun createComputer(registers: List<String>, programInput: String): Computer {
            val (a, b, c) = registers.map { it.split(": ")[1].toLong() }
            val program = programInput.split(": ")[1].split(",").map { it.toInt() }
            return Computer(a, b, c, program)
        }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
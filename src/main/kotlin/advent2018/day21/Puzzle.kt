package advent2018.day21

import lib.runPuzzle
import java.io.File

const val day = 21
val file = File("src/main/resources/advent2018/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val ipr = input[0].substringAfter("#ip ").toInt()
        val device = Device(ipr)
        device.registers[0] = 1.toLong()
        val instructions = input.drop(1)
        device.executeProgramWithShortcuts(instructions)
    }

    fun runPart2() {
    }
}

class Device(
    val ipRegister: Int,
    var instructionPointer: Int = 0,
    var counter: Int = 0,
    val registers: MutableMap<Int, Long> = (0..5).associateWith { 0L }.toMutableMap(),
    val operations: Map<String, (Int, Int) -> Long> = mapOf(
        "addr" to { a, b -> registers.getValue(a) + registers.getValue(b) },
        "addi" to { a, b -> registers.getValue(a) + b },
        "mulr" to { a, b -> registers.getValue(a) * registers.getValue(b) },
        "muli" to { a, b -> registers.getValue(a) * b },
        "banr" to { a, b -> registers.getValue(a) and registers.getValue(b) },
        "bani" to { a, b -> registers.getValue(a) and b.toLong() },
        "borr" to { a, b -> registers.getValue(a) or registers.getValue(b) },
        "bori" to { a, b -> registers.getValue(a) or b.toLong() },
        "setr" to { a, b -> registers.getValue(a) },
        "seti" to { a, b -> a.toLong() },
        "gtir" to { a, b -> if (a > registers.getValue(b)) 1 else 0 },
        "gtri" to { a, b -> if (registers.getValue(a) > b) 1 else 0 },
        "gtrr" to { a, b -> if (registers.getValue(a) > registers.getValue(b)) 1 else 0 },
        "eqir" to { a, b -> if (a.toLong() == registers.getValue(b)) 1 else 0 },
        "eqri" to { a, b -> if (registers.getValue(a) == b.toLong()) 1 else 0 },
        "eqrr" to { a, b -> if (registers.getValue(a) == registers.getValue(b)) 1 else 0 },
    ),
) {
    fun executeProgram(instructions: List<String>) {
        while (instructionPointer in 0..<instructions.size && counter++ < 10000) {
            registers[ipRegister] = instructionPointer.toLong()
            val instruction = instructions[instructionPointer]
            println("executing $instruction, counter: $counter ip: $instructionPointer registers: $registers")
            val (op, a, b, c) = instruction.split(" ")
            executeInstruction(op, a.toInt(), b.toInt(), c.toInt())
            instructionPointer = registers[ipRegister]!!.toInt() + 1
        }
    }

    fun executeProgramWithShortcuts(instructions: List<String>) {
        val fives = mutableMapOf<Long, MutableList<Int>>()
        while (instructionPointer in 0..<instructions.size && counter++ < 1_000_000) {
            if (instructionPointer == 18) {
                registers[2] = registers[3]!!/256
                instructionPointer = 26
            } 
            registers[ipRegister] = instructionPointer.toLong()
            val instruction = instructions[instructionPointer]
            if (instructionPointer == 28) {
                println("executing $instruction, counter: $counter ip: $instructionPointer registers: $registers")
                fives.merge(registers[5]!!, mutableListOf(counter)) { a, b -> (a + b).toMutableList() }
            }
            val (op, a, b, c) = instruction.split(" ")
            executeInstruction(op, a.toInt(), b.toInt(), c.toInt())
            instructionPointer = registers[ipRegister]!!.toInt() + 1
        }
        println(fives.size)
        println(fives.maxBy { it.value[0] }.key)
    }

    fun executeInstruction(operation: String, a: Int, b: Int, c: Int) {
        registers[c] = operations.getValue(operation)(a, b)
    }

}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
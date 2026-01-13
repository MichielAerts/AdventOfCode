package advent2018.day19

import lib.runPuzzle
import java.io.File

const val day = 19
val file = File("src/main/resources/advent2018/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val ipr = input[0].substringAfter("#ip ").toInt()
        val device = Device(ipr)
        val instructions = input.drop(1)
        device.registers[0] = 1
        device.executeProgram(instructions)
//        device.executeProgramWithShortcuts(instructions)
        println(device.registers[0]!!)
    }

    fun runPart2() {
        val factors = listOf(1, 2, 5275663, 10551326)
        println(factors.sum())
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

    fun executeProgramWithShortcuts(instructions: List<String>) {
        while (instructionPointer in 0..<instructions.size && counter++ < 2000) {
            if (instructionPointer == 3) {
                val r4 = registers[4]!!
                val r2 = registers[2]!!
                val r0 = registers[0]!!
                if (r4 % r2 == 0L && r4 / r2 in 1..r4) {
                    registers[0] = r0 + r2
                }
                registers[5] = r4 + 1
                instructionPointer = 12
            }
            if (instructionPointer == 12) {
                val r4 = registers[4]!!
                registers[2] = r4 + 1
                instructionPointer = 16
            }
            registers[ipRegister] = instructionPointer.toLong()
            val instruction = instructions[instructionPointer]
            println("executing $instruction, ip: $instructionPointer registers: $registers")
            val (op, a, b, c) = instruction.split(" ")
            executeInstruction(op, a.toInt(), b.toInt(), c.toInt())
            instructionPointer = registers[ipRegister]!!.toInt() + 1
//            println("after instruction, ip: $instructionPointer registers: $registers")
        }
    }
    
    fun executeProgram(instructions: List<String>) {
        while (instructionPointer in 0..<instructions.size && counter++ < 5000) {
            if (instructionPointer == 3) {
                registers[4] = 20
            }
            registers[ipRegister] = instructionPointer.toLong()
            val instruction = instructions[instructionPointer]
            println("executing $instruction, ip: $instructionPointer registers: $registers")
            val (op, a, b, c) = instruction.split(" ")
            executeInstruction(op, a.toInt(), b.toInt(), c.toInt())
            instructionPointer = registers[ipRegister]!!.toInt() + 1
//            println("after instruction, ip: $instructionPointer registers: $registers")
        }
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
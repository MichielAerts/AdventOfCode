package advent2018.day16

import lib.runPuzzle
import lib.splitBy
import java.io.File

const val day = 16
val file = File("src/main/resources/advent2018/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val samples = input.splitBy { it.isEmpty() }.filter { it.isNotEmpty() }.dropLast(1)
        println(samples.count { testSample(it).count { it.value } >= 3 })
    }

    fun runPart2() {
        val instructions = input.splitBy { it.isEmpty() }.last().map { it.split(" ").map { it.toInt() } }
        val program = Program()
        instructions.forEach { 
            val (op, a, b, c) = it
            program.executeInstruction(op, a, b, c)
        }
        println(program.registers)
    }

    private fun testSample(sample: List<String>): Map<String, Boolean> {
        val before = sample[0].substringAfter("Before: [").substringBefore("]")
            .split(", ").map { it.toLong() }
        val instruction = sample[1].split(" ").map { it.toInt() }
        val after = sample[2].substringAfter("After:  [").substringBefore("]")
            .split(", ").map { it.toLong() }

        val program = Program()
        val results = program.operations.entries.associate { operation ->
            program.setRegisters(before)
            val (_, a, b, c) = instruction
            program.executeInstruction(operation.key, a, b, c)
            val result = program.registers.values.toList() == after
            operation.key to result
        }
        val options = results.filter { it.value }.keys
        if (options.count { it !in program.operationToOpcode.keys } == 1 && instruction[0] !in program.operationToOpcode.values) {
            println("opCode ${instruction[0]} = ${options.filterNot { it in program.operationToOpcode.keys }}")
        }
        return results
    }
}

class Program(
    val registers: MutableMap<Int, Long> = (0..3).associateWith { 0L }.toMutableMap(),
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
    val operationToOpcode: Map<String, Int> = mapOf(
        "bori" to 0,
        "borr" to 1,
        "seti" to 2,
        "mulr" to 3,
        "setr" to 4,
        "addr" to 5,
        "gtir" to 6,
        "eqir" to 7,
        "gtri" to 8,
        "bani" to 9,
        "muli" to 10,
        "gtrr" to 11,
        "banr" to 12,
        "eqri" to 13,
        "addi" to 14,
        "eqrr" to 15
    ),
    val opcodeToOperation: Map<Int, String> = operationToOpcode.entries.associate { it.value to it.key }
) {
    
    fun setRegisters(setting: List<Long>) {
        setting.forEachIndexed { idx, set -> registers[idx] = set }
    }
    
    fun executeInstruction(operation: String, a: Int, b: Int, c: Int) {
        registers[c] = operations.getValue(operation)(a, b)
    }

    fun executeInstruction(operation: Int, a: Int, b: Int, c: Int) {
        registers[c] = operations.getValue(opcodeToOperation.getValue(operation))(a, b)
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
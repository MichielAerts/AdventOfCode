package advent2017.day23

import lib.runPuzzle
import java.io.File

const val day = 23
val file = File("src/main/resources/advent2017/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val instructions = input
        val program = Program(instructions)
        program.execute()
        println(program.counter)
    }

    fun runPart2() {
        val instructions = input
        val program = Program(instructions)
        program.registers['a'] = 1
        
        program.executeShortCut()
        println(program.registers['h'])
    }
}

class Program(
    val instructions: List<String>,
    val registers: MutableMap<Char, Long> = instructions.flatMap { it.findRegisters() }.toSet().associateWith { 0L }.toMutableMap(),
    var index: Int = 0,
    var counter: Int = 0,
    var maxInstructions: Int = 10000
) {
    fun execute() {
        while (index in 0..<instructions.size) {
            val instruction = instructions[index]
            val jump = registers.execute(instruction)
            index += jump
        }
    }

    fun executeShortCut() {
        var instructionsExecuted = 0
        while (index in 0..<instructions.size && instructionsExecuted++ < maxInstructions) { 
            if (index == 11) {
                val b = registers['b']!!
                val shouldSetF = (2..<b).any { d -> b % d == 0L && b / d in 2..<b }
                if (shouldSetF) {
                    registers['f'] = 0
                }
                registers['g'] = 0
                registers['e'] = b
                registers['d'] = b
                index = 24                
            }
            val instruction = instructions[index]
            val jump = registers.execute(instruction)
            index += jump
        }
    }
    
    private fun MutableMap<Char, Long>.execute(instruction: String): Int {
        val fields = instruction.split(" ")
        val operation = fields[0]
        val op1 = fields[1][0]
        val op2 = if (fields[2].length == 1 && fields[2][0] in 'a'..'z')
            getValue(fields[2][0]) else fields[2].toLong()
        when(operation) {
            "set" -> { this[op1] = op2 }
            "sub" -> { this[op1] = this.getValue(op1) - op2 }
            "mul" -> { 
                this[op1] = this.getValue(op1) * op2 
                counter++
            }
            "jnz" -> { 
                val firstValue = if (op1 in 'a'..'z')
                    getValue(op1) else op1.digitToInt().toLong()
                if (firstValue != 0L) return op2.toInt() 
            }
        }
        return 1
    }
}

private fun String.findRegisters(): List<Char> =
    this.split(" ").filter { it.length == 1 && it[0] in 'a'..'z' }
        .map { it[0] }.toList()


fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
//    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
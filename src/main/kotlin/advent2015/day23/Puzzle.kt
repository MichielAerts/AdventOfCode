package advent2015.day23

import advent2015.day23.Type.*
import lib.runPuzzle
import java.io.File

const val day = 23
val file = File("src/main/resources/advent2015/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val instructions = input.map { Instruction.toInstruction(it) }
        val computer = Computer(program = instructions)
        computer.runProgram()
        println(computer.registers["b"])
    }

    fun runPart2() {
        println(input)
    }
}


/*
hlf r sets register r to half its current value, then continues with the next instruction.
tpl r sets register r to triple its current value, then continues with the next instruction.
inc r increments register r, adding 1 to it, then continues with the next instruction.
jmp offset is a jump; it continues with the instruction offset away relative to itself.
jie r, offset is like jmp, but only jumps if register r is even ("jump if even").
jio r, offset is like jmp, but only jumps if register r is 1 ("jump if one", not odd).
 */

data class Computer(
    val registers: MutableMap<String, Int> = mutableMapOf("a" to 1, "b" to 0),
    var offset: Int = 0,
    val program: List<Instruction>,
) {
    
    fun runProgram() {
        while(offset in 0..program.size - 1) {
            val (type, register, jump) = program[offset]
            when (type) {
                HLF -> {
                    registers[register!!] = registers[register]!! / 2
                    offset += 1
                }
                TPL -> {
                    registers[register!!] = registers[register]!! * 3
                    offset += 1
                }
                INC -> {
                    registers[register!!] = registers[register]!! + 1
                    offset += 1
                }
                JMP -> {
                    offset += jump!!
                }
                JIE -> {
                    if (registers[register!!]!! % 2 == 0) offset += jump!!
                    else offset += 1
                }
                JIO -> {
                    if (registers[register!!]!! == 1) offset += jump!!
                    else offset += 1
                }
            }
            println(this)
        }
    }
}

data class Instruction(val type: Type, val register: String? = null, val jump: Int? = null) {
    companion object {
        fun toInstruction(input: String): Instruction {
            val type = Type.valueOf(input.substring(0, 3).uppercase())
            val values = input.substring(4)
            
            return when(type) {
                HLF -> Instruction(HLF, register = values)
                TPL -> Instruction(TPL, register = values)
                INC -> Instruction(INC, register = values)
                JMP -> Instruction(JMP, jump = values.toInt())
                JIE -> Instruction(JIE, register = values.split(", ")[0], jump = values.split(", ")[1].toInt())
                JIO -> Instruction(JIO, register = values.split(", ")[0], jump = values.split(", ")[1].toInt())
            }
        }
    }
}

enum class Type { HLF, TPL, INC, JMP, JIE, JIO }

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
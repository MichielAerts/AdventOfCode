package advent2017.day8

import lib.allGroups
import lib.component6
import lib.runPuzzle
import java.io.File

const val day = 8
val file = File("src/main/resources/advent2017/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val registers = input.flatMap { Instruction.getRegisters(it) }.toSet()
        val cpu = registers.associateWith { 0 }.toMutableMap()

        val instructions = input.map { Instruction.toInstruction(it) }
        var highestValue = 0
        for (instruction in instructions) {
            val (register, operation, condition) = instruction
            if (condition(cpu)) {
                val newValue = operation(cpu.getValue(register))
                cpu[register] = newValue
                if (newValue > highestValue) highestValue = newValue
            }
//            println(cpu)
        }
        println(cpu.values.max())
        println(highestValue)
    }

    fun runPart2() {
        println(input)
    }
}

data class Instruction(
    val register: String,
    val operation: (Int) -> Int,
    val condition: (Map<String, Int>) -> Boolean
) {
    companion object {
        val regex = Regex("(\\w+) (\\w+) (-?\\d+) if (\\w+) ([>=<!]+) (-?\\d+)")
        
        fun getRegisters(input: String): List<String> {
            require(regex.matches(input))
            val (reg, op, amount, regC, testC, amountC) = regex.allGroups(input)
            return listOf(reg, regC)
        }
        
        fun toInstruction(input: String): Instruction {
            //iy dec -132 if xzn < 1578
            require(regex.matches(input))
            val (reg, op, amount, regC, testC, amountC) = regex.allGroups(input)
            val operation = { currentValue: Int -> 
                when (op) {
                    "inc" -> currentValue + amount.toInt()
                    "dec" -> currentValue - amount.toInt()
                    else -> throw UnsupportedOperationException()
                }
            }
            val condition = { registers: Map<String, Int> -> 
                val registerValue = registers.getValue(regC)
                when(testC) {
                    ">" -> registerValue > amountC.toInt()
                    "<" -> registerValue < amountC.toInt()
                    ">=" -> registerValue >= amountC.toInt()
                    "<=" -> registerValue <= amountC.toInt()
                    "==" -> registerValue == amountC.toInt()
                    "!=" -> registerValue != amountC.toInt()
                    else -> throw UnsupportedOperationException()
                }
            }
            
            return Instruction(
                register = reg, 
                operation = operation, 
                condition = condition
            )
        }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
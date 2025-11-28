package advent2017.day18

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import lib.runPuzzle
import java.io.File

const val day = 18
val file = File("src/main/resources/advent2017/day${day}/input")

class Puzzle(private val input: List<String>) {
    
    fun runPart1() {
        val instructions = input
        val program = Program(instructions)
        program.execute()
    }

    fun runPart2() = runBlocking {
        val instructions = input

        val channelAtoB = Channel<Long>(50000) //buffered channel
        val channelBtoA = Channel<Long>(50000) //buffered channel
        
        val a = SendingProgram(0, instructions, channelAtoB, channelBtoA)
        val b = SendingProgram(1, instructions, channelBtoA, channelAtoB)

        val jobA = launch { a.execute() }
        val jobB = launch { b.execute() }
        
        while(notLocked(a, b) && (!a.isDone || !b.isDone)) {
            delay(5000)    
        }
        jobA.cancel()
        jobB.cancel()
        println("done!")
    }

    private fun notLocked(a: SendingProgram, b: SendingProgram): Boolean = !a.isWaiting && !b.isWaiting
}

class SendingProgram(
    val id: Int,
    val instructions: List<String>,
    val sendingChannel: Channel<Long>,
    val receivingChannel: Channel<Long>,
    val registers: MutableMap<Char, Long> = instructions.flatMap { it.findRegisters() }.toSet().associateWith { 0L }.toMutableMap(),
    var index: Int = 0,
    var isWaiting: Boolean = false,
    var isDone: Boolean = false
) {
    
    suspend fun execute() {
        registers['p'] = id.toLong()
        while (index in 0..<instructions.size) {
            val instruction = instructions[index]
            val jump = registers.execute(instruction)
            index += jump
        }
        isDone = true
        println("$id is done!")
    }

    suspend fun MutableMap<Char, Long>.execute(instruction: String): Int {
        val fields = instruction.split(" ")
        val operation = fields[0]
        val op1 = fields[1][0]
        val op2 = when(operation) {
            "set", "add", "mul", "mod", "jgz" -> getVal(fields[2])
            "snd", "rcv" -> null
            else -> throw UnsupportedOperationException()
        }
        when(operation) {
            "set" -> { this[op1] = op2!! }
            "add" -> { this[op1] = this.getValue(op1) + op2!! }
            "mul" -> { this[op1] = this.getValue(op1) * op2!! }
            "mod" -> { this[op1] = this.getValue(op1) % op2!! }
            "snd" -> { sendingChannel.send(getVal(op1)) }
            "rcv" -> {
                isWaiting = true
                this[op1] = receivingChannel.receive()
                isWaiting = false
            }
            "jgz" -> { if (getVal(op1) > 0) return op2!!.toInt() }
        }
        return 1
    }

    private fun MutableMap<Char, Long>.getVal(operand: String): Long =
        if (operand.length == 1 && operand[0] in 'a'..'z') getValue(operand[0]) else operand.toLong()

    private fun MutableMap<Char, Long>.getVal(operand: Char): Long =
        if (operand in 'a'..'z') getValue(operand) else "$operand".toLong()
}

class Program(
    val instructions: List<String>,
    val registers: MutableMap<Char, Long> = instructions.flatMap { it.findRegisters() }.toSet().associateWith { 0L }.toMutableMap(),
    var index: Int = 0,
    var mostRecentlyPlayedSound: Long? = null
) {
    fun execute() {
        while (index in 0..<instructions.size) {
            val instruction = instructions[index]
            val jump = registers.execute(instruction)
            index += jump
        }
    }

    private fun MutableMap<Char, Long>.execute(instruction: String): Int {
        val fields = instruction.split(" ")
        val operation = fields[0]
        val op1 = fields[1][0]
        val op2 = when(operation) {
            "set", "add", "mul", "mod", "jgz" ->
                if (fields[2].length == 1 && fields[2][0] in 'a'..'z') getValue(fields[2][0]) else fields[2].toLong()
            "snd", "rcv" -> null
            else -> throw UnsupportedOperationException()
        }
        when(operation) {
            "set" -> { this[op1] = op2!! }
            "add" -> { this[op1] = this.getValue(op1) + op2!! }
            "mul" -> { this[op1] = this.getValue(op1) * op2!! }
            "mod" -> { this[op1] = this.getValue(op1) % op2!! }
            "snd" -> { mostRecentlyPlayedSound = this.getValue(op1) }
            "rcv" -> { if (this.getValue(op1) != 0L) {
                println("rcv: $mostRecentlyPlayedSound")
                return Integer.MAX_VALUE
            } }
            "jgz" -> { if (this.getValue(op1) > 0) return op2!!.toInt() }
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
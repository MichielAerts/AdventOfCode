package advent2017.day25

import lib.Direction
import lib.Direction.LEFT
import lib.Direction.RIGHT
import lib.runPuzzle
import java.io.File
import java.util.*

const val day = 25
val file = File("src/main/resources/advent2017/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val steps = 12425180
        val machine = TuringMachine()
        repeat(steps) { machine.executeStep() }
        println(machine.checksum())
    }

    fun runPart2() {
        println(input)
    }
}

data class TuringMachine(
    val size: Int = 10000,
    var cursor: Int = size,
    var currentState: State = State.A,
    val tape: BitSet = BitSet(2 * size),
    var executions: Int = 0
) {
    fun executeStep() {
        val currentBit = tape[cursor]
        tape[cursor] = currentState.writer(currentBit)
        cursor += if (currentState.mover(currentBit) == RIGHT) 1 else -1
        currentState = currentState.state(currentBit)
    }
    
    fun checksum() =
        tape.cardinality()
}

enum class State(
    val writer: (Boolean) -> Boolean,
    val mover: (Boolean) -> Direction,
    val state: (Boolean) -> State
) {
    A(
        writer = { input -> if (input) false else true },
        mover = { input -> if (input) RIGHT else RIGHT },
        state = { input -> if (input) F else B }
    ),
    B(
        writer = { input -> if (input) true else false },
        mover = { input -> if (input) LEFT else LEFT },
        state = { input -> if (input) C else B }
    ),
    C(
        writer = { input -> if (input) false else true },
        mover = { input -> if (input) RIGHT else LEFT },
        state = { input -> if (input) C else D }
    ),
    D(
        writer = { input -> if (input) true else true },
        mover = { input -> if (input) RIGHT else LEFT },
        state = { input -> if (input) A else E }
    ),
    E(
        writer = { input -> if (input) false else true },
        mover = { input -> if (input) LEFT else LEFT },
        state = { input -> if (input) D else F }
    ),
    F(
        writer = { input -> if (input) false else true },
        mover = { input -> if (input) LEFT else RIGHT },
        state = { input -> if (input) E else A }
    )
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
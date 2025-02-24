package advent2015.day7

import advent2015.day7.Wire.Companion.toWire
import lib.runPuzzle
import java.io.File

const val day = 7
val file = File("src/main/resources/advent2015/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val system = System.createSystem(input)
        while(!system.complete()) {
            system.gates.filter { it.op.ready(it) }
                .forEach { it.output.signal = it.op.output(it) }
            println(system)
        }
        println(system.wires["a"]!!.signal)
        //3176
    }

    fun runPart2() {
        val system = System.createSystem(input)
        system.wires["b"]!!.signal = 3176
        while(!system.complete()) {
            system.gates.filter { it.op.ready(it) }
                .forEach { it.output.signal = it.op.output(it) }
            println(system)
        }
        println(system.wires["a"]!!.signal)
    }
}

data class System(val wires: Map<String, Wire>, val gates: List<Gate>) {
    
    fun complete() = wires.all { it.value.signal != null }
    
    companion object {
        val w = "(?<i>\\w+) -> (?<o>\\w+)".toRegex()
        val and = "(?<l>\\w+) AND (?<r>\\w+) -> (?<o>\\w+)".toRegex()
        val or = "(?<l>\\w+) OR (?<r>\\w+) -> (?<o>\\w+)".toRegex()
        val ls = "(?<l>\\w+) LSHIFT (?<r>\\w+) -> (?<o>\\w+)".toRegex()
        val rs = "(?<l>\\w+) RSHIFT (?<r>\\w+) -> (?<o>\\w+)".toRegex()
        val not = "NOT (?<l>\\w+) -> (?<o>\\w+)".toRegex()
        fun createSystem(input: List<String>): System {
            val wires = mutableMapOf<String, Wire>()
            val gates = mutableListOf<Gate>()
            input.forEach { 
                println(it)
                when {
                    w.matches(it) -> {
                        val (_, win, wo) = w.find(it)?.groupValues!!
                        gates += Gate(Operator.OUT, left = wires.getOrPut(win) { toWire(win) }, output = wires.getOrPut(wo) { toWire(wo) })
                    }
                    and.matches(it) -> {
                        val (_, wl, wr, wo) = and.find(it)?.groupValues!!
                        gates += Gate(Operator.AND, left = wires.getOrPut(wl) { toWire(wl) }, right = wires.getOrPut(wr) { toWire(wr) }, output = wires.getOrPut(wo) { toWire(wo) })
                    }
                    or.matches(it) -> {
                        val (_, wl, wr, wo) = or.find(it)?.groupValues!!
                        gates += Gate(Operator.OR, left = wires.getOrPut(wl) { toWire(wl) }, right = wires.getOrPut(wr) { toWire(wr) }, output = wires.getOrPut(wo) { toWire(wo) })
                    }
                    ls.matches(it) -> {
                        val (_, wl, sh, wo) = ls.find(it)?.groupValues!!
                        gates += Gate(Operator.LSHIFT, left = wires.getOrPut(wl) { toWire(wl) }, output = wires.getOrPut(wo) { toWire(wo) }, shift = wires.getOrPut(sh) { toWire(sh) })
                    }
                    rs.matches(it) -> {
                        val (_, wl, sh, wo) = rs.find(it)?.groupValues!!
                        gates += Gate(Operator.RSHIFT, left = wires.getOrPut(wl) { toWire(wl) }, output = wires.getOrPut(wo) { toWire(wo) }, shift = wires.getOrPut(sh) { toWire(sh) })
                    }
                    not.matches(it) -> {
                        val (_, wl, wo) = not.find(it)?.groupValues!!
                        gates += Gate(Operator.NOT, left = wires.getOrPut(wl) { toWire(wl) }, output = wires.getOrPut(wo) { toWire(wo) })
                    }
                    else -> throw UnsupportedOperationException()
                }
            }
            return System(wires, gates)
        }
    }
}
data class Wire(val id: String, var signal: Int? = null) {
    companion object {
        fun toWire(input: String) =
            Wire(input, input.toIntOrNull())
    }
}
data class Gate(val op: Operator, val left: Wire, val right: Wire? = null, val output: Wire, val shift: Wire? = null)
enum class Operator(val ready: (Gate) -> Boolean, val output: (Gate) -> Int) {
    OUT(        
        ready = { it.left.signal != null && it.output.signal == null },
        output = { it.left.signal!! }),
    AND(
        ready = { it.left.signal != null && it.right?.signal != null && it.output.signal == null },
        output = { it.left.signal!! and it.right!!.signal!! }),
    LSHIFT(
        ready = { it.left.signal != null && it.output.signal == null },
        output = { it.left.signal!! shl it.shift!!.signal!! }),
    NOT(
        ready = { it.left.signal != null && it.output.signal == null },
        output = { it.left.signal!!.inv() + 65536 }),
    OR(
        ready = { it.left.signal != null && it.right?.signal != null && it.output.signal == null },
        output = { it.left.signal!! or it.right!!.signal!! }),
    RSHIFT(
        ready = { it.left.signal != null && it.output.signal == null },
        output = { it.left.signal!! shr it.shift!!.signal!! })
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
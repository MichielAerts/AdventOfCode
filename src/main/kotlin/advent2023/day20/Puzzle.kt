package advent2023.day20

import lib.lcm
import lib.runPuzzle
import java.io.File

const val day = 20
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val modules = input.map { Module.createModule(it) }.associateBy { it.name }.toMutableMap()
        val broadcaster = modules.getValue("broadcaster")
        val button = ButtonModule("button", "broadcaster")
        modules["button"] = button

        modules.values.flatMap { it.destinationsRaw.split(", ") }.filterNot { it in modules.keys }.forEach { 
            modules[it] = UntypedModule(it, "")
        }
        modules.values.filter { it.destinationsRaw.isNotEmpty() }.forEach {
            it.destinations = it.destinationsRaw.split(", ").map { modules.getValue(it) }
        }
        val conjunctionInputMap = modules.values.filterIsInstance<ConjunctionModule>().associateWith { c -> modules.values.filter { c in it.destinations } }
        conjunctionInputMap.forEach {
            it.key.lastPulseType = it.value.associateWith { PulseType.LOW }.toMutableMap()
        }
        val clInputModules = listOf("js", "qs", "dt", "ts")
            .map { modules.getValue(it) as ConjunctionModule }
            .associateWithTo(mutableMapOf()) { mutableSetOf<Int>() }
        val cl = modules.getValue("cl") as ConjunctionModule
        val rx = modules.getValue("rx")

        val buttonPushes = 1_000_000
        var lowCount = 0
        var highCount = 0
        for (push in 1..buttonPushes) {
            val pulses = ArrayDeque<Pulse>()
            pulses.addFirst(Pulse(PulseType.LOW, button, broadcaster))
            while (pulses.isNotEmpty()) {
                val pulse = pulses.removeFirst()
                if (pulse.type == PulseType.LOW) lowCount++ else highCount++
//                if (pulse.destination in clInputModules) println("reaching ${pulse.destination} at $push")
//                if (pulse.type == PulseType.HIGH && pulse.destination in clInputModules) println("reaching ${pulse.destination} at $push")
//                if (pulse.destination == modules.getValue("rx")) println("pulse $pulse at $push to rx")
                if (pulse.destination == rx) {
                    val state = cl.lastPulseType
                    state.filter { it.value == PulseType.HIGH }.forEach { 
                        val set = clInputModules.getValue(it.key as ConjunctionModule)
                        set += push
                        clInputModules[it.key as ConjunctionModule] = set
                    }
                }
                val newPulses = pulse.destination.processPulse(pulse)
                pulses.addAll(newPulses)
            }
        }
        println(lowCount * highCount)
        clInputModules.forEach { 
            println(it)
        }
        //250628960065793
        println(
            lcm(longArrayOf(4019, 3943, 3947, 4007))
        )
    }
    
    fun runPart2() {
        println(input)
        /*
          &cl -> rx
          cl memory should be all high for input 
&js -> cl
&qs -> cl
&dt -> cl
&ts -> cl
                 
         */
        
    }
}

class FlipFlopModule(name: String, destinationsRaw: String, var status: Status): Module(name, destinationsRaw) {
    override fun processPulse(input: Pulse): List<Pulse> = 
        if (input.type == PulseType.HIGH) {
            emptyList()
        } else {
            if (status == Status.OFF) {
                status = Status.ON
                destinations.map { Pulse(PulseType.HIGH, this, it) }
            } else {
                status = Status.OFF
                destinations.map { Pulse(PulseType.LOW, this, it) }
            }
        }
}

class ConjunctionModule(name: String, destinationsRaw: String, var lastPulseType: MutableMap<Module, PulseType> = mutableMapOf()): Module(name, destinationsRaw) {
    override fun processPulse(input: Pulse): List<Pulse> {
        lastPulseType[input.source] = input.type
        val type = if (lastPulseType.values.all { it == PulseType.HIGH }) PulseType.LOW else PulseType.HIGH
        return destinations.map { Pulse(type, this, it) }
    }

}
class BroadCastModule(name: String, destinationsRaw: String): Module(name, destinationsRaw) {
    override fun processPulse(input: Pulse): List<Pulse> = destinations.map { Pulse(input.type, this, it) }
}

class ButtonModule(name: String, destinationsRaw: String): Module(name, destinationsRaw) {
    override fun processPulse(input: Pulse): List<Pulse> = throw IllegalArgumentException("Shouldn't receive pulses")
}

class UntypedModule(name: String, destinationsRaw: String): Module(name, destinationsRaw) {
    override fun processPulse(input: Pulse): List<Pulse> = emptyList()
}

data class Pulse(val type: PulseType, val source: Module, val destination: Module)

sealed class Module(val name: String, val destinationsRaw: String, var destinations: List<Module> = listOf()) {

    override fun toString(): String = name

    abstract fun processPulse(input: Pulse): List<Pulse>
    
    companion object {
        fun createModule(input: String): Module {
            val (mod, dest) = input.split(" -> ")
            return when {
                input.startsWith("broadcaster") -> BroadCastModule("broadcaster", dest)
                input.startsWith("%") -> FlipFlopModule(mod.substringAfter("%"), dest, Status.OFF)
                input.startsWith("&") -> ConjunctionModule(mod.substringAfter("&"), dest)
                else -> throw IllegalArgumentException()
            }
        }
    }
}

enum class PulseType { LOW, HIGH }
enum class Status { ON, OFF }
    
fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

package advent2024.day24

import advent2024.day24.Gate.Companion.ops
import lib.mapToPair
import lib.runPuzzle
import lib.splitBy
import java.io.File

const val day = 24
val file = File("src/main/resources/advent2024/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val system = System.createSystem(input)
        println(system.solveZ())
    }

    fun runPart2() {

        val deps = getWiresUsedForEachZ()
        val newlyUsedWires = mutableMapOf<String, Set<String>>()
        val usedWires = mutableSetOf<String>()
        for ((z, dep) in deps.toSortedMap()) {
            val newlyUsed = dep - usedWires
            println("$z $newlyUsed")
            usedWires += dep
            newlyUsedWires[z] = newlyUsed
        }
        val suspect = 39
//        val suspects = listOf("z09", "z10", "z13", "z20", "z21", "z39", "z40",  "z45")

//        val suspects = listOf("z13", "z20", "z21", "z39", "z40",  "z45")
        val potentialSwitches = (suspect.. 45)
            .map { if (it < 10) "z0$it" else "z$it" }
            .flatMap { newlyUsedWires[it]!! }.filter { !it.startsWith("x") && !it.startsWith("y") }.toSet()

        val switchesToMake = listOf<Pair<String, String>>(
            //from z09:
//            Pair("dsk", "gwh"),
//            Pair("ptc", "mnm"),
//            Pair("ptc", "gwh"), // also good 
            Pair("z09", "gwh"), // also good
        
            //from z13
            Pair("wbw", "wgb"), //only one correct
            //from z21
//            Pair("z21", "tvj"),
//            Pair("z21", "z22"),
            Pair("z21", "rcb"), //looks best
            
         //from z39 (first one with ptc", "gwh, second with z09", "gwh) 
//            Pair("z39", "nhk"), //perfect but the extra sum? perfect!
//            Pair("z39", "z40"), //170, 170
            Pair("z39", "jct"), //perfect but the extra sum? perfect!
//            Pair("z39", "ksf"), //173, 173
        )
  
        val zXXwires = newlyUsedWires["z$suspect"]!!
        val switches = mutableListOf<Pair<String, String>>()
        for (zXXwire in zXXwires) {
            for (switch in potentialSwitches) {
//                println("trying switch $zXXwire & $switch")
                if (runTests(switchesToMake + Pair(zXXwire, switch), suspect + 1)) {
                    println("potential switch $zXXwire & $switch")
                    switches += Pair(zXXwire, switch)
                }
            }
        }

        for (pair in switches) {
            println("Pair(\"${pair.first}\", \"${pair.second}\"),")
            println("score: ${runFullTests(switchesToMake + pair, log = true)}")
        }
        println(switchesToMake.flatMap { listOf(it.first, it.second)}.sorted().joinToString(","))
    }

    private fun runTests(switchesToMake: List<Pair<String, String>>, max: Int = 45, log: Boolean = false): Boolean {
        var totalScore = 0
        val possibleScore = if (max == 45) 3 * max + 1 else 3 * max
        for (i in 1..max) {
            var testScore = 0
            val max = "1".repeat(i).toLong(2)
            val tests = mutableListOf(
                Pair(0L, 0L),
                Pair(0L, max),
                Pair(max, 0L),
            )
            if (i == 45) tests += Pair(29869243016611L, 26851697606349L)
            for (test in tests) {
                val system = System.createSystem(input, switchesToMake)
                system.setTo("x", test.first)
                system.setTo("y", test.second)
                val xInput = system.getValue("x")
                val yInput = system.getValue("y")
                val z = system.solveZ()
                if (z == (test.first + test.second)) testScore++
                if (log) println("$i: x: $xInput, y: $yInput, z: $z, diff = ${z - (xInput + yInput)}")
            }
//            println("score for $i: $testScore")
            totalScore += testScore
        }
        if (log) println("total score: $totalScore")
        if (possibleScore == totalScore) return true
        return false
    }

    private fun runFullTests(switchesToMake: List<Pair<String, String>>, log: Boolean = false) {
        var totalScore = 0
        for (i in 1..45) {
            var testScore = 0
            val max = "1".repeat(i).toLong(2)
            val tests = mutableListOf(
                Pair(0L, 0L),
                Pair(0L, max),
                Pair(max, 0L),
                Pair(max, max),
            )
            if (i == 45) tests += Pair(29869243016611L, 26851697606349L)
            for (test in tests) {
                val system = System.createSystem(input, switchesToMake)
                system.setTo("x", test.first)
                system.setTo("y", test.second)
                val xInput = system.getValue("x")
                val yInput = system.getValue("y")
                val z = system.solveZ()
                if (z == (test.first + test.second)) testScore++
//                if (log) println("$i: x: $xInput, y: $yInput, z: $z, diff = ${z - (xInput + yInput)}")
            }
//            println("score for $i: $testScore")
            totalScore += testScore
        }
        if (log) println("total score: $totalScore")
    }

    private fun getWiresUsedForEachZ(): MutableMap<String, Set<String>> {
        val (inputWires, wiresFromGates) = input.splitBy { it.isEmpty() }
            .mapToPair(
                transformLeft = { it.map { Wire.createWire(it) }.toSet() },
                transformRight = { it.flatMap { Wire.createWiresFromGate(it) }.toSet() }
            )
        val allWires = (inputWires + wiresFromGates.filter { it.name !in inputWires.map { it.name } }).toSet()
        val outputWires = allWires.filter { it.name.startsWith("z") }
        val otherWires = allWires - inputWires - outputWires

        val (_, gates) = input.splitBy { it.isEmpty() }
            .mapToPair(
                transformLeft = { it },
                transformRight = { it.map { Gate.createGate(it, allWires.associateBy { it.name }) } }
            )
        val gatesByOutput = gates.associateBy { it.output.name }
        val otherWiresByName = otherWires.map { it.name }
        val eqs = mutableMapOf<String, String>()
        val deps = mutableMapOf<String, Set<String>>()
        for (gateWithZOutput in gates.filter { it.output.name.startsWith("z") }) {
            val dep = mutableSetOf<String>()
            var eq = with(gateWithZOutput) {
                dep += leftInput.name
                dep += rightInput.name
                dep += output.name
                "(${leftInput.name} $operation ${rightInput.name})"
            }
            while (otherWiresByName.any { it in eq }) {
                val otherWire = otherWiresByName.first { it in eq }
                eq = eq.replace(otherWire, with(gatesByOutput[otherWire]!!) {
                    dep += leftInput.name
                    dep += rightInput.name
                    dep += output.name
                    "(${leftInput.name} $operation ${rightInput.name})"
                })
            }
            eqs += gateWithZOutput.output.name to eq
            deps += gateWithZOutput.output.name to dep
        }
        eqs.toSortedMap().forEach {
            //            println(it) 
    //            println("${it.key}: ${"[xy]\\d{2}".toRegex().findAll(it.value).map { it.value }.toSet().sorted() }")

        }
        deps.toSortedMap().forEach {
            //            println(it)
        }
        return deps
    }
}

data class System(
    val inputWires: List<Wire>,
    val outputWires: List<Wire>,
    val allWires: Map<String, Wire>,
    val gates: List<Gate>
) {

    fun switchOutput(l: String, r: String) {
        val left = allWires[l]!!
        val right = allWires[r]!!
        val leftGates = gates.filter { it.output == left }
        val rightGates = gates.filter { it.output == right }
        leftGates.forEach { it.output = right }
        rightGates.forEach { it.output = left }
//        gates.forEach { println(it) }    
    }
    
    
    fun solveZ(): Long {
        val maxSteps = allWires.size
        var steps = 0
        while (!zWiresAllHaveValues() && steps < maxSteps) {
            gates.filter { it.output.value == null }
                .filter { it.leftInput.value != null && it.rightInput.value != null }
                .forEach {
                    with(it) {
                        output.value = ops[operation]!!(leftInput.value!!, rightInput.value!!)
                    }
                }
            steps++
        }
        if (steps == maxSteps) return -1
        return outputWires.associateBy { it.name }
            .toSortedMap()
            .map { it.value.value }
            .reversed()
            .joinToString("")
            .toLong(2)
    }
    
    fun getValue(prefix: String): Long =
        allWires
            .filter { it.key.startsWith(prefix) }
            .toSortedMap()
            .map { it.value.value }
            .reversed()
            .joinToString("")
            .toLong(2)

    private fun zWiresAllHaveValues(): Boolean =
        outputWires.all { it.value != null }

    fun setXInputToOnesUntil(i: Int) {
        inputWires.filter { it.name.startsWith("x") }
            .forEach {
                val idx = it.name.substring(1).toInt()
                it.value = if (idx > i) 0 else 1
            }
        inputWires.filter { it.name.startsWith("y") }.forEach { it.value = 0 }
    }

    fun setTo(prefix: String, input: Long) {
        val binaryReversed = input.toString(2).padStart(45, '0').reversed()
        inputWires.filter { it.name.startsWith(prefix) }
            .forEach {
                val idx = it.name.substring(1).toInt()
                it.value = binaryReversed[idx].digitToInt()
            }
    }

    companion object {
        fun createSystem(input: List<String>, switches: List<Pair<String, String>> = emptyList()): System {
            val (initializedWires, wiresFromGates) = input.splitBy { it.isEmpty() }
                .mapToPair(
                    transformLeft = { it.map { Wire.createWire(it )}},
                    transformRight = { it.flatMap { Wire.createWiresFromGate(it)}}
                )
            val allWires = (initializedWires +
                    wiresFromGates.filter { it.name !in initializedWires.map { it.name } })
            val outputWires = allWires.filter { it.name.startsWith("z") }
            val (_, gates) = input.splitBy { it.isEmpty() }
                .mapToPair(
                    transformLeft = { it },
                    transformRight = { it.map { Gate.createGate(it, allWires.associateBy { it.name }) } }
                )
            val system = System(initializedWires, outputWires, allWires.associateBy { it.name }, gates)
            switches.forEach { system.switchOutput(it.first, it.second) }
            return system
        }
    }
}
val gateRegex = "(\\w+) (\\w+) (\\w+) -> (\\w+)".toRegex()

data class Wire(val name: String, var value: Int? = null) {
    companion object {
        fun createWire(input: String): Wire {
            //x00: 1
            val (name, value) = input.split(": ")
            return Wire(name, value.toInt())
        }

        fun createWiresFromGate(input: String): List<Wire> {
            //x00 AND y00 -> z00
            val (_, w1, op, w2, w3) = gateRegex.matchEntire(input)?.groupValues!!
            return listOf(Wire(w1), Wire(w2), Wire(w3))
        }
    }
}
data class Gate(
    val leftInput: Wire,
    val rightInput: Wire,
    var output: Wire,
    val operation: String
) {
    companion object {
        val AND: (Int, Int) -> Int = { a, b -> if (a == 1 && b == 1) 1 else 0 }
        val OR: (Int, Int) -> Int = { a, b -> if (a == 1 || b == 1) 1 else 0 }
        val XOR: (Int, Int) -> Int = { a, b -> if (a != b) 1 else 0 }
        val ops = mapOf("AND" to AND, "OR" to OR, "XOR" to XOR)        
        fun createGate(input: String, allWires: Map<String, Wire>): Gate {
            //x00 AND y00 -> z00
            val (_, w1, op, w2, w3) = gateRegex.matchEntire(input)?.groupValues!!

            return Gate(
                leftInput = allWires[w1]!!,
                rightInput = allWires[w2]!!,
                output = allWires[w3]!!,
                operation = op
            )
        }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
//    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
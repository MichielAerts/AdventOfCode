package advent2025.day11

import lib.runPuzzle
import java.io.File

const val day = 11
val file = File("src/main/resources/advent2025/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val devices = createDevices().topologicalSort().associateBy { it.name }
        val numberOfValidPaths = devices.findNumberOfPaths("you", "out")
        println(numberOfValidPaths)
    }

    private fun Map<String, Device>.topologicalSort(): List<Device> {
        val sortedList = mutableListOf<Device>()
        val devices = this.values.toMutableList()
        while (devices.isNotEmpty()) {
            val outgoingEdges = devices.flatMap { it.outputs }
            val start = devices.first { it !in outgoingEdges }
            sortedList += start
            devices.remove(start)
        }
        return sortedList
    }

    private fun Map<String, Device>.findNumberOfPaths(
        start: String,
        target: String
    ): Long {
        //assumes topological sort
        val map = this.keys.associateWith { 0L }.toMutableMap()
        map[start] = 1L
        for ((name, amount) in map) {
            if (name == target) return amount
            for (next in this.getValue(name).outputs) {
                map.merge(next.name, amount) { a, b -> a + b }
            }
        }
        throw IllegalStateException("$target not encountered")
    }

    fun runPart2() {
        val devices = createDevices().topologicalSort().associateBy { it.name }
        println(
            devices.findNumberOfPaths("svr", "fft") *
                    devices.findNumberOfPaths("fft", "dac") *
                    devices.findNumberOfPaths("dac", "out")
                    + devices.findNumberOfPaths("svr", "dac") *
                    devices.findNumberOfPaths("dac", "fft") *
                    devices.findNumberOfPaths("fft", "out")
        )
    }

    private fun createDevices(): Map<String, Device> {
        val devices = (input.map { Device.toDevice(it) } + Device("out")).associateBy { it.name }
        input.forEach { line ->
            val (name, output) = line.split(": ")
            val device = devices.getValue(name)
            device.outputs = output.split(" ").map { devices.getValue(it) }
        }
        return devices
    }
}

data class Device(val name: String, var outputs: List<Device> = emptyList()) {

    override fun toString(): String =
        "$name: output: ${outputs.map { it.name }}"
    
    companion object {
        fun toDevice(input: String): Device {
            return Device(input.split(": ").first())
        }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
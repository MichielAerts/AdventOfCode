package advent2025.day11

import lib.runPuzzle
import java.io.File

const val day = 11
val file = File("src/main/resources/advent2025/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val devices = createDevices()
        val numberOfValidPaths = devices.findNumberOfPaths("you", "out")
        println(numberOfValidPaths)
    }

    private fun Map<String, Long>.getAnyIntermediateEndOfPath(target: String): Map.Entry<String, Long>? =
        this.entries.sortedBy { it.value }.firstOrNull { it.key != target && it.value > 0 }
    
    private fun Map<String, Device>.findNumberOfPaths(
        start: String,
        target: String
    ): Long {
        val map = this.keys.associateWith { 0L }.toMutableMap()
        map[start] = 1L
        while (map.getAnyIntermediateEndOfPath(target) != null) {
            val (currentEnd, amount) = map.getAnyIntermediateEndOfPath(target)!!
            for (next in this.getValue(currentEnd).outputs) {
                map.merge(next.name, amount) { a, b -> a + b }
            }
            map[currentEnd] = 0
        }
        return map.getValue(target)
    }

    fun runPart2() {
        val devices = createDevices()
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
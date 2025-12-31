package advent2017.day24

import lib.runPuzzle
import java.io.File

const val day = 24
val file = File("src/main/resources/advent2017/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val ports = input.map { Port.toPort(it) }.toSet()
        val starts = ports.filter { it.left == 0 }.map { listOf(it) }
        val queue = ArrayDeque(starts)
        var currentMaxStrength = starts.maxOf { it.sumOf { it.strength() } }
        var currentMaxLength = 1
        var currentMaxStrengthOfLongestBridge = currentMaxStrength
        while (queue.isNotEmpty()) {
            val currentBridge = queue.removeFirst()
            val currentEnd = currentBridge.last().currentEnd()
            val potentialNextPorts = (ports - currentBridge.toSet()).filter { it.left == currentEnd || it.right == currentEnd }
            for (potentialNextPort in potentialNextPorts) {
                val nextPort = if (potentialNextPort.right == currentEnd) {
                    potentialNextPort.copy(orientation = Orientation.REVERSED)
                } else {
                    potentialNextPort
                }
                val nextBridge = currentBridge + nextPort
                val strength = nextBridge.sumOf { it.strength() }
                val length = nextBridge.size
                if (strength > currentMaxStrength) {
                    currentMaxStrength = strength
                }
                if (length >= currentMaxLength) {
                    if (length == currentMaxLength && strength > currentMaxStrengthOfLongestBridge) {
                        currentMaxStrengthOfLongestBridge = strength
                    }
                    if (length > currentMaxLength) {
                        currentMaxLength = length
                        currentMaxStrengthOfLongestBridge = strength
                    }
                }
                queue.add(nextBridge)
            }
        }
        println("max strength: $currentMaxStrength")
        println("max strength of longest bridge: $currentMaxStrengthOfLongestBridge")
    }

    fun runPart2() {
        println(input)
    }
}

enum class Orientation { NORMAL, REVERSED }
data class Port (val left: Int, val right: Int, var orientation: Orientation = Orientation.NORMAL) {
    
    fun strength() = left + right
    
    fun currentEnd() = if (orientation == Orientation.NORMAL) right else left
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Port

        if (left != other.left) return false
        if (right != other.right) return false

        return true
    }

    override fun hashCode(): Int {
        var result = left
        result = 31 * result + right
        return result
    }

    companion object {
        fun toPort(input: String): Port {
            val (left, right) = input.split("/").map { it.toInt() }.sorted()
            return Port(left, right)
        }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
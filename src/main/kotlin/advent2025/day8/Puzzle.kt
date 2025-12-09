package advent2025.day8

import lib.product
import lib.runPuzzle
import lib.subListTillEnd
import java.io.File
import kotlin.math.pow
import kotlin.math.sqrt

const val day = 8
val file = File("src/main/resources/advent2025/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val boxes = input.map { JunctionBox.createJunctionBox(it) }
        val pairsByDistance = boxes
            .flatMapIndexed { idx, first ->
                boxes.subListTillEnd(idx + 1).map { second -> Pair(first, second) }
            }
            .associateByTo(sortedMapOf()) { it.first.distanceTo(it.second) }
        
        val network = Network()
        boxes.forEach { network.add(it) }        
        val connect = 1000
        pairsByDistance.entries.take(connect).forEach { (distance, pair) ->
            network.connectPair(pair.first, pair.second)
        }
        println(network.connectedSets.sortedByDescending { it.size }.take(3).map { it.size }.product())
    }

    fun runPart2() {
        val boxes = input.map { JunctionBox.createJunctionBox(it) }
        val pairsByDistance = boxes
            .flatMapIndexed { idx, first ->
                boxes.subListTillEnd(idx + 1).map { second -> Pair(first, second) }
            }
            .associateByTo(sortedMapOf()) { it.first.distanceTo(it.second) }
        val pairs = pairsByDistance.values.toList()

        val network = Network()
        boxes.forEach { network.add(it) }
        
        var lastConnectedPair = pairs[0]
        var connectedPairs = 0
        while(!network.isConnected()) {
            val pair = pairs[connectedPairs++]
            network.connectPair(pair.first, pair.second)
            lastConnectedPair = pair
        }
        println("${lastConnectedPair.first.x.toLong() * lastConnectedPair.second.x}")
    }
}

data class Network(val connectedSets: MutableSet<MutableSet<JunctionBox>> = mutableSetOf()) {
    fun add(junctionBox: JunctionBox) {
        require(!connectedSets.flatten().contains(junctionBox))
        connectedSets.add(mutableSetOf(junctionBox))
    }

    fun connectPair(first: JunctionBox, second: JunctionBox) {
        if (connectedSets.any { first in it && second in it }) return
        
        val firstSet = connectedSets.first { first in it }
        connectedSets.remove(firstSet)
        
        val secondSet = connectedSets.first { second in it }
        connectedSets.remove(secondSet)
        
        val newSet = firstSet + secondSet
        connectedSets.add(newSet.toMutableSet())
    }
    
    fun isConnected() = connectedSets.size == 1
}

data class JunctionBox(val x: Int, val y: Int, val z: Int) {
    companion object {
        fun createJunctionBox(input: String): JunctionBox {
            val (x, y, z) = input.split(",").map { it.toInt() }
            return JunctionBox(x, y, z)
        }
    }
    
    fun distanceTo(other: JunctionBox) = 
        sqrt((this.x - other.x).toDouble().pow(2.0) + 
                (this.y - other.y).toDouble().pow(2.0) +
                (this.z - other.z).toDouble().pow(2.0)
        )
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
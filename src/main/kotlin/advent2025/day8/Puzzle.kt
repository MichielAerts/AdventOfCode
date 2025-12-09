package advent2025.day8

import lib.product
import lib.runPuzzle
import lib.subListTillEnd
import org.jgrapht.alg.connectivity.ConnectivityInspector
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph
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
        
        val graph = SimpleGraph<JunctionBox, DefaultEdge>(DefaultEdge::class.java)
        boxes.forEach { graph.addVertex(it) }
        val connect = 1000
        pairsByDistance.entries.take(connect).forEach { (distance, pair) ->
            graph.addEdge(pair.first, pair.second)
        }

        val scAlg = ConnectivityInspector(graph)
        val stronglyConnectedSubgraphs = scAlg.connectedSets()

        println(stronglyConnectedSubgraphs.sortedByDescending { it.size }.take(3).map{ it.size }.product())
    }

    fun runPart2() {
        val boxes = input.map { JunctionBox.createJunctionBox(it) }
        val pairsByDistance = boxes
            .flatMapIndexed { idx, first ->
                boxes.subListTillEnd(idx + 1).map { second -> Pair(first, second) }
            }
            .associateByTo(sortedMapOf()) { it.first.distanceTo(it.second) }
        val pairs = pairsByDistance.values.toList()

        val graph = SimpleGraph<JunctionBox, DefaultEdge>(DefaultEdge::class.java)
        boxes.forEach { graph.addVertex(it) }
        
        var scAlg = ConnectivityInspector(graph)
        var connectedPairs = 0
        var lastConnectedPair = pairs.first()
        while(!scAlg.isConnected) {
            val pair = pairs[connectedPairs++]
            graph.addEdge(pair.first, pair.second)
            scAlg = ConnectivityInspector(graph)
            lastConnectedPair = pair
        }
        println("${lastConnectedPair.first.x.toLong() * lastConnectedPair.second.x}")
    }
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
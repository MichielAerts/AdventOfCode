package advent2024.day23

import lib.runPuzzle
import org.jgrapht.graph.SimpleGraph
import java.io.File

const val day = 23
val file = File("src/main/resources/advent2024/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val connections = input.map { it.split("-") }.map { Connection(it[0], it[1]) }
        val graph = SimpleGraph<String, Connection>(Connection::class.java)
        connections.forEach { 
            with(it) {
                graph.addVertex(source)
                graph.addVertex(target)
                graph.addEdge(source, target, it)
            }
        }
        val sets = mutableSetOf<List<String>>()
        val queue = ArrayDeque<List<String>>(graph.vertexSet().map { listOf(it) })
        while (queue.isNotEmpty()) {
            val currentList = queue.removeFirst()
            val currentComputer = currentList.last()
            val connectedComputers = graph.outgoingEdgesOf(currentComputer)
                .map { it.other(currentComputer) }
            for (connectedComputer in connectedComputers) {
                val newList = currentList + connectedComputer
                if (newList.size == 4) {
                    sets.add(newList)
                } else {
                    queue.add(newList)
                }
            }
        }
//        sets.forEach { println(it) }
        val setsOfThree = 
            sets.filter { it.toSet().size == 3 } // set of three computers
                .filter { it.first() == it.last() } // circle
                .map { it.toSet().sorted() }.toSet()
        println(setsOfThree.count { it.any { it.startsWith("t") } })
    }

    fun runPart2() {
        val connections = input.map { it.split("-") }.map { Connection(it[0], it[1]) }
        val graph = SimpleGraph<String, Connection>(Connection::class.java)
        connections.forEach { println(it) }
        connections.forEach {
            with(it) {
                graph.addVertex(source)
                graph.addVertex(target)
                graph.addEdge(source, target, it)
            }
        }
        val maxClique = graph.findMaxClique()
        println(maxClique.sorted().joinToString(","))
    }
}

fun SimpleGraph<String, Connection>.connectedComputersOf(source: String): List<String>
    = outgoingEdgesOf(source).map { it.other(source) }
        
fun SimpleGraph<String, Connection>.findMaxClique(): Set<String> {
    //
    var currentMaxClique = emptySet<String>()
    for (computer in vertexSet()) {
        val currentClique = mutableSetOf<String>(computer)
        val queue = ArrayDeque<String>(listOf(computer))
        while (queue.isNotEmpty()) {
            val nextComputer = queue.removeFirst()
            val connectedComputers = connectedComputersOf(nextComputer)
            for (connectedComputer in connectedComputers) {
                if (connectedComputer in currentClique) continue
                val connectedToConnectedComputer = connectedComputersOf(connectedComputer)
                if (currentClique.all { it in connectedToConnectedComputer }) {
                    currentClique += connectedComputer
                    queue.addAll(connectedToConnectedComputer)
                }
            }
        }
        if (currentClique.size > currentMaxClique.size) currentMaxClique = currentClique
    }
    // for every vertex, caclulate max clique. if clique is
    return currentMaxClique
}
data class Connection(val source: String, val target: String) {
    fun other(first: String) = when(first) {
        source -> target
        target -> source
        else -> throw IllegalArgumentException()
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
//    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
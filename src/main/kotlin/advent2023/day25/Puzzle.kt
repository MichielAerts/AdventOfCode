package advent2023.day25

import advent2023.utils.mapToPair
import advent2023.utils.product
import advent2023.utils.runPuzzle
import org.jgrapht.alg.connectivity.ConnectivityInspector
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph
import java.io.File


const val day = 25
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val graph = SimpleGraph<String, DefaultEdge>(DefaultEdge::class.java)
        val vertices = mutableListOf<String>()
        val edges = mutableSetOf<Pair<String, String>>()
        for (line in input) {
            val (source, targets) = line.split(": ").mapToPair<String, String, List<String>>(
                transformRight = { it.split(" ")}
            )
            vertices.addAll(targets + source)
            edges.addAll(targets.map { Pair(source, it) })
        }
        vertices.toSet().forEach { graph.addVertex(it) }
        edges.forEach { graph.addEdge(it.first, it.second) }
        
        val connected = 4
        for (i in 1..connected) {
            val dijkstraShortestPath = DijkstraShortestPath(graph)
            val currentShortestPath = dijkstraShortestPath.getPath(vertices[2], vertices[100])
            println(currentShortestPath)
            currentShortestPath?.let { graph.removeAllEdges(currentShortestPath.edgeList) }
        }

        val scAlg = ConnectivityInspector(graph)
        val stronglyConnectedSubgraphs = scAlg.connectedSets()

        println(stronglyConnectedSubgraphs)
        println(stronglyConnectedSubgraphs.map { it.size }.product())
    }
    
    fun runPart2() {
        println(input)      
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

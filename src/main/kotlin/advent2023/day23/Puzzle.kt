package advent2023.day23

import lib.*
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph
import org.jgrapht.graph.SimpleDirectedWeightedGraph
import java.io.File
import java.util.*

const val day = 23
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val map = input.to2DGridOfPointsWithValues()
        
        val start = map[0][1]
        val target = map[map.size - 1][map[0].size - 2]
        
        val pq = PriorityQueue<TravelledPath>( compareByDescending { it.travelledPath.size } )
        var maxLengthFound = 0
        pq.add(TravelledPath(start, setOf()))
        while (pq.isNotEmpty()) {
            val (currentPoint, travelledPath) = pq.remove()
            if (currentPoint == target) {
                val length = travelledPath.size
                println("new path found with length $length: $travelledPath")
                if (length > maxLengthFound) maxLengthFound = length
                continue
            }
            val neighbours = map.getNeighboursAndDirection(currentPoint)
                .filter { it.value.value == '.' ||
                        (it.key == Direction.DOWN && it.value.value == 'v') ||
                        (it.key == Direction.RIGHT && it.value.value == '>') ||
                        (it.key == Direction.UP && it.value.value == '^') ||
                        (it.key == Direction.LEFT && it.value.value == '<')
                }
                .filter { it.value !in travelledPath }
                .map { it.value }
            for (neighbour in neighbours) {
                pq.add(TravelledPath(neighbour, travelledPath + neighbour))
            }
        }
        println(maxLengthFound)
    }
    
    fun runPart2() {
        val map = input.to2DGridOfPointsWithValues()
        val graph = map.toGraph()
        val start = map[0][1]
        val target = map[map.size - 1][map[0].size - 2]
        val pq = PriorityQueue<NewTravelledPath>( compareByDescending { it.travelledPath.sumOf { it.steps } } )
        var maxLengthFound = 0
        pq.add(NewTravelledPath(start, setOf()))
        while (pq.isNotEmpty()) {
            val (currentPoint, travelledPath) = pq.remove()
            if (currentPoint == target) {
                val length = travelledPath.sumOf { it.steps }
                if (length > maxLengthFound) {
                    println("new path found with length $length: $travelledPath")
                    maxLengthFound = length
                }
                continue
            }
            val edges = graph.outgoingEdgesOf(currentPoint)
                .filter { it.target !in travelledPath.flatMap { listOf(it.source, it.target) } }
            for (edge in edges) {
                pq.add(NewTravelledPath(edge.target, travelledPath + edge))
            }
        }
        println(maxLengthFound)
    }
}

private fun List<List<Point>>.toGraph(): SimpleDirectedGraph<Point, PathPart> {
    val graph = SimpleDirectedWeightedGraph<Point, PathPart>(PathPart::class.java)
    graph.addVertex(this[0][1])
    graph.addVertex(this[size - 1][this[0].size - 2])
    val junctions = this.flatten().filter { it.value != '#' }.filter {
        this.getDirectNeighbours(it).neighbours.count { it.value != '#' } > 2
    }
    junctions.forEach { 
        graph.addVertex(it)
    }
    val vertexSet = graph.vertexSet()
    for (junction in vertexSet) {
        val neighbours = this.getDirectNeighbours(junction).neighbours.filter { it.value != '#' }
        for (neighbour in neighbours) {
            var currentPoint = neighbour
            val currentPath = mutableSetOf<Point>(junction, currentPoint)
            while (currentPoint !in vertexSet) {
                currentPoint = this.getDirectNeighbours(currentPoint).neighbours
                    .filter { it.value != '#' }.first { it !in currentPath }
                currentPath += currentPoint
            }
            graph.addEdge(junction, currentPoint, 
                PathPart(junction, currentPoint, currentPath.size - 1))
        }
    }
    return graph
}

data class PathPart(val source: Point, val target: Point, val steps: Int) : DefaultEdge() 

data class TravelledPath(val currentPoint: Point, val travelledPath: Set<Point>)
data class NewTravelledPath(val currentPoint: Point, val travelledPath: Set<PathPart>)

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
//    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

package advent2024.day18

import lib.*
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph
import java.io.File

const val day = 18
val file = File("src/main/resources/advent2024/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        findShortestPath(1024)
    }

    fun runPart2() {
        for (i in 1024..3450) {
            findShortestPath(i)
        }
    }
    
    private fun findShortestPath(numberOfFallenBytes: Int) {
        val size = 70
        val grid = initEmptyGrid(endX = size, endY = size)
        val fallenBytes = input.map { Byte.createByte(it) }
            .take(numberOfFallenBytes)
        val corruptedPoints = fallenBytes 
            .mapNotNull { grid.getPoint(it.x, it.y) }
            .toSet()
        grid.changePoints(corruptedPoints, '#')
        val graph = grid.toGraph()

        val start = graph.vertexSet().find { it.x == 0 && it.y == 0 }
        val end = graph.vertexSet().find { it.x == size && it.y == size }

        val shortestPath =
            DijkstraShortestPath(graph).getPath(start, end) ?: 
            throw IllegalStateException("Couldn't find a path for ${fallenBytes.last()}")
        println("shortest path: ${shortestPath.weight} steps, route: ${shortestPath.vertexList}")
    }

}

private fun List<List<Point>>.toGraph(): SimpleGraph<Point, DefaultEdge> {
    val graph = SimpleGraph<Point, DefaultEdge>(DefaultEdge::class.java)
    flatten().filter { it.value != '#' }.forEach { graph.addVertex(it) }
    graph.vertexSet().forEach { point ->
        getDirectNeighbours(point).neighbours
            .filter { it.value != '#' }
            .forEach { graph.addEdge(point, it) }
    }
    return graph
}

data class Byte(val x: Int, val y: Int) {
    companion object {
        val regex = "(?<x>\\d+),(?<y>\\d+)".toRegex()
        fun createByte(input: String): Byte =
            Byte(regex.findGroupAsInt(input, "x"), regex.findGroupAsInt(input, "y"))
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
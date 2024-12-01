package advent2023.day17

import lib.*
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.SimpleDirectedWeightedGraph
import java.io.File
import kotlin.math.min
import kotlin.math.roundToInt

const val day = 17
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val map = input.to2DGridOfPointsWithValues()
        val graph = map.toGraph()
        val start = graph.vertexSet().find { it.start }
        val end = graph.vertexSet().find { it.end }
        val shortestPath = DijkstraShortestPath(graph).getPath(start, end) ?: throw IllegalStateException("just couldn't")
        println("shortest path: ${shortestPath.weight} steps, route: ${shortestPath.vertexList}")
    }

    fun runPart2() {
        val map = input.to2DGridOfPointsWithValues()
        println(
            min(
                findShortestPath(map, initialDirection = Direction.RIGHT),
                findShortestPath(map, initialDirection = Direction.DOWN)
            )
        )
    }

    private fun findShortestPath(map: List<List<Point>>, initialDirection: Direction): Int {
        val graph = map.toUltraGraph(initialDirection)
        val start = graph.vertexSet().find { it.start }
        val end = graph.vertexSet().find { it.end }
        val shortestPath =
            DijkstraShortestPath(graph).getPath(start, end) ?: throw IllegalStateException("just couldn't")
        println("shortest path $initialDirection: ${shortestPath.weight} steps")
        shortestPath.vertexList.forEach { println(it) }
        return shortestPath.weight.roundToInt()
    }
}

private fun List<List<Point>>.toUltraGraph(initialDirection: Direction): SimpleDirectedWeightedGraph<CityBlock, DefaultWeightedEdge> {
    // create graph of vertices, every vertex is a point also including a history
    // edges to vertices looking at the history, also taking into account constraints:
    // min 4 to max 10 blocks in one direction, no turning back
    val graph = SimpleDirectedWeightedGraph<CityBlock, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)
    //create vertices
    val steps = Direction.entries.toTypedArray().flatMap { dir -> (1..10).map { Pair(dir, it) } }
    flatten().forEach { p ->
        if (p.x == this[0].size - 1 && p.y == this.size - 1) {
            graph.addVertex(CityBlock(p, p.value.digitToInt(), Pair(initialDirection, 0), end = true))
        } else {
            if (p.x == 0 && p.y == 0) graph.addVertex(
                CityBlock(p, p.value.digitToInt(), Pair(initialDirection, 0), start = true)
            )
            for (step in steps) {
                val (dir, times) = step
                this.getPointAfterMove(p, dir.opposite(), times)?.let {
                    graph.addVertex(CityBlock(p, p.value.digitToInt(), step))
                }
            }
        }
    }
    val vertexMap = graph.vertexSet().associateBy { Pair(it.point, it.stepsBefore) }
    for (vertex in graph.vertexSet()) {
        val (dir, times) = vertex.stepsBefore
        val possibleDirections = Direction.entries.toMutableSet().apply {
            this -= dir.opposite()
            if (times == 10) this -= dir // after 10 steps, don't continue
            if (times < 4) {
                this -= dir.turnRight()
                this -= dir.turnLeft()
            }
        }
        for (possibleDirection in possibleDirections) {
            this.getPointAfterMove(vertex.point, possibleDirection)?.let {
                val newSteps = when {
                    it.x == this[0].size - 1 && it.y == this.size - 1 -> Pair(initialDirection, 0)
                    possibleDirection == dir -> Pair(dir, times + 1)
                    else -> Pair(possibleDirection, 1)
                }
                val newPoint = vertexMap.getValue(Pair(it, newSteps))
                if (it.x == this[0].size - 1 && it.y == this.size - 1 && (possibleDirection != dir || times < 3)) return@let
                val edge = graph.addEdge(vertex, newPoint)
                graph.setEdgeWeight(edge, newPoint.cost.toDouble())
            }
        }
    }
    return graph
}

private fun List<List<Point>>.toGraph(): SimpleDirectedWeightedGraph<CityBlock, DefaultWeightedEdge> {
    // create graph of vertices, every vertex is a point also including a history
    // edges to vertices looking at the history, also taking into account constraints:
    // max three blocks in one direction, no turning back
    val graph = SimpleDirectedWeightedGraph<CityBlock, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)
    //create vertices
    val steps = Direction.entries.toTypedArray().flatMap { dir -> (1..3).map { Pair(dir, it) } }
    flatten().forEach { p ->
        if (p.x == this[0].size - 1 && p.y == this.size - 1) {
            graph.addVertex(CityBlock(p, p.value.digitToInt(), Pair(Direction.UP, 0), end = true))
        } else {
            if (p.x == 0 && p.y == 0) graph.addVertex(
                CityBlock(p, p.value.digitToInt(), Pair(Direction.UP, 0), start = true)
            )
            for (step in steps) {
                val (dir, times) = step
                this.getPointAfterMove(p, dir.opposite(), times)?.let {
                    graph.addVertex(CityBlock(p, p.value.digitToInt(), step))
                }
            }
        }
    }
    val vertexMap = graph.vertexSet().associateBy { Pair(it.point, it.stepsBefore) }
    for (vertex in graph.vertexSet()) {
        val (dir, times) = vertex.stepsBefore
        val possibleDirections = Direction.entries.toMutableSet().apply {
            this -= dir.opposite()
            if (times == 3) this -= dir // after three steps, don't continue
        }
        for (possibleDirection in possibleDirections) {
            this.getPointAfterMove(vertex.point, possibleDirection)?.let {
                val newSteps = when {
                    it.x == this[0].size - 1 && it.y == this.size - 1 -> Pair(Direction.UP, 0)
                    possibleDirection == dir -> Pair(dir, times + 1)
                    else -> Pair(possibleDirection, 1)
                }
                val newPoint = vertexMap.getValue(Pair(it, newSteps))
                val edge = graph.addEdge(vertex, newPoint)
                graph.setEdgeWeight(edge, newPoint.cost.toDouble())
            }
        }
    }
    return graph
}

data class CityBlock(
    val point: Point,
    val cost: Int,
    val stepsBefore: Pair<Direction, Int>,
    val start: Boolean = false,
    val end: Boolean = false
)

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

package advent2024.day16

import lib.*
import java.io.File
import java.util.*

const val day = 16
val file = File("src/main/resources/advent2024/day${day}/input")

class Puzzle(private val input: List<String>) {
    var visited: MutableMap<Position, Int> = mutableMapOf()
    var scoreOfBestRoute: Int = Int.MAX_VALUE
    
    fun runPart1() {
        val maze = input.to2DGridOfPointsWithValues()
        val start = Position(maze.findSingleValueInGrid('S'), Direction.RIGHT)
        val end = maze.findSingleValueInGrid('E')

        visited = findOptimalPath(start, end, maze)
    }

    fun runPart2() {
        val maze = input.to2DGridOfPointsWithValues()
        val start = Position(maze.findSingleValueInGrid('S'), Direction.RIGHT)
        val end = maze.findSingleValueInGrid('E')
        
        findOptimalPathAndRoute(start, end, maze, visited)
    }
    
    private fun findOptimalPath(
        start: Position,
        end: Point,
        maze: List<List<Point>>,
        visited: MutableMap<Position, Int> = mutableMapOf(start to 0)
    ): MutableMap<Position, Int> {
        var currentBestScore = Int.MAX_VALUE

        val queue = PriorityQueue<Pair<Position, Int>>(compareByDescending({ it.second }))
        queue.add(Pair(start, 0))
        //bfs with memory and priority queue
        while (queue.isNotEmpty()) {
            val (currentPosition, currentScore) = queue.remove()
            val (currentPoint, currentDirection) = currentPosition
            if (currentPoint == end) {
                if (currentScore < currentBestScore) {
                    println("new score $currentScore, size of queue: ${queue.size}")
                    currentBestScore = currentScore
                    continue
                }
            }

            val nextPoints = setOf(
                Pair(
                    Position(maze.getPointAfterMoveSure(currentPoint, currentDirection), currentDirection),
                    currentScore + 1
                ),
                Pair(Position(currentPoint, currentDirection.turnRight()), currentScore + 1000),
                Pair(Position(currentPoint, currentDirection.turnLeft()), currentScore + 1000),
            ).filter { it.first.point.value != '#' }
            for (point in nextPoints) {
                val (newPosition, newScore) = point

                if (newScore > currentBestScore) continue
                if (visited.containsKey(newPosition) && visited.getValue(newPosition) < newScore) continue

                visited[newPosition] = newScore
                queue += Pair(newPosition, newScore)
            }
        }
        println(currentBestScore)
        scoreOfBestRoute = currentBestScore
        return visited
    }

    private fun findOptimalPathAndRoute(
        start: Position,
        end: Point,
        maze: List<List<Point>>,
        visited: MutableMap<Position, Int> = mutableMapOf(start to 0),
    ) {
        var currentPointsOnBestRoute = setOf<Point>()
        
        val queue = PriorityQueue<Pair<Route, Int>>(compareByDescending({ it.second }))
        queue.add(Pair(Route(start.point, start.direction, setOf(start.point)), 0))
        //bfs with memory and priority queue
        while (queue.isNotEmpty()) {
            val (currentRoute, currentScore) = queue.remove()
            val (currentPoint, currentDirection, currentVisitedPoints) = currentRoute
            if (currentPoint == end) {
                if (currentScore == scoreOfBestRoute) {
                    println("new score $currentScore, size of queue: ${queue.size}")
                    currentPointsOnBestRoute += currentVisitedPoints
                    continue
                }
            }

            val nextPoints = setOf(
                Pair(
                    Position(maze.getPointAfterMoveSure(currentPoint, currentDirection), currentDirection),
                    currentScore + 1
                ),
                Pair(Position(currentPoint, currentDirection.turnRight()), currentScore + 1000),
                Pair(Position(currentPoint, currentDirection.turnLeft()), currentScore + 1000),
            ).filter { it.first.point.value != '#' }
            for (point in nextPoints) {
                val (newPosition, newScore) = point

                if (newScore > scoreOfBestRoute) continue
                if (visited.containsKey(newPosition) && visited.getValue(newPosition) < newScore) continue

                visited[newPosition] = newScore
                queue += Pair(Route(newPosition.point, newPosition.direction, currentVisitedPoints.clone() + newPosition.point), newScore)
            }
        }
        println(scoreOfBestRoute)
        println(currentPointsOnBestRoute.size)
    }
}

data class Position(val point: Point, val direction: Direction)

data class Route(val point: Point, val direction: Direction, val currentRoute: Set<Point>)

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
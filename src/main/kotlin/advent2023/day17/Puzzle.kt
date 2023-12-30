package advent2023.day17

import advent2023.utils.Point
import advent2023.utils.getDirectNeighbours
import advent2023.utils.runPuzzle
import advent2023.utils.to2DGridOfPointsWithValues
import java.io.File
import java.util.*

const val day = 17
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val map = input.to2DGridOfPointsWithValues()
        
        //1021 not ok
        println(map.findMinimumHeatLoss())
    }
    
    fun runPart2() {
        println(input)
    }
}

private fun List<List<Point>>.findMinimumHeatLoss(): Int {
    var currentMinimumHeatLoss = Int.MAX_VALUE
    val start = this[0][0]
    val target = this[this.size - 1][this[0].size - 1]
    val totalManhattanDistance = start.getManhattanDistance(target)
    val heatLossRateAllowance = 1.3
    var currentMinimumLossRate = currentMinimumHeatLoss.toDouble() / totalManhattanDistance
    val pq = PriorityQueue<CruciblePath>(compareBy { 
        val distanceTravelled = totalManhattanDistance - it.lastFourPoints.last().getManhattanDistance(target)
        it.currentHeatLoss / if (distanceTravelled > 0) distanceTravelled else 1
    })
    val cache = mutableMapOf<List<Point>, Int>()
    pq.add(CruciblePath(listOf(start), 0))
    while(pq.isNotEmpty()) {
        val (lastFourPoints, currentHeatLoss) = pq.remove()
        val currentPoint = lastFourPoints.last()
        if (currentPoint == target) {
            if (currentHeatLoss < currentMinimumHeatLoss) {
                currentMinimumHeatLoss = currentHeatLoss
                currentMinimumLossRate = currentMinimumHeatLoss.toDouble() / totalManhattanDistance
                println("found route with heatloss $currentMinimumHeatLoss")
            }
            continue
        }
        val neighbours = this.getDirectNeighbours(currentPoint).neighbours
            .filterNot { it == lastFourPoints.getOrNull(2) } // can't reverse
            .filterNot { it.isInSameDirectionAs(lastFourPoints) }
        for (neighbour in neighbours) {
            val newHeatLoss = currentHeatLoss + neighbour.value.digitToInt()
            val currentDistanceTravelled = neighbour.getManhattanDistance(start)
            val currentRate = newHeatLoss / if (currentDistanceTravelled > 0) currentDistanceTravelled else 1
            if (newHeatLoss + neighbour.getManhattanDistance(target) > currentMinimumHeatLoss) continue
            if (newHeatLoss > 100 && currentRate > heatLossRateAllowance * currentMinimumLossRate) continue
            val newLastFourPoints = lastFourPoints.takeLast(3) + neighbour
            val newPath = CruciblePath(newLastFourPoints, newHeatLoss)
            val cacheKey = newLastFourPoints
            if (cache.contains(cacheKey)) {
                val cachedHeatLoss = cache[cacheKey]!!
                if (newHeatLoss > cachedHeatLoss) continue
            }
            cache[cacheKey] = newHeatLoss
            pq.add(newPath)
        }
    }
    return currentMinimumHeatLoss
}

data class CruciblePath(val lastFourPoints: List<Point>, val currentHeatLoss: Int)

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

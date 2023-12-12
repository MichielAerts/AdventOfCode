package advent2023.day10

import advent2023.utils.*
import advent2023.utils.Direction.*
import java.io.File

const val day = 10
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val tiles = input.to2DGridOfPointsWithValues() 
        val pipeNetwork = PipeNetwork.createPipeNetwork(tiles)
        println(pipeNetwork.findLongestDistanceToS())
    }

    fun runPart2() {
        println(input)      
    }
}

data class PipeNetwork(val s: Point, val network: List<Point>) {
    fun findLongestDistanceToS(): Int =
        (network.size - 1) / 2

    companion object {
        fun createPipeNetwork(tiles: List<List<Point>>): PipeNetwork {
            // find S, find a connecting pipe, create list of connected points
            // if back on S, done
            val s = tiles.findSingleValueInGrid('S')
            val network = mutableListOf(s)
            var currentTile = tiles.findAConnectingTileTo(s)
            var previousTile = s
            network += currentTile
            
            while(currentTile.value != 'S') {
                val (newCurrentTile, newPreviousTile) = tiles.findNextConnectingTile(currentTile, previousTile)
                currentTile = newCurrentTile
                previousTile = newPreviousTile
                network += currentTile
            }
            return PipeNetwork(s, network)
        }
    }
}

/*
| is a vertical pipe connecting north and south.
- is a horizontal pipe connecting east and west.
L is a 90-degree bend connecting north and east.
J is a 90-degree bend connecting north and west.
7 is a 90-degree bend connecting south and west.
F is a 90-degree bend connecting south and east.
. is ground; there is no pipe in this tile.
S is the starting position of the animal; there is a pipe on this tile, but your sketch doesn't show what shape the pipe has.
 */
private fun List<List<Point>>.findAConnectingTileTo(s: Point): Point {
    val neighbours = this.getNeighboursAndDirection(s)
    if (neighbours.getValue(UP).value in listOf('|', '7', 'F')) return neighbours.getValue(UP)
    if (neighbours.getValue(DOWN).value in listOf('|', 'L', 'J')) return neighbours.getValue(DOWN)
    if (neighbours.getValue(LEFT).value in listOf('-', 'F', 'L')) return neighbours.getValue(LEFT)
    if (neighbours.getValue(RIGHT).value in listOf('-', '7', 'J')) return neighbours.getValue(RIGHT)
    throw IllegalStateException("couldn't find a connecting tile to $s")
}

private fun List<List<Point>>.findNextConnectingTile(current: Point, previous: Point): Pair<Point, Point> {
    val previousMove = current.findDirectionOfThisTo(previous)
    val nextPoint = when (current.value) {
        '|' -> this.getPointAfterMove(current, previousMove)
        '-' -> this.getPointAfterMove(current, previousMove)
        'L' -> if (previousMove == DOWN) this.getPointAfterMove(current, RIGHT) else this.getPointAfterMove(current, UP)
        'J' -> if (previousMove == DOWN) this.getPointAfterMove(current, LEFT) else this.getPointAfterMove(current, UP)
        '7' -> if (previousMove == UP) this.getPointAfterMove(current, LEFT) else this.getPointAfterMove(current, DOWN)
        'F' -> if (previousMove == UP) this.getPointAfterMove(current, RIGHT) else this.getPointAfterMove(current, DOWN)
        else -> throw IllegalStateException("nop")
    }
    return Pair(nextPoint, current)
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

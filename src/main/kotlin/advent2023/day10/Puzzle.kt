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
        val allTiles = input.to2DGridOfPointsWithValues()
        val pipeNetwork = PipeNetwork.createPipeNetwork(allTiles)
        // clean up loose pipes, change S to actual pipe
        allTiles.changePoints(allTiles.flatten().toSet() - pipeNetwork.network.toSet(), '.')
        allTiles.changePoint(allTiles.findSingleValueInGrid('S'), 'F')
        
        // create tiles with space in between
        var tiles = allTiles.map { it.joinToString("") { it.value.toString() } }
            .zip(List(input.size) { List(input[0].length) { '.'}.joinToString("") })
            .flatMap { listOf(it.first, it.second) }
            .map { it.toList().joinToString(".") }
            .to2DGridOfPointsWithValues()
        // close the loop once more
        for (row in tiles) for (point in row) {
            if (point.isInBetweenTiles()) {
                val neighbours = tiles.getNeighboursAndDirection(point).mapValues { it.value.value }
                when {
                    neighbours[LEFT] in listOf('L', '-', 'F') && neighbours[RIGHT] in listOf('J', '-', '7') -> point.value = '-'
                    neighbours[UP] in listOf('7', 'F', '|') && neighbours[DOWN] in listOf('L', 'J', '|') -> point.value = '|'    
                }
            }
        }
        
        val ySize = tiles.size
        val xSize = tiles[0].size
        var newTiles = tiles.copy()

        // every tile on outside which is not part of network is O
        newTiles.changePoints( tiles.flatten().filter {
            (it.x == 0 || it.x == (xSize - 1) || it.y == 0 || it.y == (ySize - 1)) && it.value == '.'
        }.toSet(), 'O')
        newTiles.printV()
        var tilesChanged = 1
        // let the O spread till all tiles outside the loop are covered
        while (tilesChanged != 0) {
            tiles = newTiles.copy()
            newTiles = newTiles.copy()
            val pointsToChange = tiles.flatten().filter {
                it.value == '.' && tiles.getDirectNeighbours(it).neighbours.any { it.value == 'O' }
            }.toSet()
            tilesChanged = pointsToChange.size
            newTiles.changePoints(pointsToChange, 'O')
            newTiles.printV()
            println()
        }
        val originalTiles = newTiles.filter { it[0].y % 2 == 0 }.map { it.filter { it.x % 2 == 0 } }
        originalTiles.printV()
        println(originalTiles.flatten().count { it.value == '.' })
        // remove add 'half' points again, count tiles with .
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

private fun Point.isInBetweenTiles(): Boolean =
    x % 2 == 1 || y % 2 == 1

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
    if (neighbours[UP]?.value in listOf('|', '7', 'F')) return neighbours.getValue(UP)
    if (neighbours[DOWN]?.value in listOf('|', 'L', 'J')) return neighbours.getValue(DOWN)
    if (neighbours[LEFT]?.value in listOf('-', 'F', 'L')) return neighbours.getValue(LEFT)
    if (neighbours[RIGHT]?.value in listOf('-', '7', 'J')) return neighbours.getValue(RIGHT)
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
//    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

package advent2017.day22

import lib.*
import java.io.File

const val day = 22
val file = File("src/main/resources/advent2017/day${day}/input")

class Puzzle(private val input: List<String>) {
    
    fun runPart1() {
        val bursts = 10000
        val behaviour: (Char) -> Char = { currentState ->
            when (currentState) {
                '.' -> '#'
                '#' -> '.'
                else -> throw IllegalStateException()
            }
        }
        val infections = simulateVirus(bursts, behaviour)
        println(infections)
    }

    fun simulateVirus(bursts: Int, actOnCurrentNode: (Char) -> Char): Int {
        val size = 500
        val grid = initEmptyGrid(0, size, 0, size)
        val startGrid = input.to2DGridOfPointsWithValues()
        startGrid.allPoints().forEach {
            grid.getPoint((size / 2) + it.x, (size / 2) + it.y)!!.value = it.value
        }
        var virus = grid.getPoint(x = (size / 2) + startGrid[0].size / 2, y = (size / 2) + startGrid.size / 2)
        var direction = Direction.UP
        var infections = 0
        for (i in 1..bursts) {
            requireNotNull(virus)
            direction = when(virus.value) {
                '.' -> direction.turnLeft()
                '#' -> direction.turnRight()
                'F' -> direction.opposite()
                else -> direction
            }
            virus.value = actOnCurrentNode(virus.value)
            if (virus.value == '#') infections++
            virus = grid.getPointAfterMoveSure(virus, direction)
        }
        return infections
    }
    
    fun runPart2() {
        val bursts = 10000000
        val evolvedBehaviour: (Char) -> Char = { currentState ->
            when (currentState) {
                '.' -> 'W'
                'W' -> '#'
                '#' -> 'F'
                'F' -> '.'
                else -> throw IllegalStateException()
            }
        }
        val infections = simulateVirus(bursts, evolvedBehaviour)
        println(infections)
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
package advent2024.day6

import lib.*
import java.io.File

const val day = 6
val file = File("src/main/resources/advent2024/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val map = input.to2DGridOfPointsWithValues()
        var currentPlace: Point? = map.findSingleValueInGrid('^')
        var currentDirection = Direction.UP
        
        val visited = mutableListOf(currentPlace)
        while(currentPlace != null) {
            val potentialNewPlace = map.getPointAfterMove(currentPlace, currentDirection)
            if (potentialNewPlace?.value == '#') {
                currentDirection = currentDirection.turnRight()
                continue
            }
            currentPlace = potentialNewPlace
            visited += currentPlace
        }
        println(visited.filterNotNullTo(HashSet()).size)
    }

    fun runPart2() {
        val map = input.to2DGridOfPointsWithValues()
        var escaped = 0
        var stuck = 0
        
        for (pointToObstruct in map.allPoints()) {
            if (pointToObstruct.value == '.') {
                pointToObstruct.value = '#'
//                println("changed $pointToObstruct")
            } else {
                continue
            }

            var currentPlace: Point? = map.findSingleValueInGrid('^')
            var currentDirection = Direction.UP

            val visited = mutableSetOf(Pair(currentPlace, currentDirection))
            while(true) {
                val potentialNewPlace = map.getPointAfterMove(currentPlace!!, currentDirection)
                if (potentialNewPlace?.value == '#') {
                    currentDirection = currentDirection.turnRight()
                    continue
                }
                currentPlace = potentialNewPlace
                if (currentPlace == null) {
                    escaped++
                    break
                }
                val p = Pair(currentPlace, currentDirection)
                if (p in visited) {
                    stuck++
                    break
                }
                visited += p
            }
            pointToObstruct.value = '.'
        }

        println("Tested all options, escaped: $escaped, stuck: $stuck")
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
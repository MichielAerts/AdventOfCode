package advent2018.day18

import lib.*
import java.io.File

const val day = 18
val file = File("src/main/resources/advent2018/day${day}/input")

const val OPEN = '.'
const val TREE = '|'
const val LUMBERYARD = '#'

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        var area = input.to2DGridOfPointsWithValues()
        val time = 10028
        val resourceValues = mutableMapOf<Int, Int>()
        for (t in 1..time) {
            val newArea = area.copy()
            area.allPoints().forEach { acre ->
                val adjacentAcres = area.getAdjacentNeighbours(acre).neighbours
                
                val newValue = when(acre.value) {
                    OPEN -> if (adjacentAcres.count { it.value == TREE } >= 3) TREE else OPEN
                    TREE -> if (adjacentAcres.count { it.value == LUMBERYARD } >= 3) LUMBERYARD else TREE
                    LUMBERYARD -> if (adjacentAcres.count { it.value == LUMBERYARD } >= 1 && adjacentAcres.count { it.value == TREE } >= 1)
                        LUMBERYARD else OPEN
                    else -> throw UnsupportedOperationException()
                }
                newArea.getPoint(acre.x, acre.y)?.value = newValue
            }
            area = newArea
            val resourceValue = area.findAllValuesInGrid(TREE).count() * area.findAllValuesInGrid(LUMBERYARD).count()
            println("resource value after time $t: $resourceValue")
            resourceValues[t] = resourceValue
//            area.printV()
        }
        //period = 28
        val totalTime = 1_000_000_000
        val resourceValueAfterTotalTime = resourceValues[((totalTime - 10_000) % 28) + 10_000]
        println(resourceValueAfterTotalTime)
//        println(area.findAllValuesInGrid(TREE).count() * area.findAllValuesInGrid(LUMBERYARD).count())
    }

    fun runPart2() {
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
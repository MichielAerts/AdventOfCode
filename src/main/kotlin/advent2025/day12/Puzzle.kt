package advent2025.day12

import lib.*
import java.io.File

const val day = 12
val file = File("src/main/resources/advent2025/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val groups = input.splitBy { it.isEmpty() }
        val shapesByIndex = groups.dropLast(1).map { Shape.toShape(it) }.associateBy { it.index }
        val regions = groups.last().map { Region.toRegion(it, shapesByIndex) }
        println("over 1:")
        println(regions.count { it.coveredArea() > 1.0 })
        println("under 0.75:")
        println(regions.count { it.coveredArea() < 0.75 })
    }

    fun runPart2() {
//        println(input)
    }
}

data class Region(val space: List<List<Point>>, val presents: Map<Shape, Int>) {

    fun printStats() {
        val area = (space.size) * (space[0].size) 
        println("region size: ${space.size} x ${space[0].size}: $area")
        val coveredSpace = presents.entries.sumOf { it.key.shape.allPoints().count { it.value == '#' } * it.value }
        println("total covered space: $coveredSpace: ${coveredSpace.toDouble() / area}")
    }
    
    fun coveredArea(): Double {
        val area = (space.size) * (space[0].size)
        val coveredSpace = presents.entries.sumOf { it.key.shape.allPoints().count { it.value == '#' } * it.value }
        return coveredSpace.toDouble() / area
    }
    fun canFitPresents(): Boolean {
        
        // fit shapes 300 shapes, 50 x 50 grid
        // shape 1, approx. 25000 ways
        //TODO how?
        return true
    }
    
    fun canFitPresentsBrute(): Boolean {
        //300 shapes, 50 x 50 grid
        // shape 1, approx. 2500 x 8 = 20000 ways
        // shape 2, 20000 * ~2500 * 8 = no way
        println("Trying fit for $this")
        var solutions = mutableListOf(space)
        for ((shape, requiredNumber) in presents) {
            println("fitting $requiredNumber of ${shape.index}")
            for (i in 0..<requiredNumber) {
                println("fitting $i of ${shape.index}, solution size = ${solutions.size}")
                val newSolutions = mutableListOf<List<List<Point>>>()
                for (currentSolution in solutions) {
                    for (p in currentSolution.allPoints()) {
                        val newSolution = currentSolution.copy()
                        var shapeFits = true
                        for (sp in shape.shape.allPoints().filter { it.value == '#' }) {
                            val newPointOccupiedByShape = newSolution.getPoint(sp.x + p.x, sp.y + p.y)
                            if (newPointOccupiedByShape == null || newPointOccupiedByShape.value == '#') {
                                shapeFits = false
                            } else {
                                newPointOccupiedByShape.value = '#'
                            }
                        }
                        if (shapeFits) {
                            newSolutions += newSolution
                        }
                    }
                }
                solutions = newSolutions
            }
        }
        return solutions.isNotEmpty()
    }

    companion object {
        fun toRegion(input: String, shapesByIndex: Map<Int, Shape>): Region {
            //4x4: 0 0 0 0 2 0
            val (size, shapeRequirements) = input.split(": ")
            val (x, y) = size.split("x").map { it.toInt() }
            val shapesReqs = shapeRequirements.split(" ").map { it.toInt() }
            return Region(
                space = initEmptyGrid(endX = x - 1, endY = y - 1),
                presents = (0..<shapesReqs.size).associate { shapesByIndex.getValue(it) to shapesReqs[it] }
            )
        }
    }
}

data class Shape(val index: Int, val shape: List<List<Point>>) {
    companion object {
        fun toShape(input: List<String>): Shape {
            //0:
            //###
            //##.
            //##.
            return Shape(input.first()[0].digitToInt(), input.drop(1).to2DGridOfPointsWithValues())
        }
    }
}
fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
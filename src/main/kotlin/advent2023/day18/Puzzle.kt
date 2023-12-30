package advent2023.day18

import advent2023.utils.*
import java.io.File
import kotlin.math.absoluteValue

const val day = 18
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val steps = input.map { Step.createStep(it) }
        val coordinates = steps.fold(listOf(Pos(0,0))) 
            { acc, step -> acc + (acc.last().getNextPos(step.direction, step.amount)) }
        val (minX, maxX, minY, maxY) = with(coordinates) {
            listOf(minOf { it.x }, maxOf { it.x }, minOf { it.y }, maxOf { it.y })
        }
        val offset = Pos(-minX, -minY)
        val terrain = initEmptyGrid(endX = maxX - minX, endY = maxY - minY)
        coordinates.zipWithNext().forEach {
            val start = terrain.getPoint(it.first + offset)!!
            val end = terrain.getPoint(it.second + offset)!!
            terrain.changePoints(start.getPointsInLineTo(end).toSet(), '#')       
        }
//        terrain.printV()
        val flooded = terrain.floodFromOutside()
        println(
            flooded.flatten().count { it.value != 'O' }
        )
    }
    
    fun runPart2() {
        val steps = input.map { Step.createNewStep(it) }
        val coordinates = steps.fold(listOf(Pos(0,0)))
            { acc, step -> acc + (acc.last().getNextPos(step.direction, step.amount)) }
        println(steps)
        println(coordinates)
        val xs = coordinates.flatMap { listOf(it.x, it.x + 1) }.toSet().sorted()
        val ys = coordinates.flatMap { listOf(it.y, it.y + 1) }.toSet().sorted()
        val blocks = xs.zipWithNext().flatMap { xRange -> ys.zipWithNext().map { yRange -> Block('.', xRange.first, xRange.second - 1, yRange.first, yRange.second - 1) } } // left inc, right exc
        println("${xs.size}, ${ys.size}, ${blocks.size}")
        val pairs = coordinates.zipWithNext()
        for (block in blocks) {
            if (!block.isHorizontalLine() && !block.isVerticalLine()) continue
            for (pair in pairs) {
                if (block.isHorizontalLine() && pair.isHorizontalLine() && pair.containsX(block)) block.value = '#'
                if (block.isVerticalLine() && pair.isVerticalLine() && pair.containsY(block)) block.value = '#'
            }
        }
        println(
            blocks.filter { it.value == '#' || it.isInside(pairs) }.sumOf {
                ((it.xRight - it.xLeft).absoluteValue + 1).toLong() * ((it.yUp - it.yDown).absoluteValue + 1).toLong()
            }
        )
    }
}

private fun Pair<Pos, Pos>.isHorizontalLine() = first.y == second.y
private fun Pair<Pos, Pos>.isVerticalLine() = first.x == second.x
private fun Pair<Pos, Pos>.containsX(block: Block) = when {
    block.yUp != first.y -> false
    first.x < second.x -> block.xLeft >= first.x && block.xRight <= second.x
    second.x < first.x -> block.xLeft >= second.x && block.xRight <= first.x
    else -> throw IllegalArgumentException()
}

private fun Pair<Pos, Pos>.containsY(block: Block) = when {
    block.xLeft != first.x -> false
    first.y < second.y -> block.yUp >= first.y && block.yDown <= second.y
    second.y < first.y -> block.yUp >= second.y && block.yDown <= first.y
    else -> throw IllegalArgumentException()
}

private fun Pair<Pos, Pos>.coversY(block: Block) = when {
    first.y < second.y -> block.yUp >= first.y && block.yDown <= second.y
    second.y < first.y -> block.yUp >= second.y && block.yDown <= first.y
    else -> throw IllegalArgumentException()
}

private fun Pair<Pos, Pos>.direction(): Direction = when {
    first.x == second.x && first.y > second.y -> Direction.UP
    first.x == second.x && first.y < second.y -> Direction.DOWN
    first.y == second.y && first.x < second.x -> Direction.RIGHT
    first.y == second.y && first.x > second.x -> Direction.LEFT
    else -> throw IllegalArgumentException()
    
}

data class Block(var value: Char, val xLeft: Int, val xRight: Int, val yUp: Int, val yDown: Int) {
    fun isHorizontalLine() = yUp == yDown
    fun isVerticalLine() = xLeft == xRight

    fun isInside(allLines: List<Pair<Pos, Pos>>): Boolean {
        // for every block, count vertical ranges in x dir, or horizontal ranges in y dir
        // if any of those is uneven, we're in the loop (sum)
        val lines = allLines.filter { it.isVerticalLine() && it.first.x < xLeft && it.coversY(this) }.sortedBy { it.first.x }
        var count = 0
        var idx = 0
        while (idx < lines.size) {
            val line = lines[idx++]
            if (line.first.y != yUp && line.second.y != yUp) {
                count++ //crossing line
            } else {
                if (idx == lines.size) {
                    println("$lines to the left of $this: count: $count)")
                }
                val pairedLine = lines[idx++]
                
                if (line.direction() == pairedLine.direction()) {
                    count++ // situation 1, line2 in same direction
                } 
            }                        
        }
        val inside = count.isUneven()
//        println("$lines to the left of $this: count: $count, inside: $inside)")
        // look left, 
        // count passing vertical lines no_passing
        // count ending vertical lines and divide by two
        // if sum uneven -> inside
        // if uneven vertical lines -> inside
        return inside
    }
}

data class Step(val direction: Direction, val amount: Int) {
    companion object {
        val regex = "(\\w) (\\d+) \\(#(.{2})(.{2})(.{2})\\)".toRegex()
        fun createStep(input: String): Step {
            val groups = regex.matchEntire(input)?.groupValues!!
            val (_, dir, amount) = groups
            return Step(Direction.getDirectionFromFirstLetter(dir), amount.toInt())
        }

        val newRegex = "\\w \\d+ \\(#(.{5})(.{1})\\)".toRegex()
        @OptIn(ExperimentalStdlibApi::class)
        fun createNewStep(input: String): Step {
            val groups = newRegex.matchEntire(input)?.groupValues!!
            val (_, amount, dir) = groups
            val direction = when(dir.toInt()) {
                0 -> Direction.RIGHT
                1 -> Direction.DOWN
                2 -> Direction.LEFT
                3 -> Direction.UP
                else -> throw IllegalArgumentException()
            }
            return Step(direction, amount.hexToInt())
        }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
//    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

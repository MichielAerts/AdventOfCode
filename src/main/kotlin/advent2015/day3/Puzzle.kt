package advent2015.day3

import lib.runPuzzle
import java.io.File

const val day = 3
val file = File("src/main/resources/advent2015/day${day}/input")

class Puzzle(private val input: List<String>) {
    
    fun runPart1() {
        val houses = visitedHouses(this@Puzzle.input[0])
        println(houses.groupingBy { it }.eachCount().count())
    }

    private fun visitedHouses(input: String): MutableList<House> {
        var currentHouse = House(0, 0)
        val houses = mutableListOf(currentHouse)
        for (c in input) {
            var next = currentHouse.next(c)
            houses += next
            currentHouse = next
        }
        return houses
    }

    fun runPart2() {
        val santasInstructions = input[0].filterIndexed { idx, c -> idx % 2 == 0 }
        val roboSantasInstructions = input[0].filterIndexed { idx, c -> idx % 2 == 1 }
        val visitedHouses = visitedHouses(santasInstructions) + visitedHouses(roboSantasInstructions) 
        println(visitedHouses.groupingBy { it }.eachCount().count())
        
    }
}

fun House.next(dir: Char) =
    when(dir) {
        '^' -> House(x, y + 1)
        'v' -> House(x, y - 1)
        '>' -> House(x + 1, y)
        '<' -> House(x - 1, y)
        else -> throw UnsupportedOperationException()
    }
data class House(val x: Int, val y: Int)

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
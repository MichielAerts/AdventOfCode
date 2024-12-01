package advent2023.day2

import lib.findGroupAsInt
import lib.findOptionalGroupAsInt
import lib.runPuzzle
import java.io.File

const val day = 2
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val bag = Bag()
        
        println(
            input.map { Game.createGame(it) }
                .filter { game -> game.isPossible(bag) }
                .sumOf { it.no }
        )
    }
    
    fun runPart2() {
        println(
            input.map { Game.createGame(it) }
                .map { it.minCubes() }
                .sumOf { it.power() }
        )      
    }
}
data class Bag(val cubes: Set = Set(blues = 14, reds = 12, greens = 13)) 

data class Game(val no: Int, val sets: List<Set>) {

    fun minCubes(): Set =
        Set(blues = sets.maxOf { it.blues }, reds = sets.maxOf { it.reds }, greens = sets.maxOf { it.greens })
    
    fun isPossible(bag: Bag) =
        sets.all { it.greens <= bag.cubes.greens && it.blues <= bag.cubes.blues && it.reds <= bag.cubes.reds }
        
    companion object {
        private val gameRegex = Regex("Game (?<no>\\d+)")
        // Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
        fun createGame(input: String): Game {
            val (game, setsLine) = input.split(":")
            val no = gameRegex.findGroupAsInt(game, "no")
            val sets = setsLine.split(";")
                .map { Set.createSet(it) }
            return Game(no, sets)
        }
    }
}

data class Set(val blues: Int = 0, val reds: Int = 0, val greens: Int = 0) {
    
    fun power(): Int = blues * reds * greens
    
    companion object {
        // 3 blue, 4 red
        private val blueRegex = Regex("(?<no>\\d+) blue")
        private val redRegex = Regex("(?<no>\\d+) red")
        private val greenRegex = Regex("(?<no>\\d+) green")
        fun createSet(input: String): Set {
            val blues = blueRegex.findOptionalGroupAsInt(input, "no") ?: 0
            val reds = redRegex.findOptionalGroupAsInt(input, "no") ?: 0
            val greens = greenRegex.findOptionalGroupAsInt(input, "no") ?: 0
            return Set(blues, reds, greens)
        }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

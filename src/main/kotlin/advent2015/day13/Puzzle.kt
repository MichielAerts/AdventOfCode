package advent2015.day13

import lib.allPermutations
import lib.runPuzzle
import java.io.File

const val day = 13
val file = File("src/main/resources/advent2015/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val preferences = input.map { SeatingPreference.toSeatingPreference(it) } 
        val guestsWithPreferences = preferences.groupBy { it.guest }.mapValues { it.value.associate { it.neighbour to it.happiness } }
        val arrangements = allPermutations(guestsWithPreferences.keys).map { it.zipWithNext() + Pair(it.first(), it.last())  }
        println(arrangements.maxOf { arrangement -> arrangement.sumOf { pair ->
            guestsWithPreferences.getValue(pair.first).getValue(pair.second) +
                    guestsWithPreferences.getValue(pair.second).getValue(pair.first)        
        } } )
    }

    fun runPart2() {
        val preferences = input.map { SeatingPreference.toSeatingPreference(it) }.toMutableList()
        val guests = preferences.map { it.guest }
        preferences += guests.map { SeatingPreference("me", it, 0) }
        preferences += guests.map { SeatingPreference(it, "me", 0) }
        
        val guestsWithPreferences = preferences.groupBy { it.guest }.mapValues { it.value.associate { it.neighbour to it.happiness } }
        
        val arrangements = allPermutations(guestsWithPreferences.keys).map { it.zipWithNext() + Pair(it.first(), it.last())  }
        println(arrangements.maxOf { arrangement -> arrangement.sumOf { pair ->
            guestsWithPreferences.getValue(pair.first).getValue(pair.second) +
                    guestsWithPreferences.getValue(pair.second).getValue(pair.first)
        } } )
    }
}

data class SeatingPreference(val guest: String, val neighbour: String, val happiness: Int) {
    companion object {
        /*
        Alice would gain 54 happiness units by sitting next to Bob.
Alice would lose 79 happiness units by sitting next to Carol.
         */
        val regex = "(\\w+) would (gain|lose) (\\d+) happiness units by sitting next to (\\w+).".toRegex()
        fun toSeatingPreference(input: String): SeatingPreference {
            val (_, g, a, u, n) = regex.find(input)?.groupValues!!
            return SeatingPreference(guest = g, neighbour = n, if (a == "gain") u.toInt() else -u.toInt())
        }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
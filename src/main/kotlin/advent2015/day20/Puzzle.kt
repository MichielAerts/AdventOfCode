package advent2015.day20

import lib.runPuzzle
import java.io.File

const val day = 20
val file = File("src/main/resources/advent2015/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val target = input[0].toInt()
        
        val max = 1_000_000
        for (house in 650000..max) {
            var presents = house * 10
            for (elf in 1..(house / 2)) {
                if (house % elf == 0) {
                    presents += elf * 10
                }
            }
            if (house % 1000 == 0) {
                println("house $house got $presents!!")
            }
            if (presents >= target) {
                println("Yay, house $house got $presents!!")
                break
            }
        }
    }

    fun runPart2() {
        val target = input[0].toInt()

        val max = 1_000_000
        val start = 700_000
        for (house in start..max) {
            var presents = house * 11
            for (elf in 1..(house / 2)) {
                if (house % elf == 0 && house / elf <= 50) {
                    presents += elf * 11
                }
            }
            if (house % 1000 == 0) {
                println("house $house got $presents!!")
            }
            if (presents >= target) {
                println("Yay, house $house got $presents!!")
                break
            }
        }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
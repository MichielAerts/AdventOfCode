package advent2023.day1

import lib.runPuzzle
import java.io.File

const val day = 1
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        println(
            input.sumOf { it.firstDigit() * 10 + it.lastDigit() }
        )
    }
    
    fun runPart2() {
        println(
            input.sumOf { 
                it.firstDigitIncludingSpelledOut() * 10 + it.lastDigitIncludingSpelledOut() 
            }
        )      
    }
}

private fun String.firstDigitIncludingSpelledOut(): Int {
    val digit = digitsRegex.find(this)!!.value
    return if (digit in digits.values) digit.toInt() else digits.getValue(digit).toInt()  
}

private fun String.lastDigitIncludingSpelledOut(): Int {
    val digit = digitsRegexReversed.find(this.reversed())!!.value
    return if (digit in digits.values) digit.toInt() else digits.getValue(digit.reversed()).toInt()
}

val digits = mapOf("one" to "1", "two" to "2", "three" to "3", "four" to "4", "five" to "5", "six" to "6", "seven" to "7", "eight" to "8", "nine" to "9")
val digitsRegex = Regex((digits.keys + digits.values).joinToString(separator = "|"))
val digitsRegexReversed = Regex((digits.keys + digits.values).joinToString(separator = "|").reversed())

fun String.firstDigit(): Int = first { it.isDigit() }.digitToInt()
fun String.lastDigit(): Int = last { it.isDigit() }.digitToInt()

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

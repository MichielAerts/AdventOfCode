package advent2018.day14

import lib.runPuzzle
import lib.subListTillEnd
import java.io.File

const val day = 14
val file = File("src/main/resources/advent2018/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val recipe1 = Recipe(3)
        val recipe2 = Recipe(7)
        recipe1.next = recipe2
        recipe2.next = recipe1
        var elf1 = recipe1
        var elf2 = recipe2
        val startOfScoreboard = recipe1
        var endOfScoreboard = recipe2
        val recipesToMakeFirst = 409551
        var currentNumberOfRecipes = 2
        while (currentNumberOfRecipes < (recipesToMakeFirst + 10)) {
            val newRecipes = (elf1.score + elf2.score).toString().toList().map { Recipe(it.digitToInt()) }
            for (newRecipe in newRecipes) {
                endOfScoreboard.next = newRecipe
                newRecipe.next = startOfScoreboard
                endOfScoreboard = newRecipe
                currentNumberOfRecipes++
                if (currentNumberOfRecipes in ((recipesToMakeFirst + 1)..(recipesToMakeFirst + 10))) {
                    print(newRecipe.score)
                }
            }
            repeat(1 + elf1.score) { elf1 = elf1.next }
            repeat(1 + elf2.score) { elf2 = elf2.next }
        }
        println()
    }

    fun runPart2() {
        val recipe1 = Recipe(3)
        val recipe2 = Recipe(7)
        recipe1.next = recipe2
        recipe2.next = recipe1
        var elf1 = recipe1
        var elf2 = recipe2
        val startOfScoreboard = recipe1
        var endOfScoreboard = recipe2
        val recipeScoresToLookFor = "409551".toList().map { it.digitToInt() }
        var currentLastRecipes = List(recipeScoresToLookFor.size) { 0 }
        var currentNumberOfRecipes = 2
        outer@ while (true) {
            val newRecipes = (elf1.score + elf2.score).toString().toList().map { Recipe(it.digitToInt()) }
            for (newRecipe in newRecipes) {
                endOfScoreboard.next = newRecipe
                newRecipe.next = startOfScoreboard
                endOfScoreboard = newRecipe
                currentNumberOfRecipes++
                currentLastRecipes = currentLastRecipes.subListTillEnd(1) + newRecipe.score
                if (currentLastRecipes == recipeScoresToLookFor) {
                    break@outer
                }
                
            }
            repeat(1 + elf1.score) { elf1 = elf1.next }
            repeat(1 + elf2.score) { elf2 = elf2.next }
        }
        println(currentNumberOfRecipes - recipeScoresToLookFor.size)
    }
}

class Recipe(val score: Int) {
    lateinit var next: Recipe
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
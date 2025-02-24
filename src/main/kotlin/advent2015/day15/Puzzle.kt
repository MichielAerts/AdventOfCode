package advent2015.day15

import lib.runPuzzle
import java.io.File
import kotlin.math.max

const val day = 15
val file = File("src/main/resources/advent2015/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val ingredients = input.map { Ingredient.toIngredient(it) }
        var currentMaxScore = 0
        for (i in 0..100) for (j in 0..100) for (k in 0..100) {
            if (i + j + k > 100) continue
            val l = 100 - (i + j + k)
            val first = ingredients[0]
            val second = ingredients[1]
            val third = ingredients[2]
            val fourth = ingredients[3]
            val score = max(first.capacity * i + second.capacity * j + third.capacity * k + fourth.capacity * l, 0) *
                    max(first.durability * i + second.durability * j + third.durability * k + fourth.durability * l, 0) *
                    max(first.flavor * i + second.flavor * j + third.flavor * k + fourth.flavor * l, 0) *
                    max(first.texture * i + second.texture * j + third.texture * k + fourth.texture * l, 0)
            if (score > currentMaxScore) {
                println("i: $i, j: $j, k: $k, l: $l, score: $score")
                currentMaxScore = score
            }
        }
    }

    fun runPart2() {
        val ingredients = input.map { Ingredient.toIngredient(it) }
        var currentMaxScore = 0
        for (i in 0..100) for (j in 0..100) for (k in 0..100) {
            if (i + j + k > 100) continue
            val l = 100 - (i + j + k)
            val first = ingredients[0]
            val second = ingredients[1]
            val third = ingredients[2]
            val fourth = ingredients[3]
            val calories = first.calories * i + second.calories * j + third.calories * k + fourth.calories * l
            if (calories != 500) continue
            val score = max(first.capacity * i + second.capacity * j + third.capacity * k + fourth.capacity * l, 0) *
                    max(first.durability * i + second.durability * j + third.durability * k + fourth.durability * l, 0) *
                    max(first.flavor * i + second.flavor * j + third.flavor * k + fourth.flavor * l, 0) *
                    max(first.texture * i + second.texture * j + third.texture * k + fourth.texture * l, 0)
            if (score > currentMaxScore) {
                println("i: $i, j: $j, k: $k, l: $l, score: $score")
                currentMaxScore = score
            }
        }
    }
}

data class Ingredient(
    val name: String,
    val capacity: Int,
    val durability: Int,
    val flavor: Int,
    val texture: Int,
    val calories: Int
) {
    companion object {
        //Butterscotch: capacity -1, durability -2, flavor 6, texture 3, calories 8
        val regex = "(\\w+): capacity (-?\\d+), durability (-?\\d+), flavor (-?\\d+), texture (-?\\d+), calories (-?\\d+)".toRegex()

        fun toIngredient(input: String): Ingredient {
            val (_, n, c, d, f) = regex.find(input)?.groupValues!!
            val t = regex.find(input)?.groupValues!![5]
            val cal = regex.find(input)?.groupValues!![6]
            return Ingredient(
                name = n,
                capacity = c.toInt(),
                durability = d.toInt(),
                flavor = f.toInt(),
                texture = t.toInt(),
                calories = cal.toInt()
            )
        }
    }
}


fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
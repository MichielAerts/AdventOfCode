package advent2017.day7

import lib.allGroups
import lib.runPuzzle
import java.io.File

const val day = 7
val file = File("src/main/resources/advent2017/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val towers = input.map { Tower.createTower(it) }.associateBy { it.name }
        input.mapNotNull { Tower.structure(it) }
            .forEach { 
                val tower = towers.getValue(it.first)
                val carries = it.second.map { towers.getValue(it) }
                tower.carries += carries
                carries.forEach { it.carriedBy = tower }
            }
        println("bottom: ${towers.values.find { it.carriedBy == null }}")
        
        for (tower in towers.values) {
            tower.calculateTotalWeigth()
        }

        val unbalancedTowers = towers.values.filter { !it.isBalanced() }
        println(unbalancedTowers)
    }

    fun runPart2() {
        println(input)
    }
}

val calculatedTotalWeights = mutableMapOf<String, Int>()

data class Tower(
    val name: String,
    val weight: Int,
    val carries: MutableList<Tower> = mutableListOf(),
    var carriedBy: Tower? = null,
    var totalWeight: Int = weight
) {
    override fun toString(): String = name
    fun calculateTotalWeigth() {
        totalWeight = calculatedTotalWeights.getOrPut(name) {
//            println("calculating weight for $name")
            carries.forEach { it.calculateTotalWeigth() }
            weight + carries.sumOf { it.totalWeight }
        }
    }
    
    fun isBalanced() = carries.isEmpty() ||
            carries.map { it.totalWeight }.toSet().size == 1

    companion object {
        val regex = Regex("(\\w+) \\((\\d+)\\)")
        fun createTower(input: String): Tower {
            //fwft (72) -> ktlj, cntj, xhth
            val towerPart = input.split(" -> ")[0]
            val (name, weight) = regex.allGroups(towerPart)
            return Tower(name, weight.toInt())
        }

        fun structure(input: String): Pair<String, List<String>>? {
            //ktlj (57)
            //fwft (72) -> ktlj, cntj, xhth
            if (!input.contains(" -> ")) return null
            val (towerPart, carryPart)  = input.split(" -> ")
            val (name, weight) = regex.allGroups(towerPart)
            val carries = carryPart.split(", ")
            return Pair(name, carries)
        }
    }
}


fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
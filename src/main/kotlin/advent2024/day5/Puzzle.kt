package advent2024.day5

import lib.mapToPair
import lib.runPuzzle
import lib.splitBy
import java.io.File

const val day = 5
val file = File("src/main/resources/advent2024/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val (rules, updates) = input.splitBy { it.isEmpty() }
            .mapToPair(
                transformLeft = { it.map { Rule.toRule(it) }},
                transformRight = { it.map { Update.toUpdate(it)}}
            )
        println(updates.filter { it.inRightOrder(rules) }.sumOf { it.middle() })
    }

    fun runPart2() {
        val (rules, updates) = input.splitBy { it.isEmpty() }
            .mapToPair(
                transformLeft = { it.map { Rule.toRule(it) }},
                transformRight = { it.map { Update.toUpdate(it)}}
            )
        println(updates.filterNot { it.inRightOrder(rules) }.map { it.fix(rules) }.sumOf { it.middle() })
    }
}

data class Update(val updates: List<Int>) {
    
    fun inRightOrder(rules: List<Rule>): Boolean =
        rules.all { followsRule(it) }
    
    fun followsRule(rule: Rule): Boolean = 
        if (rule.first in updates && rule.second in updates)
            updates.indexOf(rule.first) < updates.indexOf(rule.second)
        else
            true
        
    fun middle(): Int = updates[updates.size / 2]
    
    fun fix(rules: List<Rule>): Update {
        val fixedUpdates = updates.map { e -> ElementAndRemainder(e, updates.filterNot { it == e }) }
            .associate { it.element to it.countOccurenceElementBeforeOthersInRules(rules) }
            .entries
            .sortedByDescending { it.value }
            .map { it.key }
        return Update(fixedUpdates)
    }
    
    companion object {
        fun toUpdate(input: String): Update =
            Update(input.split(",").map { it.toInt() })
    }
}

data class ElementAndRemainder(val element: Int, val remainder: List<Int>) {
    fun countOccurenceElementBeforeOthersInRules(rules: List<Rule>): Int =
        remainder.count { Rule(element, it) in rules }
}

data class Rule(val first: Int, val second: Int) {
    companion object {
        fun toRule(input: String): Rule {
            val (first, second) = input.split("|")
            return Rule(first.toInt(), second.toInt())
        }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
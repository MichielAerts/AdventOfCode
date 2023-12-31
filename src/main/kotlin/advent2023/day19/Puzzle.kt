package advent2023.day19

import advent2023.utils.mapToPair
import advent2023.utils.product
import advent2023.utils.runPuzzle
import advent2023.utils.splitBy
import java.io.File

const val day = 19
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val (workflows, parts) = input.splitBy { it.isEmpty() }
            .mapToPair(
                transformLeft = { it.map { Workflow.createWorkflow(it) }.associateBy { it.name }.toMutableMap() },
                transformRight = { it.map { Part.createPart(it) } }
            )
        workflows["R"] = Workflow("R", emptyList())
        workflows["A"] = Workflow("A", emptyList())

        val sortedParts = parts.groupBy { sortPart(it, workflows.getValue("in"), workflows).name }
        println(
            sortedParts.getValue("A").sumOf { it.x + it.m + it.a + it.s }
        )
    }

    private fun sortPart(part: Part, currentWorkflow: Workflow, allWorkflows: Map<String, Workflow>): Workflow =
        when (currentWorkflow.name) {
            "R", "A" -> currentWorkflow
            else -> sortPart(
                part = part,
                currentWorkflow = allWorkflows.getValue(currentWorkflow.rules.first { it.test(part) }.destination),
                allWorkflows = allWorkflows
            )
        }

    fun runPart2() {
        val (workflows, _) = input.splitBy { it.isEmpty() }
            .mapToPair(
                transformLeft = { it.map { Workflow.createWorkflow(it) }.associateBy { it.name }.toMutableMap() },
                transformRight = { it.map { Part.createPart(it) } }
            )
        workflows["R"] = Workflow("R", emptyList())
        workflows["A"] = Workflow("A", emptyList())
        val all = PartRange(listOf('x', 'm', 'a', 's').associateWith { 1..4000 })
        val sortedParts = sortPartByRange(all, workflows.getValue("in"), workflows)
        println(
            sortedParts.filter { it.first == "A" }.sumOf { it.second.getCombinations() }
        )
    }

    private fun sortPartByRange(
        partRange: PartRange,
        currentWorkflow: Workflow,
        allWorkflows: MutableMap<String, Workflow>
    ): List<Pair<String, PartRange>> =
        when (currentWorkflow.name) {
            "R", "A" -> listOf(currentWorkflow.name to partRange)
            else -> currentWorkflow.processByRange(partRange)
                .flatMap { sortPartByRange(it.second, allWorkflows.getValue(it.first), allWorkflows) }
        }
}

data class Workflow(val name: String, val rules: List<Rule>) {

    // a<2006:qkq, m>2090:A, rfg
    fun processByRange(partRange: PartRange): List<Pair<String, PartRange>> {
        if (partRange.ranges.isEmpty()) return emptyList()
        var currentRange = partRange
        val destinations = mutableListOf<Pair<String, PartRange>>()
        for (rule in rules) {
            val (_, rating, testType, value, destination) = rule
            if (testType == null) {
                destinations += destination to PartRange(currentRange.ranges.toMap())
                return destinations
            }
            val currentRangeForRating = currentRange.ranges.getValue(rating!!)
            val (inRange, outOfRange) = currentRangeForRating.split(testType, value!!)
            outOfRange?.let { 
                currentRange = PartRange(
                    currentRange.ranges.mapValues { if (it.key == rating) outOfRange else it.value}
                )
            } 
            inRange?.let { 
                destinations += destination to PartRange(
                    currentRange.ranges.mapValues { if (it.key == rating) inRange else it.value}
                )
            }
        }
        return destinations
    }
    
    companion object {
        // px{a<2006:qkq,m>2090:A,rfg}
        val regex = "(\\w+)\\{(.*)}".toRegex()
        fun createWorkflow(input: String): Workflow {
            val (_, name, rules) = regex.matchEntire(input)?.groupValues!!
            return Workflow(name, rules.split(",").map { Rule.toRule(it) })
        }
    }
}

data class PartRange(val ranges: Map<Char, IntRange>) {
    fun getCombinations(): Long =
        ranges.values.map { it.last.toLong() - it.first + 1 }.product()
}

data class Part(val x: Int, val m: Int, val a: Int, val s: Int) {
    companion object {
        //{x=787,m=2655,a=1222,s=2876}
        val regex = "\\{x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)}".toRegex()
        fun createPart(input: String): Part {
            val (x, m, a, s) = regex.matchEntire(input)?.groupValues?.drop(1)?.map { it.toInt() }!!
            return Part(x, m, a, s)
        }
    }
}

data class Rule(
    val test: (Part) -> Boolean,
    val rating: Char?,
    val testType: Char?,
    val value: Int?,
    val destination: String,
) {
    companion object {
        //a<2006:qkq,m>2090:A,rfg
        fun toRule(input: String): Rule {
            if (input.contains(":")) {
                val (testInput, dest) = input.split(":")
                return when {
                    testInput.contains("<") -> {
                        val value = testInput.substringAfter("<").toInt()
                        val testType = '<'
                        val (rating, test) = when (testInput[0]) {
                            'm' -> Pair(testInput[0], { part: Part -> part.m < value })
                            'a' -> Pair(testInput[0], { part: Part -> part.a < value })
                            'x' -> Pair(testInput[0], { part: Part -> part.x < value })
                            's' -> Pair(testInput[0], { part: Part -> part.s < value })
                            else -> throw IllegalArgumentException()
                        }
                        Rule(test, rating, testType, value, dest)
                    }

                    testInput.contains(">") -> {
                        val value = testInput.substringAfter(">").toInt()
                        val testType = '>'
                        val (rating, test) = when (testInput[0]) {
                            'x' -> Pair(testInput[0], { part: Part -> part.x > value })
                            'a' -> Pair(testInput[0], { part: Part -> part.a > value })
                            'm' -> Pair(testInput[0], { part: Part -> part.m > value })
                            's' -> Pair(testInput[0], { part: Part -> part.s > value })
                            else -> throw IllegalArgumentException()
                        }
                        Rule(test, rating, testType, value, dest)
                    }

                    else -> throw IllegalArgumentException()
                }
            } else {
                return Rule(
                    test = { true },
                    rating = null,
                    testType = null,
                    value = null,
                    destination = input
                )
            }
        }
    }
}

data class SplitRange(val inRange: IntRange? = null, val outOfRange: IntRange? = null)

private fun IntRange.split(testType: Char, value: Int): SplitRange {
    return when(testType) {
        '<' -> when {
            value in this -> SplitRange(inRange = first..<value, outOfRange = value..last)
            value > this.last -> SplitRange(inRange = this)
            else -> SplitRange(outOfRange = this)
        }
        '>' -> when {
            value in this -> SplitRange(inRange = value + 1 .. last, outOfRange = first..value)
            value < this.first -> SplitRange(inRange = this)
            else -> SplitRange(outOfRange = this)
        }
        else -> throw IllegalArgumentException()
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

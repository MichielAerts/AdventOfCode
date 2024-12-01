package advent2023.day8

import lib.lcm
import lib.runPuzzle
import java.io.File

const val day = 8
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val instructions = ArrayDeque(input[0].toList())
        val nodes = input.drop(2).map { Node.createNode(it) }
        val map = nodes.associateBy { it.name }
        var currentNode = map.getValue("AAA")
        val targetNode = map.getValue("ZZZ")
        var steps = 0
        while (currentNode != targetNode) {
            val instruction = instructions.removeFirst() 
            currentNode = if (instruction == 'R') map.getValue(currentNode.right) else map.getValue(currentNode.left)
            instructions.addLast(instruction)
            steps++
        }
        println(steps)
    }
    
    fun runPart2() {
        val instructions = ArrayDeque(input[0].toList())
        val nodes = input.drop(2).map { Node.createNode(it) }
        val map = nodes.associateBy { it.name }
        var currentNodes = map.values.filter { it.name[2] == 'A' }
        var steps = 0
        while (currentNodes.any { it.name[2] != 'Z' } && steps < 1_000_000) {
            val instruction = instructions.removeFirst()
            currentNodes = if (instruction == 'R') 
                currentNodes.map { map.getValue(it.right) } 
            else 
                currentNodes.map { map.getValue(it.left) }
            instructions.addLast(instruction)
            steps++
            // find repeating cycles for each ghost, then find least common multiple
            // There seem to be no offsets
            currentNodes.forEachIndexed { idx, node -> if (node.name[2] == 'Z') println("ghost $idx on Z at step $steps") }
        }
        println(steps)
        println(lcm(longArrayOf(17141, 18827, 20513, 12083, 22199, 19951)))
    }
}

data class Node(val name: String, val left: String, val right: String) {
//    AAA = (BBB, CCC)
    companion object {
        private val regex = "(\\w{3}) = \\((\\w{3}), (\\w{3})\\)".toRegex()
        fun createNode(input: String): Node {
            val (_, name, left, right) = regex.matchEntire(input)?.groupValues!!
            return Node(name, left, right)
        }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
//    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

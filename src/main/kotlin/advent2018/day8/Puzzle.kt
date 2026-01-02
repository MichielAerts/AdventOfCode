package advent2018.day8

import lib.runPuzzle
import java.io.File

const val day = 8
val file = File("src/main/resources/advent2018/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val numbers = input[0].split(" ").map { it.toInt() }
        val root = buildTree(numbers)
        println(root.calculateMetadata())
    }


    fun runPart2() {
        val numbers = input[0].split(" ").map { it.toInt() }
        val root = buildTree(numbers)
        println(root.calculateValue())
    }
    
    private fun buildTree(numbers: List<Int>): Node {
        val root = Node(numbers[0], numbers[1])
        var currentNode = root
        var i = 2
        while (i < numbers.size) {
            while (currentNode.childNodes.size < currentNode.numberOfChildNodes) {
                val newNode = Node(numbers[i], numbers[i + 1])
                currentNode.childNodes += newNode
                newNode.parentNode = currentNode
                currentNode = newNode
                i += 2
            }
            while (currentNode.metadata.size < currentNode.numberOfMetadataEntries) {
                currentNode.metadata += numbers[i]
                i += 1
            }
            currentNode = currentNode.parentNode ?: break
        }
        return root
    }
}

class Node (
    val numberOfChildNodes: Int,
    val numberOfMetadataEntries: Int,
    val childNodes: MutableList<Node> = mutableListOf(),
    var parentNode: Node? = null,
    val metadata: MutableList<Int> = mutableListOf()
) {
    fun calculateMetadata(): Int = 
        metadata.sum() + childNodes.sumOf { it.calculateMetadata() }

    fun calculateValue(): Int {
        if (childNodes.isEmpty()) return metadata.sum()
        return metadata.sumOf { childNodes.getOrNull(it - 1)?.calculateValue() ?: 0 }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
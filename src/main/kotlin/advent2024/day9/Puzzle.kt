package advent2024.day9

import lib.runPuzzle
import java.io.File

const val day = 9
val file = File("src/main/resources/advent2024/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val disk = initDiskMap()
        val totalSize = disk.count { it != null }
        var checksum = 0L
        for (i in 0..<totalSize) {
            val first = disk.removeFirst()
            if (first != null) {
                checksum += i * first
            } else {
                var element: Int? = null
                do {
                    element = disk.removeLast()
                } while (element == null)
                checksum += i * element
            }
        }
        println(checksum)
    }

    private fun initDiskMap(): ArrayDeque<Int?> {
        val disk = ArrayDeque<Int?>()
        var currentFileId = 0
        for ((idx, c) in input[0].withIndex()) {
            val n = c.digitToInt()
            if (idx % 2 == 0) {
                repeat(n) { disk += currentFileId }
                currentFileId++
            } else {
                repeat(n) { disk += null }
            }
        }
        return disk
    }

    lateinit var start: Node
    lateinit var end: Node
    
    fun runPart2() {
        initLinkedList()
        printList()
        
        var currentFileNode = end
        while (true) {
            while (currentFileNode.type == Type.EMPTY) {
                currentFileNode = currentFileNode.previous!!
            }
            val sizeFile = currentFileNode.size
            var candidateEmptyNode = start
            while(currentFileNode != candidateEmptyNode) {
                candidateEmptyNode = candidateEmptyNode.next!!
                val emptySize = candidateEmptyNode.size
                if (candidateEmptyNode.type == Type.FILE ||
                    emptySize < sizeFile) {
                    continue
                }
                //fits

                var previousNode = candidateEmptyNode.previous
                var nextNode = candidateEmptyNode.next
                val newFileNode = Node(Type.FILE, sizeFile, currentFileNode.id, previousNode, null)
                val newEmptyNode = Node(Type.EMPTY, emptySize - sizeFile, null, newFileNode, nextNode)
                previousNode?.next = newFileNode
                newFileNode.next = newEmptyNode
                nextNode?.previous = newEmptyNode

                currentFileNode.type = Type.EMPTY
                currentFileNode.id = null
                break
            }
            if (currentFileNode.previous == null) break 
            currentFileNode = currentFileNode.previous!!
//            printList()
        }
        var checkSum = 0L
        var position = 0
        var node = start
        while (true) {
            for (pos in position..<(position + node.size)) {
                if (node.type == Type.FILE) checkSum += pos * node.id!!
            }
            position += node.size
            if (node.next == null) break
            node = node.next!!
        }
//        printList()
        println(checkSum)
    }

    private fun printList() {
        var currentNode = start
        println(currentNode)
        while (currentNode.next != null) {
            currentNode = currentNode.next!!
            println(currentNode)
        }
        println()
    }

    private fun initLinkedList() {
        val size = input[0].length
        var previousNode: Node? = null
        var currentNode: Node? = null
        for ((idx, c) in input[0].withIndex()) {
            val no = c.digitToInt()
            currentNode = when {
                idx == 0 -> {
                    start = Node(Type.FILE, no, 0, null, null)
                    start
                }
                idx == size - 1 -> {
                    end = Node(Type.FILE, no, idx / 2, previousNode, null)
                    end
                }
                else -> {
                    val type = Type.type(idx)
                    Node(type, no, if (type == Type.FILE) idx / 2 else null, previousNode, null)
                }
            }
            previousNode?.let { it.next = currentNode }
            previousNode = currentNode
        }
    }

}

data class Node(
    var type: Type,
    val size: Int,
    var id: Int? = null,
    var previous: Node? = null,
    var next: Node? = null
) {
    override fun toString(): String =
        "Type: $type, Size: $size" + if (id != null) " Id: $id" else ""
}

enum class Type {
    FILE, EMPTY;

    companion object {
        fun type(index: Int): Type =
            if (index % 2 == 0) FILE else EMPTY
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
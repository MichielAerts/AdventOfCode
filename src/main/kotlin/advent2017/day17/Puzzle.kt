package advent2017.day17

import lib.runPuzzle
import lib.splitAt
import java.io.File

const val day = 17
val file = File("src/main/resources/advent2017/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val stepSize = 349
        var currentPosition = 0
        val numberOfInsertions = 2017
        var buffer = listOf(0)
        for (insertionValue in 1..numberOfInsertions) {
            currentPosition = (currentPosition + stepSize) % buffer.size
            val (first, second) = buffer.splitAt(currentPosition + 1)
            currentPosition += 1
            buffer = first + insertionValue + second
        }
        println(buffer[buffer.indexOf(2017) + 1])
    }

    fun runPart2b() {
        val stepSize = 349
        val numberOfInsertions = 50_000_000
        val buffer = Buffer(Node(0))
        
        for (insertionValue in 1..numberOfInsertions) {
            if (insertionValue % 10000 == 0) println(insertionValue)
            buffer.move(stepSize) 
            buffer.insert(insertionValue)
        }
        println(buffer.start.next!!.value)
    }

    fun runPart2() {
        // only need the one after start, which gets filled when position is 0
        val stepSize = 349
        val numberOfInsertions = 50_000_000
        var currentPosition = 0
        var valueAtPositionOne = 0
        for (insertionValue in 1..numberOfInsertions) {
            currentPosition = (currentPosition + 1 + stepSize) % insertionValue
            if (currentPosition == 0) {
                valueAtPositionOne = insertionValue
            }
        }
        println(valueAtPositionOne)
    }
}

class Buffer(val start: Node, var position: Node = start) {
    fun move(stepSize: Int) {
        for (i in 1..stepSize) {
            position = position.next ?: start
        }
    }

    fun insert(insertionValue: Int) {
        val newNode = Node(insertionValue, position.next)
        position.next = newNode
        position = position.next ?: start
    }
    
    fun print() {
        println("position is ${position.value}")
        println("buffer: ")
        var currentNode = start
        while(true) {
            print("${currentNode.value} ")
            if (currentNode.next != null) {
                currentNode = currentNode.next!!
            } else {
                break
            }
        }
    println()
    }
}

class Node(val value: Int, var next: Node? = null) 

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
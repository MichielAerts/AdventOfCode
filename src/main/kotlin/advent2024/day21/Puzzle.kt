package advent2024.day21

import lib.*
import java.io.File
import kotlin.math.absoluteValue

const val day = 21
val file = File("src/main/resources/advent2024/day${day}/input")

class Puzzle(private val input: List<String>) {
    private val numericKeyPad: List<List<Point>> = 
        """
            789
            456
            123
             0A
        """.trimIndent().lines().to2DGridOfPointsWithValues()
    private val numericKeyPadMoves = createMoveMap(numericKeyPad)

    private val directionalKeyPad = 
        """
             ^A
            <v>
        """.trimIndent().lines().to2DGridOfPointsWithValues()
    private val directionalKeyPadMoves = createMoveMap(directionalKeyPad)
    
    fun runPart1() {

        val sequences = input.associateWith {
            println(it)
            val firstRobotSequence = createSequenceOnInputPad(it, numericKeyPadMoves)
            val secondRobotSequence = createSequenceOnInputPad(firstRobotSequence, directionalKeyPadMoves)
            val mySequence = createSequenceOnInputPad(secondRobotSequence, directionalKeyPadMoves)
            mySequence.length
        }

        println(
            sequences.entries.sumOf {
                it.key.substringBefore('A').toInt() * it.value
            }
        )
    }

    fun runPart2() {
        
        val sequences = input.associateWith {
            println(it)
            val firstRobotSequence = createSequenceOnInputPad(it, numericKeyPadMoves)
            determineShortestSequenceLength(firstRobotSequence, directionalKeyPadMoves, 25)
        }
        
        println(sequences.entries.sumOf {
            it.key.substringBefore('A').toInt() * it.value
        })
    }

    private fun determineShortestSequenceLength(
        input: String,
        keyMap: Map<Pair<Char, Char>, List<Char>>,
        numberOfRobots: Int
    ): Long {
        val moves = ("A" + input).zipWithNext().groupingBy { it }.eachCount().mapValues { (_, v) -> v.toLong() }
        var currentMoves = moves.toMap()
        var movesOnNextPad: MutableMap<Pair<Char, Char>, Long> = mutableMapOf()
        (1..numberOfRobots).forEach { 
            movesOnNextPad = mutableMapOf()
            currentMoves.forEach { (move, no) ->
                val sequenceOnNextPad = "A" + keyMap.getValue(move).joinToString("", postfix = "A")
                sequenceOnNextPad.zipWithNext().forEach { movesOnNextPad.merge(it, no) { acc, v -> acc + v } }
            }
            currentMoves = movesOnNextPad.toMap()
        }
        return movesOnNextPad.values.sum()
    }

    private fun createSequenceOnInputPad(
        goalSequence: String,
        keyMap: Map<Pair<Char, Char>, List<Char>>,
    ): String {
        val moves = ("A" + goalSequence).toList().zipWithNext()
            .map { keyMap.getValue(Pair(it.first, it.second)).joinToString("", postfix = "A") }
        return moves.joinToString("")
    }
    
    private fun createMoveMap(keyPad: List<List<Point>>): Map<Pair<Char, Char>, List<Char>> {
        val (xHole, yHole) = keyPad.findSingleValueInGrid(' ')
        val allKeyPadMoves = mutableMapOf<Pair<Char, Char>, List<Char>>()
        
        for (current in keyPad.allPoints()) {
            for (other in keyPad.allPoints()) {
                allKeyPadMoves[Pair(current.value, other.value)] = buildList {
                    val dx = other.x - current.x
                    val dy = other.y - current.y
                    val horizontalMoves = List(dx.absoluteValue) { if (dx < 0) '<' else '>' }
                    val verticalMoves = List(dy.absoluteValue) { if (dy < 0) '^' else 'v' }
                    if (current.x == xHole && other.y == yHole) { //7 to A
                        addAll(horizontalMoves + verticalMoves)
                    } else if (current.y == yHole && other.x == xHole) {
                        addAll(verticalMoves + horizontalMoves)
                    } else {
                        if ('<' in horizontalMoves) {
                            addAll(horizontalMoves + verticalMoves)
                        } else {
                            addAll(verticalMoves + horizontalMoves)
                        }
                    }
                }
            }
        }
        return allKeyPadMoves
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
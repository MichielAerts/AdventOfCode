package advent2017.day12

import lib.mapToPair
import lib.runPuzzle
import java.io.File

const val day = 12
val file = File("src/main/resources/advent2017/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val programs = getPrograms()
        val start = 0
        val visited = programs.getGroupOf(start)
        println(visited.size)
    }

    private fun Map<Int, Program>.getGroupOf(
        startingProgram: Int
    ): Set<Int> {
        val start = getValue(startingProgram)

        val visited = mutableSetOf(start.id)
        val queue = ArrayDeque(listOf(start))
        while (queue.isNotEmpty()) {
            val program = queue.removeFirst()
            visited += program.id
            for (connectedProgram in program.connections) {
                if (connectedProgram.id !in visited) {
                    queue.add(connectedProgram)
                }
            }
        }
        return visited
    }

    fun runPart2() {
        val programs = getPrograms()

        val allGroups = mutableMapOf<Int, Set<Int>>()
        while (allGroups.values.sumOf { it.size } < input.size) {
            val currentGroups = allGroups.values.flatten()
            val start = programs.keys.first { it !in currentGroups }
            val group = programs.getGroupOf(start)
            allGroups[start] = group
        }
        
        println(allGroups)
        println(allGroups.size)
    }

    private fun getPrograms(): Map<Int, Program> {
        val programs = input.map { Program.getProgram(it) }.associateBy { it.id }
        input.map { Program.connections(it) }.forEach {
            val sourceProgram = programs.getValue(it.first)
            val connections = it.second.filterNot { it == sourceProgram.id }
                .map { programs.getValue(it) }
            sourceProgram.connections.addAll(connections)
        }
        return programs
    }
}

data class Program(val id: Int, val connections: MutableList<Program> = mutableListOf()) {

    override fun toString(): String = id.toString()
    
    companion object {
        fun getProgram(input: String): Program {
            val (program, _) = input.split(" <-> ")
            return Program(program.toInt())
        }
        
        fun connections(input: String): Pair<Int, List<Int>> {
            val (program, connections) = input.split(" <-> ").mapToPair(
                transformLeft = { it.toInt() },
                transformRight = { it.split(", ").map { it.toInt() }}
            )
            return Pair(program, connections)
        } 
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
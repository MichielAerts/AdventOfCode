package advent2018.day7

import lib.allGroups
import lib.runPuzzle
import java.io.File

const val day = 7
val file = File("src/main/resources/advent2018/day${day}/input")

class Puzzle(private val input: List<String>) {
    
    fun runPart1() {
        val steps = input.flatMap { Step.getStepNames(it) }.toSortedSet().map { Step(it) }.associateBy { it.name }
        input.forEach { line ->
            val (source, next) = Step.getStepNames(line)
            steps.getValue(source).next += steps.getValue(next)
        }
        println(steps.topologicalSort().joinToString("") { it.name })
    }

    fun runPart2() {
        val steps = input.flatMap { Step.getStepNames(it) }.toSortedSet().map { Step(it) }.associateBy { it.name }
        input.forEach { line ->
            val (source, next) = Step.getStepNames(line)
            steps.getValue(source).next += steps.getValue(next)
            steps.getValue(next).dependsOn += steps.getValue(source)
        }
        
        val sortedSteps = steps.topologicalSort()
        val numberOfSteps = sortedSteps.size
        val numberOfWorkers = 5
        val toDo = sortedSteps.toMutableList()
        val done = mutableListOf<Step>()
        val workers = (1..numberOfWorkers).map { Worker(it) }.toMutableList()
        var time = 0
        while (done.size < numberOfSteps) {
            for (worker in workers) {
                if (worker.isDoneWithTask()) {
                    done += worker.removeTask()
                }

                if (worker.isAvailable()) {
                    val availableStep = toDo.firstOrNull { it.dependsOn.isEmpty() || it.dependsOn.all { it in done } }
                    if (availableStep != null) {
                        worker.workingOn += availableStep
                        toDo.remove(availableStep)
                    }
                }

                if (!worker.isAvailable()) {
                    worker.timeWorkingOnCurrentStep++
                }
            }
            
            time++
            
        }
        println(time - 1)
    }
    
    private fun Map<String, Step>.topologicalSort(): List<Step> {
        val sortedList = mutableListOf<Step>()
        val steps = this.values.toMutableList()
        while (steps.isNotEmpty()) {
            val outgoingEdges = steps.flatMap { it.next }
            val start = steps.first { it !in outgoingEdges }
            sortedList += start
            steps.remove(start)
        }
        return sortedList
    }
}

class Worker(
    val id: Int,
    val workingOn: MutableList<Step> = mutableListOf(),
    var timeWorkingOnCurrentStep: Int = 0
) {
    override fun toString(): String {
        return "worker: $id, working on $workingOn for $timeWorkingOnCurrentStep seconds"
    }
    
    fun isAvailable(): Boolean = workingOn.isEmpty()
    
    fun isDoneWithTask(): Boolean {
        if (workingOn.isEmpty()) return false
        return workingOn.first().time == timeWorkingOnCurrentStep
    }
    
    fun removeTask(): Step {
        val finishedTask = workingOn.removeFirst()
        timeWorkingOnCurrentStep = 0
        return finishedTask
    }
}


data class Step(
    val name: String,
    val next: MutableList<Step> = mutableListOf(),
    val dependsOn: MutableList<Step> = mutableListOf(),
    val time: Int = name[0] - 'A' + 61
) {
    override fun toString(): String {
        return "$name, next: ${next.map { it.name }}, depends on: ${dependsOn.map { it.name }}"
    }

    companion object {
        val regex = Regex("Step (\\w{1}) must be finished before step (\\w{1}) can begin\\.")
        fun getStepNames(input: String): List<String> {
            return regex.allGroups(input)
        }
    }
}


fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
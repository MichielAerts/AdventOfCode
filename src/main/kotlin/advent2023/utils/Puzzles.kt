package advent2023

import kotlin.time.measureTime

fun runPuzzle(part: Int, puzzlePart: () -> Unit) {
    println("Part $part of Day $day")
    val timeTaken = measureTime {
        puzzlePart()
    }
    println("took $timeTaken")
}

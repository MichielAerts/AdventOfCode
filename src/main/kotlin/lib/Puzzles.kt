package lib

import kotlin.time.measureTime

fun runPuzzle(day: Int, part: Int, puzzle: () -> Unit) {
    println("Part $part of Day $day")
    val timeTaken = measureTime {
        puzzle()
    }
    println("took $timeTaken")
}

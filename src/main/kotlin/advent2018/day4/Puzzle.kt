package advent2018.day4

import lib.allGroups
import lib.runPuzzle
import lib.splitBeforeInclusive
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val day = 4
val file = File("src/main/resources/advent2018/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val records = input.map { Record.toRecord(it) }.sortedBy { it.time }
        val shifts = records.splitBeforeInclusive { it.line.contains("Guard") }.map { Shift.toShift(it) }
        val sleepsByGuard = shifts.groupBy { it.id }.mapValues { it.value.flatMap { it.sleeps } }
        val mostAsleepGuard = sleepsByGuard.maxBy { it.value.sumOf { it.second - it.first + 1 } }
        val mostAsleepMinute = mostAsleepGuard.value.mostAsleepMinute().key
        
        println(mostAsleepGuard.key * mostAsleepMinute)
        
        val mostAsleepMinuteByGuard = sleepsByGuard.filter { it.value.isNotEmpty() }.mapValues { it.value.mostAsleepMinute() }
        val guardWithMostAsleepMinute = mostAsleepMinuteByGuard.entries.maxBy { it.value.value }
        println(guardWithMostAsleepMinute.key * guardWithMostAsleepMinute.value.key)
    }
    
    private fun List<Pair<Int, Int>>.mostAsleepMinute() =
        flatMap { (it.first..it.second).toList() }
            .groupingBy { it }.eachCount()
            .entries.maxBy { it.value }

    fun runPart2() {
//        println(input)
    }
}

data class Record(val time: LocalDateTime, val line: String) {
    companion object {
        val format: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        
        fun toRecord(input: String): Record {
            val (time, line) = input.split("] ")
            return Record(LocalDateTime.parse(time.drop(1), format), line)
        }
    }
}

data class Shift(val id: Int, val sleeps: List<Pair<Int, Int>> = emptyList()) {
    companion object {
        val guard = Regex("Guard #(\\d+) begins shift")
        fun toShift(records: List<Record>): Shift {
            val (id) = guard.allGroups(records.first().line).map { it.toInt() }
            val sleeps = 
                records
                    .drop(1).chunked(2)
                    .map { it.map { it.time.minute } }
                    .map { Pair(it[0], it[1] - 1) }
            return Shift(id, sleeps)
        }
    }
}


fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
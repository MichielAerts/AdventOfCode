package advent2018.day15

import lib.*
import java.io.File
import java.util.*

const val day = 15
val file = File("src/main/resources/advent2018/day${day}/input")

private const val GOBLIN = 'G'
private const val ELF = 'E'
private const val EMPTY = '.'

class Puzzle(private val input: List<String>) {
    fun runPart1() {
    }

    fun runPart2() {
        for (ap in 3..50) {
            val map = input.to2DGridOfUnits(ap)
            println("elves alive at the start ${map.findAllValuesInGrid(ELF).count()}: attack power $ap")
            val simulationTime = 1000
            combat@ for (t in 0..simulationTime) {
                val units = map.allPoints().filter { it.value == GOBLIN || it.value == ELF }
                for (unit in units) {
                    if (unit.value == EMPTY) continue
                    val enemies = map.findAllValuesInGrid(unit.enemy())
                    if (enemies.isEmpty()) {
                        val remainingHp =
                            map.allPoints().filter { it.value == GOBLIN || it.value == ELF }.sumOf { (it as Unit).hp }
                        println("Combat ends at $t, remaining hp: $remainingHp, outcome: ${t * remainingHp}")
                        println("elves alive ${map.findAllValuesInGrid(ELF).count()}")
                        break@combat
                    }

                    val squaresNextToEnemies = enemies.flatMap { map.getDirectNeighbours(it).neighbours }
                    var unitAfterMove = unit
                    if (unit !in squaresNextToEnemies) {
                        //move
                        val openSquaresNextToEnemies = squaresNextToEnemies.filter { it.value == EMPTY }
                        val nextStep = map.shortestPathBetween(unit, openSquaresNextToEnemies)?.get(1)
                        if (nextStep != null) {
                            nextStep.value = unit.value
                            (nextStep as Unit).hp = (unit as Unit).hp
                            nextStep.attackPower = unit.attackPower
                            unit.value = '.'
                            unit.hp = 200
                            unit.attackPower = 3
                            unitAfterMove = nextStep
                        }
                    }
                    //attack
                    val unitThatAttacks = (unitAfterMove as Unit)
                    val enemiesNextToUnit =
                        map.getDirectNeighbours(unitThatAttacks).neighbours.filter { it.value == unitThatAttacks.enemy() }
                            .map { it as Unit }
                    if (enemiesNextToUnit.isNotEmpty()) {
                        val lowestHitPoints = enemiesNextToUnit.minOf { it.hp }
                        val enemyToAttack = enemiesNextToUnit.filter { it.hp == lowestHitPoints }.minWith(readingOrder)
                        enemyToAttack.hp -= unitThatAttacks.attackPower
                        if (enemyToAttack.hp <= 0) {
                            enemyToAttack.value = EMPTY
                        }
                    }
                }
            }
        }
    }
}

class Unit(x: Int, y: Int, value: Char, var hp: Int = 200, var attackPower: Int = 3) : Point(x, y, value = value) {
    override fun toString(): String = "Unit: (x: $x, y: $y, z: $z, c: $value, hp: $hp)"
}
val readingOrder = compareBy<Point>({ it.y }, { it.x })

fun List<String>.to2DGridOfUnits(attackPowerElves: Int = 3): List<List<Unit>> = this.mapIndexed { y, r ->
    r.toList().mapIndexed { x, v -> Unit(x, y, value = v, hp = 200, attackPower = if (v == ELF) attackPowerElves else 3) }
}

private fun List<List<Point>>.shortestPathBetween(source: Point, targets: List<Point>): List<Point>? {
    //if multiple paths, return the one with the first step in reading order
    val distances = mutableMapOf<Point, Int>().withDefault { Int.MAX_VALUE }
    val previous = mutableMapOf<Point, Point>()
    
    val pq = PriorityQueue<Pair<Point, Int>>(compareBy { it.second })
    pq.add(source to 0)
    
    while(pq.isNotEmpty()) {
        val (closestPoint, distance) = pq.remove()
        for (neighbour in this.getDirectNeighbours(closestPoint).neighbours.filter { it.value == EMPTY }) {
            val newDistance = distance + 1
            val oldDistance = distances.getValue(neighbour)
            if (newDistance <= oldDistance) {
                if (newDistance < oldDistance) {
                    distances[neighbour] = newDistance
                    pq.add(neighbour to newDistance)
                }
                val currentPointBeforeNeighbour = previous[neighbour]
                // 6,3 should have 7,3 as previous, 5,3 should have 6,3
                if (currentPointBeforeNeighbour == null || 
                    (closestPoint.y < currentPointBeforeNeighbour.y || (closestPoint.y == currentPointBeforeNeighbour.y && closestPoint.x < currentPointBeforeNeighbour.x))) {
                    previous[neighbour] = closestPoint
                }
            }
        }
    }
    val closestDistance = distances.filter { it.key in targets }.minOfOrNull { it.value }
    val closestTarget = 
        distances.filter { it.key in targets && it.value == closestDistance }.entries
            .minWithOrNull(compareBy({ it.key.y }, { it.key.x }))?.key
    return closestTarget?.let {
        val path = mutableListOf(closestTarget)
        var currentPoint: Point = closestTarget
        while (currentPoint != source) {
            val previousPoint = previous.getValue(currentPoint)
            path += previousPoint
            currentPoint = previousPoint
        }
        path.reversed()
    }
}

private fun Point.enemy() = when(this.value) {
    GOBLIN -> ELF
    ELF -> GOBLIN
    else -> throw UnsupportedOperationException()
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
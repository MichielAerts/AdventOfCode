package advent2018.day13

import lib.*
import lib.Direction.*
import java.io.File

const val day = 13
val file = File("src/main/resources/advent2018/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val track = input.to2DGridOfPointsWithValues()
        val carts = mutableListOf<Cart>()
        track.allPoints().filter { it.value in listOf('<', '>', '^', 'v') }.forEach { 
            carts += Cart.newCart(it)
            it.value = if (it.value == '>' || it.value == '<') '-' else '|'
        }
        val maxTicks = 20000
        outer@ for (t in 1..maxTicks) {
            carts.sortBy { it.currentPosition.y + it.currentPosition.x }
            for (cart in carts) {
                if (cart.crashed) continue
                val (currentPosition, currentDirection, nextTurn) = cart
                val nextPoint = track.getPointAfterMoveSure(track.getPoint(currentPosition)!!, currentDirection)
                if (nextPoint.pos() in carts.filter { !it.crashed }.map { it.currentPosition }) {
                    println("Crash! at ${nextPoint.pos()}")
                    println("time: $t, carts: $carts")
                    cart.crashed = true
                    val otherCart = carts.first { it.currentPosition == nextPoint.pos() }
                    otherCart.crashed = true
                    if (carts.count { !it.crashed } == 1) {
                        println(carts.filter { !it.crashed })
                        break@outer
                    }
                }
                when (nextPoint.value) {
                    '+' -> {
                        cart.currentDirection = currentDirection.turn(nextTurn)
                        cart.nextTurn = nextTurn.next()
                    }
                    '/', '\\' -> {
                        cart.currentDirection = currentDirection.turn(nextPoint.value)
                    }
                    else -> {}
                }
                cart.currentPosition = nextPoint.pos()
            }
        }
    }

    fun runPart2() {
    }
}
enum class Turn {
    LEFT, STRAIGHT, RIGHT;
    fun next() = when(this) {
        LEFT -> STRAIGHT
        STRAIGHT -> RIGHT
        RIGHT -> LEFT
    }
}

private fun Direction.turn(turn: Turn): Direction =
    when(turn) {
        Turn.LEFT -> this.turnLeft()
        Turn.STRAIGHT -> this
        Turn.RIGHT -> this.turnRight()
    }

private fun Direction.turn(corner: Char): Direction =
    when(corner) {
        '/' -> when(this) {
                UP -> RIGHT
                DOWN -> LEFT
                RIGHT -> UP
                LEFT -> DOWN
            }
        '\\' -> when(this) {
                UP -> LEFT
                DOWN -> RIGHT
                RIGHT -> DOWN
                LEFT -> UP
            }
        else -> throw UnsupportedOperationException()
    }

data class Cart(
    var currentPosition: Pos,
    var currentDirection: Direction,
    var nextTurn: Turn,
    var crashed: Boolean = false
) {
    companion object {
        fun newCart(point: Point): Cart {
            return Cart(
                currentPosition = Pos(point.x, point.y),
                currentDirection =  when(point.value) {
                    '>' -> RIGHT
                    '<' -> LEFT
                    '^' -> UP
                    'v' -> DOWN
                    else -> throw UnsupportedOperationException()
                },
                nextTurn = Turn.LEFT
            )
        }
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
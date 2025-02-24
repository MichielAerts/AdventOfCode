package lib

import org.apache.commons.math3.util.ArithmeticUtils.gcd
import kotlin.math.sqrt

fun solveQuadraticEquation(a: Double, b: Double, c: Double): Pair<Double, Double>? {
    val discriminant = b * b - 4 * a * c
    if (discriminant < 0) {
        // No real roots
        return null
    }
    val root1 = (-b + sqrt(discriminant)) / (2 * a)
    val root2 = (-b - sqrt(discriminant)) / (2 * a)
    return Pair(root1, root2)
}

fun Int.isUneven(): Boolean =
    this % 2 == 1

fun Int.isEven(): Boolean =
    this % 2 == 0

private fun lcm(a: Long, b: Long): Long {
    return a * (b / gcd(a, b))
}

fun <T> List<T>.allSublists(): List<List<T>> = when {
    isEmpty() -> listOf(listOf())
    else -> drop(1).allSublists().let { it + it.map { it + first() } }
}

fun <T> Collection<T>.powerset(): Set<Set<T>> = when {
    isEmpty() -> setOf(setOf())
    else -> drop(1).powerset().let { it + it.map { it + first() } }
}

fun <T> allPermutations(input: Set<T>): Set<List<T>> {
    if (input.isEmpty()) return emptySet()
    
    fun <T> _allPermutations(list: List<T>): Set<List<T>> {
        if (list.isEmpty()) return setOf(emptyList())
        
        val result = mutableSetOf<List<T>>()
        for (i in list.indices) {
            _allPermutations(list - list[i]).forEach {
                item -> result.add(item + list[i])
            }
        }
        return result
    }
    
    return _allPermutations(input.toList())
}

fun lcm(input: LongArray): Long {
    var result = input[0]
    for (i in 1 until input.size) result = lcm(result, input[i])
    return result
}

private fun gcd(a: Long, b: Long): Long {
    var a = a
    var b = b
    while (b > 0) {
        val temp = b
        b = a % b // % is remainder
        a = temp
    }
    return a
}

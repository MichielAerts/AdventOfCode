package advent2023.utils

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
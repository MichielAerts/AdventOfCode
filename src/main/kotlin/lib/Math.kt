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

fun solveQuadraticEquationWithZeroA(a: Double, b: Double, c: Double): Pair<Double, Double>? {
    if (a == 0.0) {
        return Pair(-c / b, -c / b)
    }
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

data class Point3D(val x: Double, val y: Double, val z: Double) {
    operator fun minus(other: Point3D) = Point3D(x - other.x, y - other.y, z - other.z)
    operator fun plus(other: Point3D) = Point3D(x + other.x, y + other.y, z + other.z)
    operator fun times(scalar: Double) = Point3D(x * scalar, y * scalar, z * scalar)

    fun magnitude() = sqrt(x * x + y * y + z * z)
    fun dot(other: Point3D) = x * other.x + y * other.y + z * other.z
    fun cross(other: Point3D) = Point3D(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x
    )
    fun normalize() = this * (1.0 / magnitude())
}

data class IntPoint3D(val x: Long, val y: Long, val z: Long) {
    fun toPoint3D() = Point3D(x.toDouble(), y.toDouble(), z.toDouble())
}

data class Sphere(val center: IntPoint3D, val manhattanRadius: Long) {
    val centerDouble: Point3D = center.toPoint3D()
    val radiusDouble: Double = manhattanRadius.toDouble()
}

sealed class IntersectionResult {
    data class TwoPoints(val point1: Point3D, val point2: Point3D) : IntersectionResult()
    data class OnePoint(val point: Point3D) : IntersectionResult()
    data class NoIntersection(val reason: String) : IntersectionResult()
}

fun findSphereIntersection(
    sphere1: Sphere,
    sphere2: Sphere,
    sphere3: Sphere,
    epsilon: Double = 1e-10
): IntersectionResult {
    val c1 = sphere1.centerDouble
    val c2 = sphere2.centerDouble
    val c3 = sphere3.centerDouble
    val r1 = sphere1.radiusDouble
    val r2 = sphere2.radiusDouble
    val r3 = sphere3.radiusDouble

    // Vector from c1 to c2
    val d12 = c2 - c1
    val dist12 = d12.magnitude()

    // Check if sphere centers 1 and 2 are identical
    if (dist12 < epsilon) {
        return IntersectionResult.NoIntersection("Spheres 1 and 2 have the same center")
    }

    // Check if spheres 1 and 2 are too far apart
    if (dist12 > r1 + r2 + epsilon) {
        return IntersectionResult.NoIntersection("Spheres 1 and 2 don't intersect (too far apart)")
    }

    // Unit vector ex from c1 to c2
    val ex = d12.normalize()

    // Vector from c1 to c3
    val d13 = c3 - c1

    // Scalar projection i of d13 onto ex
    val i = ex.dot(d13)

    // Component of d13 perpendicular to ex
    val temp = d13 - ex * i
    val tempMag = temp.magnitude()

    // Check if all three centers are collinear
    if (tempMag < epsilon) {
        return IntersectionResult.NoIntersection("All three sphere centers are collinear")
    }

    // Unit vector ey perpendicular to ex
    val ey = temp.normalize()

    // Scalar projection j of d13 onto ey
    val j = ey.dot(d13)

    // Unit vector ez = ex × ey
    val ez = ex.cross(ey)

    // Solve for coordinates in the transformed system
    val xi = (r1 * r1 - r2 * r2 + dist12 * dist12) / (2.0 * dist12)
    val eta = (r1 * r1 - r3 * r3 + i * i + j * j - 2.0 * i * xi) / (2.0 * j)
    val zetaSquared = r1 * r1 - xi * xi - eta * eta

    // Check if intersection exists
    if (zetaSquared < -epsilon) {
        return IntersectionResult.NoIntersection("No intersection exists (geometric constraint)")
    }

    // Handle tangent case (single point)
    if (zetaSquared < epsilon) {
        val point = c1 + ex * xi + ey * eta
        return IntersectionResult.OnePoint(point)
    }

    // Two intersection points
    val zeta = sqrt(zetaSquared)
    val point1 = c1 + ex * xi + ey * eta + ez * zeta
    val point2 = c1 + ex * xi + ey * eta - ez * zeta

    return IntersectionResult.TwoPoints(point1, point2)
}
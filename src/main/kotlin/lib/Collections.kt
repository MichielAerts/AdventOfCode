package lib

import org.paukov.combinatorics3.Generator

fun <E> List<E>.subListTillEnd(fromIndex: Int): List<E> = this.subList(fromIndex, this.size)
fun List<Int>.product(): Int = this.reduce { acc, i -> acc * i }
fun List<Long>.product(): Long = this.reduce { acc, i -> acc * i }
fun <E> List<E>.repeat(copyFactor: Int): List<E> =
    (1..< copyFactor).fold(this) { acc, _ -> acc + this}

inline fun <T> Iterable<T>.takeWhileInclusive(
    predicate: (T) -> Boolean
): List<T> {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = predicate(it)
        result
    }
}

fun cartesianProduct(a: List<String>, b: List<String>, vararg others: List<String>): List<List<String>> =
    (listOf(a, b).plus(others))
        .fold(listOf(listOf<String>())) { acc, set ->
            acc.flatMap { list -> set.map { element -> list + element } }
        }
        .toList()

fun <E> List<E>.allPermutations(): List<List<E>> =
    Generator.permutation(this).simple().stream().toList()

fun <E> List<E>?.subList(fromIndex: Int): List<E>? = this?.subList(fromIndex, this.size)
fun <T> List<T>.toPair(): Pair<T, T> {
    if (this.size != 2) {
        throw IllegalArgumentException("List is not of length 2!")
    }
    return Pair(this[0], this[1])
}

@Suppress("UNCHECKED_CAST")
fun <R, A, B> List<R>.mapToPair(transformLeft: (R) -> A = { it as A }, transformRight: (R) -> B = { it as B }): Pair<A, B> {
    if (this.size != 2) {
        throw IllegalArgumentException("List is not of length 2!")
    }
    return Pair(transformLeft(this[0]), transformRight(this[1]))
}

val <T> List<T>.tail: List<T>
    get() = subList(1, size)
val <T> List<T>.head: T
    get() = first()

fun <T> List<T>.headTail() = Pair(head, tail)
fun <E> List<E>.splitBy(splitter: (E) -> Boolean): List<List<E>> {
    val list = mutableListOf<MutableList<E>>()
    var currentList = mutableListOf<E>()
    for (item in this) {
        if (splitter(item)) {
            list += currentList;
            currentList = mutableListOf();
        } else {
            currentList += item;
        }
    }
    if (currentList.isNotEmpty()) list += currentList
    return list;
}

fun IntRange.containsRange(o: IntRange): Boolean {
    if (this.first > this.last || o.first > o.last) throw IllegalStateException("only incrementing IntRange supported")
    return o.first >= this.first && o.last <= this.last
}

fun IntRange.hasOverlap(o: IntRange): Boolean {
    if (this.first > this.last || o.first > o.last) throw IllegalStateException("only incrementing IntRange supported")
    return this.intersect(o).isNotEmpty()
}

fun <E> List<E>.withoutItemAt(idx: Int) = filterIndexed { i, _ -> i != idx }

fun <E> List<List<E>>.transpose(): List<List<E>> {
    val t = MutableList(this[0].size) { MutableList(this.size) { this[0][0] } }

    for (y in indices) {
        for (x in 0..< this[0].size) {
            t[x][y] = this[y][x]
        }
    }
    return t
}

fun <E> List<E>.allPairs(): List<Pair<E, E>> {
    val pairs = mutableListOf<Pair<E, E>>()
    return if (size < 2) {
        return pairs
    } else {
        for (i in 0..<size) {
            for (j in (i + 1)..<size) {
                pairs += Pair(this[i], this[j])
            }
        }
        pairs
    }
}

fun List<List<Char>>.countAllOccurrences(c: Char): Int = joinToString("") { it.joinToString("") }.count { it == c }
fun List<Char>.countOccurrences(c: Char): Int = joinToString("").count { it == c }
fun List<Char>.hasAllDifferentCharacters(): Boolean = this.size == this.toSet().size
fun <T> List<T>.asRepeatedSequence() =
    generateSequence(0) {
        (it + 1) % this.size
    }.map(::get)

fun <E> List<E>.findOrThrow(function: (E) -> Boolean): E =
    this.find(function) ?: throw IllegalStateException("not found")

fun <K, V> Map<K, V>.getAllInListOrThrow(vararg keys: K): List<V> {
    val newList = mutableListOf<V>()
    for (key in keys) {
        newList.add(this.getValue(key))
    }
    return newList
}

fun <K, V, R> Pair<Map<K, V>, Map<K, V>>.merge(merger: (V?, V?) -> R): Map<K, R> {
    return (first.keys.asSequence() + second.keys.asSequence())
        .associateWith { merger(first[it], second[it]) }
}
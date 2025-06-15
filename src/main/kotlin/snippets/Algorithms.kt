package snippets

fun main() {
    println(generateAllSubsets(listOf(1, 2, 3)))
}

fun generateAllSubsets(input: List<Int>): MutableSet<Set<Int>> {
    return input.fold(initial = mutableSetOf(setOf())) { result, element -> 
        result.addAll(
            generateAllSubsets(input.filterNot { it == element })
                .map { setOf(element) + it })
        result
    }
}
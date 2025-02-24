package lib

fun Regex.groupAsLong(str: String, group: String): Long =
    find(str)?.groups?.get(group)?.value?.toLong() ?: throw IllegalArgumentException("couldn't")

fun Regex.groupAsInt(str: String, group: String): Int =
    find(str)?.groups?.get(group)?.value?.toInt() ?: throw IllegalArgumentException("couldn't")

fun Regex.optionalGroupAsInt(str: String, group: String): Int? =
    find(str)?.groups?.get(group)?.value?.toInt()

fun Regex.groupAsString(str: String, group: String): String =
    find(str)?.groups?.get(group)?.value ?: throw IllegalArgumentException("couldn't")

fun Regex.group(str: String, group: String): String =
    groupAsString(str, group)

val alphabet = listOf(
    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
    'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
)

inline fun <reified T : Enum<T>> Regex.groupAsEnum(str: String, group: String): T =
    enumValueOf(find(str)?.groups?.get(group)?.value?.uppercase() ?: throw IllegalArgumentException("couldn't"))

operator fun Regex.contains(text: CharSequence): Boolean = this.matches(text)

data class RegexM(val regex: CharArray, val etaTransitions: Map<Int, List<Int>>) {
    fun match(input: String): Boolean {
        var states = mutableSetOf(0)
        for (i in input.indices) {
            states.addAll(states.flatMap { etaTransitions.dfs(it) })
            println(states)
            states = states.filter { regex[it] == input[i] || regex[it] == '.' }.map { it + 1 }.toMutableSet()
            println(states)
        }
        return states.any { it == regex.lastIndex }
    }

    companion object {
        fun toRegex(regex: String): RegexM {
            val re = regex.toCharArray()
            val stack = ArrayDeque<Int>()
            val etaTransitions = re.flatMapIndexed { index, c ->
                val etaTransitions = mutableListOf<Pair<Int, Int>>()
                if (c == '(' || c == ')' || c == '*') etaTransitions.add(Pair(index, index + 1))
                when (c) {
                    ')' -> {
                        var pop = stack.removeFirst()
                        if (re[pop] == '|') {
                            etaTransitions.add(Pair(pop, index))
                            pop = stack.removeFirst()
                        }
                        if (index < re.lastIndex && re[index + 1] == '*') {
                            etaTransitions.add(Pair(index + 1, pop))
                            etaTransitions.add(Pair(pop, index + 1))
                        }
                    }

                    '*' -> {
                        if (re[index - 1] != ')') {
                            etaTransitions.add(Pair(index, index - 1))
                            etaTransitions.add(Pair(index - 1, index))
                        }
                    }

                    '|' -> {
                        etaTransitions.add(Pair(stack.first(), index + 1))
                    }

                    in 'a'..'z', in 'A'..'Z', '.', '(' -> {}
                    else -> throw IllegalArgumentException()
                }
                if (c == '(' || c == '|') stack.addFirst(index)
                return@flatMapIndexed etaTransitions
            }
            return RegexM(
                re,
                etaTransitions.groupBy { it.first }.mapValues { it.value.map { it.second } }.toSortedMap()
            )
        }
    }
}
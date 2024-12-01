package snippets

fun main() {

    val input = listOf(
        setOf(1, 2, 3),
        setOf(1, 3, 2),
        setOf(2, 1, 3),
        setOf(4, 5, 6),
    )
    val summerMemo = summerMemo()
    input.forEach { println(summerMemo(it)) }

}

fun summerMemo(): (Set<Int>) -> Int {
    val cache = mutableMapOf<Set<Int>, Int>()
    return { numbers: Set<Int> -> cache.getOrPut(numbers) { summer(numbers) } }
}

fun summer(numbers: Set<Int>): Int {
    println("calculating $numbers")
    return numbers.sum()
}

//    val next = counter()
//    println(next())
//    println(next())
//    println(next())
fun counter(): () -> Int {
    var i = 0
    return { i++ }
}


//    val errorLogger= createLogger("ERROR")
//    errorLogger("Panic!")
//    println(substract(8)(2))

fun createLogger(logLevel: String) = { message: String -> println("$logLevel: $message") }

fun substract(x: Int) = { y: Int -> x - y }

interface StarTrekRepository {
    fun getCaptain(starshipName: String): String
    fun addCaptain(starshipName: String, captainName: String)
}

class DefaultStarTrekRepository : StarTrekRepository {
    private val captains = mutableMapOf("USS Enterprise" to "Jean-Luc Picard")
    override fun getCaptain(starshipName: String): String {
        return captains[starshipName] ?: "Unknown"
    }

    override fun addCaptain(starshipName: String, captainName: String) {
        captains[starshipName] = captainName
    }
}

class LoggingGetCaptain(private val repository: StarTrekRepository): StarTrekRepository by repository {
    override fun getCaptain(starshipName: String): String {
        println("bla")
        return repository.getCaptain(starshipName)
    }
}

class ValidatingAdd(private val repository: StarTrekRepository): StarTrekRepository by repository {
    private val maxLength = 15
    override fun addCaptain(starshipName: String, captainName: String) {
        require(captainName.length < maxLength)
        repository.addCaptain(starshipName, captainName)
    }
}

val default = DefaultStarTrekRepository()
val withLogAndVal = ValidatingAdd(LoggingGetCaptain(default))


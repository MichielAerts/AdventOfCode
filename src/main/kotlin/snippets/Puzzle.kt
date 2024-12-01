package snippets

fun main() {
    //given an infinite number of quarters (25 cents), dimes (10 cents), nickels (5 cents) and pennies (1 cent), 
    // write code to calculate the number of ways of reprensenting n cents

    // new plan 
    // n = 100, check case for 0 quarters, 1, 2, 3 etc
    // number = sol with 0 plus, sol with 1, sol with 2, sol with 3, sol with 4
    // sol with 4 -> done, return 1
    // sol with 3 -> 25 cents left, solve sol with 0 dimes, 1 dime, 2 dimes
    var solutions = 0
    val n = 100
    for (q in 0..(n / 25)) {
        solutions += solveWithQuarters(n - q * 25)
    }
    println(solutions)
    println(solve(coins, 100))

}

// if we have already calculated a value for a certain coin and rest combination, return that, don't recalculate 
val cache = mutableMapOf<Pair<Int, Int>, Int>()

fun solve(coins: List<Int>, rest: Int): Int {
    require(rest >= 0)
    if (coins.size == 1) return 1
    val currentCoin = coins[0]
    val coinPlusRest = Pair(currentCoin, rest)
    if (cache.contains(coinPlusRest)) return cache.getValue(coinPlusRest)
    var solutions = 0
    for (i in 0..(rest / currentCoin)) {
        solutions += solve(coins.drop(1), rest - currentCoin * i)
    }
    cache[coinPlusRest] = solutions
    return solutions
}

fun solveWithQuarters(rest: Int): Int {
    var solutions = 0
    for (d in 0..(rest / 10)) {
        solutions += solveWithDimes(rest - d * 10)
    }
    return solutions
}

fun solveWithDimes(rest: Int): Int {
    var solutions = 0
    for (n in 0..(rest / 5)) {
        solutions += solveWithNickels(rest - n * 5)
    }
    return solutions
}

fun solveWithNickels(rest: Int): Int {
    require(rest >= 0)
    return 1
}

val coins = listOf(25, 10, 5, 1)


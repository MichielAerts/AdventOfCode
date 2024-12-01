package advent2023.day7

import lib.runPuzzle
import org.paukov.combinatorics3.Generator
import java.io.File

const val day = 7
val file = File("src/main/resources/advent2023/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val hands = input.map { Hand.createHand(it) }
        println(hands.sortedDescending().mapIndexed { idx, hand -> (idx + 1) * hand.bid }.sum())
    }
    
    fun runPart2() {
        val hands = input.map { Hand.createHand(it) }
        println(hands.sortedDescending().mapIndexed { idx, hand -> (idx + 1) * hand.bid }.sum())
    }
}

data class Hand(val cards: List<Char>, val bid: Int): Comparable<Hand> {

    override fun compareTo(other: Hand): Int = comparator.compare(this, other)

    fun getJokerCardType(): HandType {
        val cardsInput = cards.joinToString("")
        val noOfJokers = cardsInput.count { it == 'J' }
        val notJs = cardsInput.filterNot { it == 'J' }
        val potentialHands = when (noOfJokers) {
            0 -> listOf(cardsInput)
            1 -> permutations1.map { cardsInput.replaceJs(it) }
            2 -> permutations2.map { cardsInput.replaceJs(it) }
            3 -> permutations3.map { cardsInput.replaceJs(it) }
            4 -> listOf(notJs.repeat(5))
            else -> listOf("AAAAA")
        }
        return potentialHands.map { getType(it.toList()) }.minOf { it }
    }
    
    fun getType(): HandType {
        val cardsByLabel = cards.groupBy { it }.mapValues { it.value.size }
        return when {
            cardsByLabel.containsValue(5) -> HandType.FIVE_OF_A_KIND
            cardsByLabel.containsValue(4) -> HandType.FOUR_OF_A_KIND
            cardsByLabel.containsValue(3) && cardsByLabel.containsValue(2) -> HandType.FULL_HOUSE
            cardsByLabel.containsValue(3) -> HandType.THREE_OF_A_KIND
            cardsByLabel.values.count { it == 2 } == 2 -> HandType.TWO_PAIR
            cardsByLabel.containsValue(2) -> HandType.ONE_PAIR
            else -> HandType.HIGH_CARD
        } 
    }

    private fun getType(cards: List<Char>): HandType {
        val cardsByLabel = cards.groupBy { it }.mapValues { it.value.size }
        return when {
            cardsByLabel.containsValue(5) -> HandType.FIVE_OF_A_KIND
            cardsByLabel.containsValue(4) -> HandType.FOUR_OF_A_KIND
            cardsByLabel.containsValue(3) && cardsByLabel.containsValue(2) -> HandType.FULL_HOUSE
            cardsByLabel.containsValue(3) -> HandType.THREE_OF_A_KIND
            cardsByLabel.values.count { it == 2 } == 2 -> HandType.TWO_PAIR
            cardsByLabel.containsValue(2) -> HandType.ONE_PAIR
            else -> HandType.HIGH_CARD
        }
    }

    companion object {
//        val order = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2')
        val order = listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J')
        //32T3K 765
        fun createHand(input: String): Hand {
            val (cardsInput, bidInput) = input.split(" ")
            return Hand(cardsInput.toList(), bidInput.toInt())
        }
        
        private val permutations1 = Generator.permutation(order).withRepetitions(1).stream().toList()
        private val permutations2 = Generator.permutation(order).withRepetitions(2).stream().toList()
        private val permutations3 = Generator.permutation(order).withRepetitions(3).stream().toList()
        
        private val comparator: Comparator<Hand> = compareBy(Hand::getJokerCardType)
            .thenComparing { first, second -> order.indexOf(first.cards[0]).compareTo(order.indexOf(second.cards[0])) }
            .thenComparing { first, second -> order.indexOf(first.cards[1]).compareTo(order.indexOf(second.cards[1])) }
            .thenComparing { first, second -> order.indexOf(first.cards[2]).compareTo(order.indexOf(second.cards[2])) }
            .thenComparing { first, second -> order.indexOf(first.cards[3]).compareTo(order.indexOf(second.cards[3])) }
            .thenComparing { first, second -> order.indexOf(first.cards[4]).compareTo(order.indexOf(second.cards[4])) }
    }
}

private fun String.replaceJs(chars: List<Char>): String {
    var out = ""
    var count = 0
    for (c in this) {
        out += if (c == 'J') chars[count++] else c
    }
    return out
}
enum class HandType {
    FIVE_OF_A_KIND, FOUR_OF_A_KIND, FULL_HOUSE, THREE_OF_A_KIND, TWO_PAIR, ONE_PAIR, HIGH_CARD
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}

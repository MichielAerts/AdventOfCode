package advent2015.day21

import lib.combinations
import lib.runPuzzle
import java.io.File
import kotlin.math.ceil

const val day = 21
val file = File("src/main/resources/advent2015/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val boss = Player(name = "boss", hp = 109, damage = 8, armor = 2)
        val shop = Shop()
        val outfits = shop.weapons
            .flatMap { weapon -> shop.armor.combinations(0).map { listOf(weapon, *it.toTypedArray())} + 
                    shop.armor.combinations(1).map { listOf(weapon, *it.toTypedArray()) } } 
            .flatMap { weaponAndArmor -> shop.rings.combinations(0).map { weaponAndArmor + listOf(*it.toTypedArray()) } +   
                    shop.rings.combinations(1).map { weaponAndArmor + listOf(*it.toTypedArray()) } +
                    shop.rings.combinations(2).map { weaponAndArmor + listOf(*it.toTypedArray()) } }
        val win = outfits.sortedBy { it.sumOf { it.cost } }
                .asSequence()
                .map { Pair(it, Player("me", 100, it.sumOf { it.damage }, it.sumOf { it.armor })) }
                .find { p -> Game(p.second, boss).play() == p.second }
        println(win!!.first.sumOf { it.cost })
    }

    fun runPart2() {
        val boss = Player(name = "boss", hp = 109, damage = 8, armor = 2)
        val shop = Shop()
        val outfits = shop.weapons
            .flatMap { weapon -> shop.armor.combinations(0).map { listOf(weapon, *it.toTypedArray())} +
                    shop.armor.combinations(1).map { listOf(weapon, *it.toTypedArray()) } }
            .flatMap { weaponAndArmor -> shop.rings.combinations(0).map { weaponAndArmor + listOf(*it.toTypedArray()) } +
                    shop.rings.combinations(1).map { weaponAndArmor + listOf(*it.toTypedArray()) } +
                    shop.rings.combinations(2).map { weaponAndArmor + listOf(*it.toTypedArray()) } }
        val win = outfits.sortedByDescending { it.sumOf { it.cost } }
            .asSequence()
            .map { Pair(it, Player("me", 100, it.sumOf { it.damage }, it.sumOf { it.armor })) }
            .find { p -> Game(p.second, boss).play() == boss }
        println(win!!.first.sumOf { it.cost })
    }
}
data class Shop(
    val weapons: List<Item> = listOf(
        Item("Dagger", 8, 4, 0),
        Item("Shortsword", 10, 5, 0),
        Item("Warhammer", 25, 6, 0),
        Item("Longsword", 40, 7, 0),
        Item("Greataxe", 74, 8, 0),
    ),
    val armor: List<Item> = listOf(
        Item("Leather", 13, 0, 1),
        Item("Chainmail", 31, 0, 2),
        Item("Splintmail", 53, 0, 3),
        Item("Bandedmail", 75, 0, 4),
        Item("Platemail", 102, 0, 5),
    ),
    val rings: List<Item> = listOf(
        Item("Damage +1", 25, 1, 0),
        Item("Damage +2", 50, 2, 0),
        Item("Damage +3", 100, 3, 0),
        Item("Defense +1", 20, 0, 1),
        Item("Defense +2", 40, 0, 2),
        Item("Defense +3", 80, 0, 3),
    )
)

data class Item(val name: String, val cost: Int, val damage: Int, val armor: Int)
data class Game(val player1: Player, val player2: Player) {
    fun play(): Player {
        val damageP1 = maxOf(player1.damage - player2.armor, 1)
        val damageP2 = maxOf(player2.damage - player1.armor, 1)
        val turnsP1 = ceil(player2.hp.toDouble() / damageP1)
        val turnsP2 = ceil(player1.hp.toDouble() / damageP2)
        return if (turnsP1 <= turnsP2) player1 else player2 
    }
}

data class Player(val name: String, var hp: Int, val damage: Int, val armor: Int)

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
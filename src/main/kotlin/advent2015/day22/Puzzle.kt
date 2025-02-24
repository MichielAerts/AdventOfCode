package advent2015.day22

import lib.runPuzzle
import java.io.File
import java.util.*

const val day = 22
val file = File("src/main/resources/advent2015/day${day}/input")

class Puzzle(private val input: List<String>) {
    fun runPart1() {
        val boss = Player(name = "boss", hp = 55, damage = 8, armor = 0)
        val me = Wizard(name = "me", hp = 50, mana = 500, armor = 0)
        
        val game = Game(me, boss)
        val win = listOf("Shield", "Recharge", "Magic Missile", "Shield", "Poison", "Recharge", "Magic Missile", "Shield", "Recharge", "Magic Missile", "Shield", "Poison", "Recharge", "Magic Missile", "Shield", "Recharge", "Magic Missile").map { spells[it]!! }
        var minMana = win.sumOf { it.cost }   
        
        println(minMana)   
        
        val pq = PriorityQueue<Game>(compareBy({ it.manaSpent }))
        pq.add(game)
        while (pq.isNotEmpty()) {
            val currentGame = pq.remove()
            val availableSpells = currentGame.availableSpells()
            for (spell in availableSpells) {
                val newGame = Game(
                    wizard = currentGame.wizard.copy(),
                    boss = currentGame.boss.copy(),
                    activeEffects = currentGame.activeEffects.toMutableMap(),
                    performedSpells = currentGame.performedSpells.toMutableList(),
                    manaSpent = currentGame.manaSpent
                )
                val result = newGame.playTurn(spell)
//                println("$result: ${newGame.manaSpent}")
                if (result == "wizard" && newGame.manaSpent < minMana) {
                    println("new win! mana spent ${newGame.manaSpent}")
                    minMana = newGame.manaSpent
                    continue
                }
                if (result == "boss") continue
                if (newGame.manaSpent > minMana) continue
                pq.add(newGame)
            }
        }
    }

    fun runPart2() {
        println(input)
    }
}

data class Game(
    val wizard: Wizard,
    val boss: Player,
    var activeEffects: MutableMap<Spell, Int> = mutableMapOf(),
    val performedSpells: MutableList<Spell> = mutableListOf(),
    var manaSpent: Int = 0
) {
    fun playTurn(spell: Spell): String? {
        wizardsTurn(spell)?.let { return it }
        bossTurn()?.let { return it }
        return null
    }
    
    fun play(spells: List<Spell>): String? {
        for (spell in spells) {
            wizardsTurn(spell)?.let { return it }
            println(this)
            bossTurn()?.let { return it }
            println(this)
        }
        return null
    }


    fun availableSpells(): List<Spell> {
        val activeSpells = activeEffects.filter { it.value > 1 }
        return spells.values.filter { it !in activeSpells && it.cost < wizard.mana }
    }
    
    private fun winner(): String? {
        if (wizard.mana <= 0) return "boss"
        if (boss.hp <= 0) return "wizard"
        if (wizard.hp <= 0) return "boss"
        return null
    }

    private fun bossTurn(): String? {
        applyEffects()
        winner()?.let { return it }
        wizard.hp -= maxOf(boss.damage - wizard.armor, 1)
        wizard.armor = 0
        winner()?.let { return it }
        return null
    }

    private fun applyEffects() {
        val newlyActiveEffects = mutableMapOf<Spell, Int>()
        
        for ((spell, turns) in activeEffects) {
            when (spell.name) {
                "Shield" -> { wizard.armor += 7 }
                "Poison" -> { boss.hp -= 3 }
                "Recharge" -> { wizard.mana += 101 }
            }
            if (turns - 1 > 0) {
                newlyActiveEffects[spell] = turns - 1
            }
        }
        activeEffects = newlyActiveEffects
    }

    private fun wizardsTurn(spell: Spell): String? {
        wizard.hp -= 1
        winner()?.let { return it }
        applyEffects()
        winner()?.let { return it }
        wizard.mana -= spell.cost
        manaSpent += spell.cost
        performedSpells += spell
        when (spell.name) {
            "Magic Missile" -> { 
                boss.hp -= 4 
            }
            "Drain" -> { 
                boss.hp -= 2
                wizard.hp += 2
            }
            "Shield", "Poison", "Recharge" -> {
                require(!activeEffects.containsKey(spell))
                activeEffects.put(spell, spell.effectDuration)
            }
        }
        wizard.armor = 0
        winner()?.let { return it }
        return null
    }
}

data class Spell(
    val name: String,
    val cost: Int,
    val damage: Int = 0,
    val heals: Int = 0,
    val effectDuration: Int = 0,
)
val spells = listOf(
    Spell(name = "Magic Missile", cost = 53, damage = 4),
    Spell(name = "Drain", cost = 73, damage = 2, heals = 2),
    Spell(name = "Shield", cost = 113, effectDuration = 6,),
    Spell(name = "Poison", cost = 173, effectDuration = 6),
    Spell(name = "Recharge", cost = 229, effectDuration = 5),
).associateBy { it.name }

data class Wizard(val name: String, var hp: Int, var mana: Int, var armor: Int = 0)
data class Player(val name: String, var hp: Int, var damage: Int, var armor: Int = 0)

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
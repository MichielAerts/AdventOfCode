package advent2018.day24

import lib.*
import java.io.File

const val day = 24
val file = File("src/main/resources/advent2018/day${day}/input")

class Puzzle(private val input: List<String>) {

    val targetSelectionOrder = compareBy<Group>({ it.effectivePower() }, { it.initiative }).reversed()
    val targetSelectionCriteria = compareBy<Pair<Group, Group>>(
        { it.first.damageTo(it.second) }, { it.second.effectivePower() }, { it.second.initiative }
    )
    val attackOrder = compareByDescending<Group> { it.initiative }

    fun runPart1() {
        val (teamImmuneSystem, teamInfection) = createTeams()
        fight(teamImmuneSystem, teamInfection)
    }

    fun runPart2() {
        val maxBoost = 150
        for (boost in 0..maxBoost) {
            val (teamImmuneSystem, teamInfection) = createTeams()
            teamImmuneSystem.boost(boost)
            val winner = fight(teamImmuneSystem, teamInfection)
            println("boost: $boost, winner: ${winner?.groups?.first()?.type}")
        }
    }
    
    private fun createTeams(): Pair<Team, Team> {
        return input.splitBy { it.isEmpty() }
            .mapToPair(
                transformLeft = {
                    Team(
                        groups = it.drop(1)
                            .mapIndexed { idx, line -> Group.createGroup(idx + 1, line, Type.IMMUNE_SYSTEM) }
                            .toMutableList()
                    )
                },
                transformRight = {
                    Team(
                        groups = it.drop(1).mapIndexed { idx, line -> Group.createGroup(idx + 1, line, Type.INFECTION) }
                            .toMutableList()
                    )
                }
            )
    }

    private fun fight(teamImmuneSystem: Team, teamInfection: Team): Team? {
        var round = 1
        var killsPerRound = Int.MAX_VALUE
        while (teamInfection.hasUnits() && teamImmuneSystem.hasUnits() && killsPerRound > 0) {
            //target selection
            val allGroups = (teamInfection.groups + teamImmuneSystem.groups).sortedWith(targetSelectionOrder)
            val selectedTargets = Type.entries.associateWith { mutableSetOf<Group>() }
            val fights = mutableMapOf<Group, Group>()
            for (group in allGroups) {
                if (group.units == 0) continue

                val enemy = group.type.enemy()
                val pair = allGroups.filter { it.type == enemy && it.units > 0 && it !in selectedTargets[enemy]!! }
                    .map { Pair(group, it) }.maxWithOrNull(targetSelectionCriteria)
                if (pair != null) {
                    val selectedEnemy = pair.second
                    val damage = group.damageTo(selectedEnemy)
                    if (damage > 0) {
                        fights[group] = selectedEnemy
                        selectedTargets[enemy]!!.add(selectedEnemy)
                    }
                }
            }
            //fight!
            killsPerRound = 0
            val fighters = allGroups.sortedWith(attackOrder)
            for (attacker in fighters) {
                if (attacker.units == 0) continue

                val defender = fights[attacker]
                if (defender != null) {
                    val kills = defender.attackedBy(attacker)
                    killsPerRound += kills
                }
            }
            round++
        }
        println("Fighting done, result: team immune system: ${teamImmuneSystem.totalUnits()}, infection: ${teamInfection.totalUnits()}")
        return when {
            teamInfection.isKilled() -> teamImmuneSystem
            teamImmuneSystem.isKilled() -> teamInfection
            else -> null
        }
    }
}

class Team(val groups: List<Group>) {
    fun hasUnits() = groups.any { it.units > 0 }
    fun isKilled() = !hasUnits()
    fun totalUnits() = groups.sumOf { it.units }
    fun boost(boost: Int) {
        groups.forEach { it.attack += boost }
    }
}

class Group(
    val no: Int,
    val type: Type,
    var units: Int,
    val hpPerUnit: Int,
    val weakness: List<Attack>,
    val immunity: List<Attack>,
    var attack: Int,
    val attackType: Attack,
    val initiative: Int
) {
    override fun toString(): String = "$type group $no (units: $units)"
    fun effectivePower() = units * attack
    fun damageTo(enemy: Group): Int {
        val multiplier = when (this.attackType) {
            in enemy.weakness -> 2
            in enemy.immunity -> 0
            else -> 1
        }
        return effectivePower() * multiplier
    }

    fun attackedBy(attacker: Group): Int {
        val damageReceived = attacker.damageTo(this)
        val potentialKills = damageReceived / hpPerUnit
        val actualKills = maxOf(units, potentialKills)
        units = maxOf(0, units - potentialKills)
        return actualKills
    }

    companion object {
        val regex = Regex("(\\d+) units each with (\\d+) hit points (.*)with an attack that does (\\d+) (\\w+) damage at initiative (\\d+)")
        fun createGroup(no: Int, input: String, type: Type): Group {
            val (units, hp, properties, attack, attackType, initiative) = regex.allGroups(input)
            val props = properties.split(";")
            val weakness = props.firstOrNull { it.contains("weak") }?.substringAfter("weak to ")?.substringBefore(")")
                ?.split(", ")?.map { Attack.valueOf(it.uppercase()) } ?: emptyList()
            val immunity = props.firstOrNull { it.contains("immune") }?.substringAfter("immune to ")?.substringBefore(")")
                ?.split(", ")?.map { Attack.valueOf(it.uppercase()) } ?: emptyList()
            return Group(
                no = no,
                type = type,
                units = units.toInt(),
                hpPerUnit = hp.toInt(),
                weakness = weakness,
                immunity = immunity,
                attack = attack.toInt(),
                attackType = Attack.valueOf(attackType.uppercase()),
                initiative = initiative.toInt()
            )
            //989 units each with 1274 hit points (immune to fire; weak to bludgeoning,
            // slashing) with an attack that does 25 slashing damage at initiative 3
        }
    }
} 
enum class Attack { RADIATION, BLUDGEONING, FIRE, SLASHING, COLD }
enum class Type { IMMUNE_SYSTEM, INFECTION;
    fun enemy(): Type = when(this) {
        IMMUNE_SYSTEM -> INFECTION
        INFECTION -> IMMUNE_SYSTEM
    }
}

fun main() {
    val input = file.readLines()
    val puzzle = Puzzle(input)
    runPuzzle(day, 1) { puzzle.runPart1() }
    runPuzzle(day, 2) { puzzle.runPart2() }
}
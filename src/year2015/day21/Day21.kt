package year2015.day21

import readInput
import kotlin.math.max

fun main() {
    val input = readInput("2015", "Day21")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val (bossInitialHP, bossDmg, bossArmor) = input.map { it.split(' ').last().toInt() }
    val combinations = getEquipmentCombinations()
    val cheapestWinningCombination = combinations.first { playerStats ->
        fightBoss(playerStats, bossArmor, bossDmg, bossInitialHP)
    }
    return cheapestWinningCombination.cost
}

private fun part2(input: List<String>): Int {
    val (bossInitialHP, bossDmg, bossArmor) = input.map { it.split(' ').last().toInt() }
    val combinations = getEquipmentCombinations()
    val mostExpensiveLosingCombination = combinations.last { playerStats ->
        !fightBoss(playerStats, bossArmor, bossDmg, bossInitialHP)
    }
    return mostExpensiveLosingCombination.cost
}

private fun fightBoss(
    playerStats: Stats,
    bossArmor: Int,
    bossDmg: Int,
    bossInitialHP: Int,
    playerInitialHP: Int = 100,
): Boolean {
    var bossHP = bossInitialHP
    var playerHP = playerInitialHP
    val playerDmgPerRound = max(playerStats.damage - bossArmor, 1)
    val bossDmgPerRound = max(bossDmg - playerStats.armor, 1)
    while (bossHP > 0 && playerHP > 0) {
        bossHP -= playerDmgPerRound
        if (bossHP > 0) {
            playerHP -= bossDmgPerRound
        }
    }
    return playerHP > 0
}

private data class Stats(
    val cost: Int,
    val damage: Int,
    val armor: Int,
) {
    operator fun plus(other: Stats) = Stats(
        cost = cost + other.cost,
        damage = damage + other.damage,
        armor = armor + other.armor,
    )
}

private fun getEquipmentCombinations(): List<Stats> {
    val weapons = listOf(
        Stats(8, 4, 0),
        Stats(10, 5, 0),
        Stats(25, 6, 0),
        Stats(40, 7, 0),
        Stats(74, 8, 0),
    )
    val armors = listOf(
        Stats(13, 0, 1),
        Stats(31, 0, 2),
        Stats(53, 0, 3),
        Stats(75, 0, 4),
        Stats(102, 0, 5),
    )
    val rings = listOf(
        Stats(25, 1, 0),
        Stats(50, 2, 0),
        Stats(100, 3, 0),
        Stats(20, 0, 1),
        Stats(40, 0, 2),
        Stats(80, 0, 3),
    )

    val combinations = HashSet(weapons)
    combinations += armors.flatMap { armor -> combinations.map { armor + it } }
    val ringCombos = rings.flatMap { ring -> rings.map { ring to it } }
        .filter { it.first != it.second }
        .map { it.first + it.second }
    combinations += rings.flatMap { ring -> combinations.map { ring + it } } +
            ringCombos.flatMap { ringCombo -> combinations.map { ringCombo + it } }
    return combinations.sortedBy { it.cost }
}
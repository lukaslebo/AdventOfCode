package day22

import readInput
import kotlin.math.max

fun main() {
    val input = readInput("2015", "Day22")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = winWithLeastManaSpent(toGame(input)) ?: error("Unable to win")

private fun part2(input: List<String>) = winWithLeastManaSpent(
    game = toGame(input),
    playerHpDrain = 1,
) ?: error("Unable to win")

private fun toGame(input: List<String>): Game {
    val (bossInitialHP, bossDmg) = input.map { it.split(' ').last().toInt() }
    return Game(
        bossHP = bossInitialHP,
        bossDmg = bossDmg,
    )
}

private fun winWithLeastManaSpent(
    game: Game,
    playerHpDrain: Int = 0,
    cache: MutableMap<Game, Int?> = hashMapOf()
): Int? {
    return cache.getOrPut(game) {
        var nextGame = game.tickEffects()
        if (nextGame.bossHP <= 0) {
            return@getOrPut nextGame.manaSpent
        }
        nextGame = nextGame.copy(playerHP = nextGame.playerHP - playerHpDrain)
        if (nextGame.playerHP <= 0) {
            return@getOrPut null
        }
        val availableSpells = Spell.values().filter {
            val canReapplyEffect = when (it) {
                Spell.Shield -> nextGame.effects.shieldTimer == 0
                Spell.Poison -> nextGame.effects.poisonTimer == 0
                Spell.Recharge -> nextGame.effects.rechargeTimer == 0
                else -> true
            }
            canReapplyEffect && it.cost <= nextGame.playerMana
        }
        val gameOnStartOfRound = nextGame
        return@getOrPut availableSpells.mapNotNull {
            nextGame = gameOnStartOfRound
            nextGame = nextGame.playerTurn(it)
            if (nextGame.bossHP <= 0) {
                return@mapNotNull nextGame.manaSpent
            }
            nextGame = nextGame.tickEffects()
            if (nextGame.bossHP <= 0) {
                return@mapNotNull nextGame.manaSpent
            }
            nextGame = nextGame.bossTurn()
            if (nextGame.playerHP <= 0) {
                return@mapNotNull null
            }
            winWithLeastManaSpent(nextGame, playerHpDrain, cache)
        }.minOrNull()
    }
}

private data class Game(
    val bossHP: Int,
    val bossDmg: Int,
    val playerHP: Int = 50,
    val playerMana: Int = 500,
    val manaSpent: Int = 0,
    val effects: Effects = Effects(),
) {
    fun tickEffects() = Game(
        bossHP = bossHP - if (effects.poisonTimer > 0) 3 else 0,
        bossDmg = bossDmg,
        playerHP = playerHP,
        playerMana = playerMana + if (effects.rechargeTimer > 0) 101 else 0,
        manaSpent = manaSpent,
        effects = effects.tick(),
    )

    fun bossTurn() = Game(
        bossHP = bossHP,
        bossDmg = bossDmg,
        playerHP = playerHP - (bossDmg - if (effects.shieldTimer > 0) 7 else 0),
        playerMana = playerMana,
        manaSpent = manaSpent,
        effects = effects,
    )

    fun playerTurn(spell: Spell) = Game(
        bossHP = bossHP - spell.dmg,
        bossDmg = bossDmg,
        playerHP = playerHP + spell.heal,
        playerMana = playerMana - spell.cost,
        manaSpent = manaSpent + spell.cost,
        effects = effects.cast(spell),
    )
}

private enum class Spell(
    val cost: Int,
    val dmg: Int,
    val heal: Int,
) {
    MagicMissile(53, 4, 0),
    Drain(73, 2, 2),
    Shield(113, 0, 0),
    Poison(173, 0, 0),
    Recharge(229, 0, 0),
}

private data class Effects(
    val poisonTimer: Int = 0,
    val shieldTimer: Int = 0,
    val rechargeTimer: Int = 0,
) {
    fun tick() = Effects(
        poisonTimer = max(poisonTimer - 1, 0),
        shieldTimer = max(shieldTimer - 1, 0),
        rechargeTimer = max(rechargeTimer - 1, 0),
    )

    fun cast(spell: Spell) = Effects(
        poisonTimer = if (spell == Spell.Poison) 6 else poisonTimer,
        shieldTimer = if (spell == Spell.Shield) 6 else shieldTimer,
        rechargeTimer = if (spell == Spell.Recharge) 5 else rechargeTimer,
    )
}

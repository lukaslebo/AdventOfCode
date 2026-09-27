package day15

import algorithms.Node
import algorithms.aStar
import algorithms.allBestPaths
import check
import readInput

fun main() {
    check(part1(readInput("2018", "Day15_test_1")), 27730)
    check(part1(readInput("2018", "Day15_test_2")), 36334)
    check(part1(readInput("2018", "Day15_test_3")), 39514)
    check(part1(readInput("2018", "Day15_test_4")), 27755)
    check(part1(readInput("2018", "Day15_test_5")), 28944)
    check(part1(readInput("2018", "Day15_test_6")), 18740)

    check(part2(readInput("2018", "Day15_test_1")), 4988)
    check(part2(readInput("2018", "Day15_test_2")), 29064)
    check(part2(readInput("2018", "Day15_test_3")), 31284)
    check(part2(readInput("2018", "Day15_test_4")), 3478)
    check(part2(readInput("2018", "Day15_test_5")), 6474)
    check(part2(readInput("2018", "Day15_test_6")), 1140)

    val input = readInput("2018", "Day15")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val gameState = input.parseGameState()
    while (!gameState.gameEnded()) {
        gameState.playRound()
//        gameState.printMap()
    }
    return gameState.outcome()
}

private fun part2(input: List<String>): Int {
    var gameState = input.parseGameState()
    var attackPower = 3
    while (!gameState.gameEnded()) {
        gameState.playRound()
        if (gameState.players.any { it.race == Race.Elf && !it.alive }) {
            gameState = input.parseGameState(elfAttackPower = ++attackPower)
        }
    }
    return gameState.outcome()
}

private data class Pos(val x: Int, val y: Int) {
    operator fun plus(other: Pos) = Pos(x + other.x, y + other.y)
    fun adjacent() = adjacent.map { it + this }

    companion object {
        val right = Pos(1, 0)
        val left = Pos(-1, 0)
        val up = Pos(0, -1)
        val down = Pos(0, 1)

        val adjacent = listOf(up, left, right, down)
    }
}

private enum class Race {
    Elf, Goblin
}

private data class Player(val race: Race, var pos: Pos, val attackPower: Int, var hp: Int = 200) {
    val alive: Boolean
        get() = hp > 0
}

private data class GameState(
    val walls: Set<Pos>,
    val players: List<Player>,
    var round: Int = 0,
) {
    fun gameEnded() = players.count { it.race == Race.Elf && it.alive } == 0 ||
        players.count { it.race == Race.Goblin && it.alive } == 0

    fun outcome() = round * players.filter { it.alive }.sumOf { it.hp }
}

private fun List<String>.parseGameState(elfAttackPower: Int = 3): GameState {
    val walls = mutableSetOf<Pos>()
    val players = mutableListOf<Player>()
    for ((y, line) in withIndex()) {
        for ((x, c) in line.withIndex()) {
            val pos = Pos(x, y)
            when (c) {
                '#' -> walls += pos
                'E' -> players += Player(race = Race.Elf, pos = pos, attackPower = elfAttackPower)
                'G' -> players += Player(race = Race.Goblin, pos = pos, attackPower = 3)
            }
        }
    }
    return GameState(
        walls = walls,
        players = players,
    )
}

private fun GameState.playRound() {
    val playersSorted = players.sortedWith(compareBy<Player> { it.pos.y }.thenBy { it.pos.x })
    for (player in playersSorted) {
        if (!player.alive) continue
        if (gameEnded()) return
        val enemies = playersSorted.filter { it.race != player.race && it.alive }
        val blocked = walls + playersSorted.filter { it != player && it.alive }.map { it.pos }
        val targets = enemies.flatMap { it.pos.adjacent() }.filter { it !in blocked }.toSet()
        val move = targets.mapNotNull { target ->
            aStar(
                from = player.pos,
                goal = { it == target },
                neighboursWithCost = { adjacent().filter { it !in blocked }.map { it to 1 } },
            )
        }.minWithOrNull(compareBy<Node<Pos>> { it.cost }.thenBy { it.value.y }.thenBy { it.value.x })
            ?: continue

        if (move.cost > 0) {
            val possiblePaths = allBestPaths(
                from = player.pos,
                goal = { it == move.value },
                neighboursWithCost = { adjacent().filter { it !in blocked }.map { it to 1 } },
            )
            player.pos = possiblePaths.map { it.path()[1] }.distinct().minWith(compareBy<Pos> { it.y }.thenBy { it.x })
        }

        val enemyToAttack = enemies.filter { player.pos in it.pos.adjacent() }
            .minWithOrNull(compareBy<Player> { it.hp }.thenBy { it.pos.y }.thenBy { it.pos.x })
        if (enemyToAttack != null) {
            enemyToAttack.hp -= player.attackPower
        }
    }
    round++
}

private fun GameState.printMap() {
    val playersOnLine = mutableListOf<Player>()
    val playersByPos = players.filter { it.alive }.associateBy { it.pos }
    println("Round $round")
    for (y in 0..walls.maxOf { it.y }) {
        for (x in 0..walls.maxOf { it.x }) {
            when (val pos = Pos(x, y)) {
                in walls -> print("#")
                in playersByPos -> {
                    val player = playersByPos.getValue(pos)
                    playersOnLine += player
                    print(if (player.race == Race.Elf) "E" else "G")
                }

                else -> print(".")
            }
        }
        print(playersOnLine.joinToString(prefix = " ") { "${it.race}(${it.hp})" }.trimEnd())
        println()
        playersOnLine.clear()
    }
    println()
}

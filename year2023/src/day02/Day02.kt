package day02

import check
import readInput

fun main() {
    val testInput = readInput("2023", "Day02_test")
    check(part1(testInput), 8)
    check(part2(testInput), 2286)

    val input = readInput("2023", "Day02")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.map { it.parseGame() }
    .filter { it.isValid() }
    .sumOf { it.id }

private fun Game.isValid() = combinations.all { it.red <= 12 && it.green <= 13 && it.blue <= 14 }

private fun part2(input: List<String>) = input.map { it.parseGame() }
    .sumOf { game ->
        val maxRed = game.combinations.maxOf { it.red }
        val maxGreen = game.combinations.maxOf { it.green }
        val maxBlue = game.combinations.maxOf { it.blue }
        maxRed * maxGreen * maxBlue
    }

private data class Game(
    val id: Int,
    val combinations: List<Combination>,
)

private data class Combination(
    val red: Int,
    val green: Int,
    val blue: Int,
)

private fun String.parseGame(): Game {
    val (gameString, rounds) = split(": ")
    val combinations = rounds.split("; ").map { round ->
        val cubesByColor = round.split(", ").associate { c ->
            val (n, color) = c.split(" ")
            color to n.toInt()
        }
        Combination(
            red = cubesByColor["red"] ?: 0,
            green = cubesByColor["green"] ?: 0,
            blue = cubesByColor["blue"] ?: 0,
        )
    }
    return Game(
        id = gameString.substringAfterLast(" ").toInt(),
        combinations = combinations,
    )
}

package day02

import check
import readInput

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day02_test")
    check(part1(testInput), 15)
    check(part2(testInput), 12)

    val input = readInput("2022", "Day02")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int = input.sumOf { it.score1 }

private fun part2(input: List<String>): Int = input.sumOf { it.score2 }

private val String.score1: Int
    get() = when (this) {
        "A X" -> 3 + 1
        "B X" -> 0 + 1
        "C X" -> 6 + 1
        "A Y" -> 6 + 2
        "B Y" -> 3 + 2
        "C Y" -> 0 + 2
        "A Z" -> 0 + 3
        "B Z" -> 6 + 3
        "C Z" -> 3 + 3
        else -> error("$this could not be mapped to a score")
    }

private val String.score2: Int
    get() = when (this) {
        "A X" -> 0 + 3
        "B X" -> 0 + 1
        "C X" -> 0 + 2
        "A Y" -> 3 + 1
        "B Y" -> 3 + 2
        "C Y" -> 3 + 3
        "A Z" -> 6 + 2
        "B Z" -> 6 + 3
        "C Z" -> 6 + 1
        else -> error("$this could not be mapped to a score")
    }

/** Alternative Solution:

private fun part1(input: List<String>): Int = input.sumOf {
    val (opponent, me) = it.split(' ').map(String::toShape)
    me.fight(opponent)
}

private fun part2(input: List<String>): Int = input.sumOf {
    val (opponentString, outcomeString) = it.split(' ')
    val opponent = opponentString.toShape()
    val outcome = outcomeString.toOutcome()
    val play = getPlayForOutcome(opponent, outcome)
    outcome.score + play.value
}

private fun getPlayForOutcome(opponent: Shape, outcome: Outcome): Shape = when (outcome) {
    Outcome.Draw -> opponent
    Outcome.Win -> when (opponent) {
        Shape.Rock -> Shape.Paper
        Shape.Paper -> Shape.Scissors
        Shape.Scissors -> Shape.Rock
    }

    Outcome.Lose -> when (opponent) {
        Shape.Rock -> Shape.Scissors
        Shape.Paper -> Shape.Rock
        Shape.Scissors -> Shape.Paper
    }
}

private enum class Shape(
    val value: Int,
) : Comparable<Shape> {
    Rock(1),
    Paper(2),
    Scissors(3);

    fun fight(opponent: Shape): Int = when {
        this == opponent -> Outcome.Draw
        (this == Rock && opponent == Scissors) || (this == Paper && opponent == Rock) || (this == Scissors && opponent == Paper) -> Outcome.Win
        else -> Outcome.Lose
    }.score + value
}

private enum class Outcome(val score: Int) {
    Win(6),
    Draw(3),
    Lose(0),
}

private fun String.toShape() = when (this) {
    "A", "X" -> Shape.Rock
    "B", "Y" -> Shape.Paper
    "C", "Z" -> Shape.Scissors
    else -> error("$this can not be mapped into a shape")
}

private fun String.toOutcome() = when (this) {
    "X" -> Outcome.Lose
    "Y" -> Outcome.Draw
    "Z" -> Outcome.Win
    else -> error("$this can not be mapped into a outcome")
}

**/

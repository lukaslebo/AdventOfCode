package year2023.day07

import check
import readInput
import kotlin.math.pow

fun main() {
    val testInput = readInput("2023", "Day07_test")
    check(part1(testInput), 6440)
    check(part2(testInput), 5905)

    val input = readInput("2023", "Day07")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    return input.map { it.toHand() }
        .sortedWith(compareBy<Hand> { it.type }.thenBy { it.strength })
        .mapIndexed { index, hand -> (index + 1) * hand.bid }
        .sum()
}

private fun part2(input: List<String>): Int {
    return input.map { it.toHand(withJokers = true) }
        .sortedWith(compareBy<Hand> { it.type }.thenBy { it.strength })
        .mapIndexed { index, hand -> (index + 1) * hand.bid }
        .sum()
}

private data class Hand(
    val cards: String,
    val bid: Int,
    val type: Int,
    val strength: Int,
)

private fun String.toHand(withJokers: Boolean = false): Hand {
    val (cards, bid) = split(" ")
    return Hand(cards, bid.toInt(), cards.getType(withJokers), cards.getStrength(withJokers))
}

private fun String.getType(withJokers: Boolean = false): Int {
    val jokers = count { it == 'J' && withJokers }
    val groups = groupBy { it }.filter { it.key != 'J' || !withJokers }.map { it.value.size }
    val maxGroup = groups.maxOrNull() ?: 0
    val pairs = groups.count { it == 2 }
    return when {
        maxGroup + jokers == 5 -> 7
        maxGroup + jokers == 4 -> 6
        (maxGroup == 3 && pairs == 1) || (pairs == 2 && jokers == 1) -> 5
        maxGroup + jokers == 3 -> 4
        pairs == 2 -> 3
        maxGroup + jokers == 2 -> 2
        else -> 1
    }
}

private fun String.getStrength(withJokers: Boolean): Int {
    return reversed().mapIndexed { i, c ->
        val points = if (withJokers) cardsWithJokers.indexOf(c) else cards.indexOf(c)
        (points + 1) * 13.0.pow(i).toInt()
    }.sum()
}

private const val cardsWithJokers = "J23456789TQKA"
private const val cards = "23456789TJQKA"

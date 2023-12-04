package year2023.day04

import check
import readInput

fun main() {
    val testInput = readInput("2023", "Day04_test")
    check(part1(testInput), 13)
    check(part2(testInput), 30)

    val input = readInput("2023", "Day04")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.sumOf { line ->
    val wins = line.parseCard().winCount
    if (wins == 0) 0 else 1 shl (wins - 1)
}

private fun part2(input: List<String>): Int {
    val cards = input.map { it.parseCard() }
    val copiesByCard = cards.associateWith { 1 }.toMutableMap()
    cards.forEachIndexed { i, card ->
        val copies = copiesByCard.getValue(card)
        val nextCardRange = (i + 1) until (i + 1 + card.winCount)
        for (j in nextCardRange) {
            val cardToIncrease = cards.getOrNull(j) ?: break
            copiesByCard[cardToIncrease] = copiesByCard.getValue(cardToIncrease) + copies
        }
    }
    return copiesByCard.values.sum()
}

private data class Card(
    val id: Int,
    val winningNumbers: Set<Int>,
    val elfNumbers: List<Int>,
    val winCount: Int,
)

private fun String.parseCard(): Card {
    val winningNumbers = substringAfter(": ")
        .substringBefore(" | ")
        .split(" ")
        .filter { it.isNotBlank() }
        .map { it.toInt() }
        .toSet()
    val elfNumbers = substringAfter(" | ")
        .split(" ")
        .filter { it.isNotBlank() }
        .map { it.toInt() }
    return Card(
        id = substringBefore(":").split(" ").last().toInt(),
        winningNumbers = winningNumbers,
        elfNumbers = elfNumbers,
        winCount = elfNumbers.count { it in winningNumbers },
    )
}

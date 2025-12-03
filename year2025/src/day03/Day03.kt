package day03

import check
import readInput

fun main() {
    val testInput = readInput("2025", "Day03_test")
    check(part1(testInput), 357)
    check(part2(testInput), 3121910778619)

    val input = readInput("2025", "Day03")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.parseBatteryBanks().sumOf { it.maxJoltage(batteries = 2) }
private fun part2(input: List<String>) = input.parseBatteryBanks().sumOf { it.maxJoltage(batteries = 12) }

private fun List<Int>.maxJoltage(batteries: Int): Long {
    var bank = this
    var remaining = batteries
    var joltage = 0L
    while (remaining > 0) {
        remaining--
        val max = bank.dropLast(remaining).max()
        bank = bank.drop(bank.indexOf(max) + 1)
        joltage = joltage * 10 + max
    }
    return joltage
}

private fun List<String>.parseBatteryBanks() = map { bank ->
    bank.map { it.digitToInt() }
}

package year2024.day11

import check
import readInput

fun main() {
    val testInput = readInput("2024", "Day11_test")
    check(part1(testInput), 55312)

    val input = readInput("2024", "Day11")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.first().parseStones().sumOf { replicationsAfterBlinks(it, 25) }

private fun part2(input: List<String>) = input.first().parseStones().sumOf { replicationsAfterBlinks(it, 75) }

private fun String.parseStones() = split(" ").map { it.toLong() }

private fun Long.blink(): List<Long> = when {
    this == 0L -> listOf(1L)
    toString().length % 2 == 0 -> {
        val numText = toString()
        val half = numText.length / 2
        listOf(numText.take(half).toLong(), numText.takeLast(half).toLong())
    }

    else -> listOf(this * 2024L)
}

private fun replicationsAfterBlinks(
    stone: Long,
    blinks: Int,
    cache: MutableMap<Pair<Long, Int>, Long> = mutableMapOf(),
): Long {
    if (blinks == 0) return 1
    return cache.getOrPut(stone to blinks) {
        stone.blink().sumOf {
            replicationsAfterBlinks(it, blinks - 1, cache)
        }
    }
}

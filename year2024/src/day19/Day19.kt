package day19

import check
import readInput

fun main() {
    val testInput = readInput("2024", "Day19_test")
    check(part1(testInput), 6)
    check(part2(testInput), 16)

    val input = readInput("2024", "Day19")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val (towels, patterns) = input.parseTowelsAndPatterns()
    return patterns.count { pattern -> towels.countWaysToMake(pattern) > 0 }
}

private fun part2(input: List<String>): Long {
    val (towels, patterns) = input.parseTowelsAndPatterns()
    return patterns.sumOf { pattern -> towels.countWaysToMake(pattern) }
}

private fun List<String>.parseTowelsAndPatterns(): Pair<List<String>, List<String>> {
    val towels = first().split(", ")
    val patterns = drop(2)
    return towels to patterns
}

private fun List<String>.countWaysToMake(
    pattern: String,
    cache: MutableMap<String, Long> = mutableMapOf(),
): Long {
    if (pattern.isEmpty()) return 1
    return cache.getOrPut(pattern) {
        sumOf { towel ->
            val next = pattern.removePrefix(towel)
            if (next != pattern) countWaysToMake(next, cache) else 0
        }
    }
}

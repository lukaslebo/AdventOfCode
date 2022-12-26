package year2015.day05

import check
import readInput

fun main() {
    val testInput = readInput("2015", "Day05_test")
    check(part1(testInput), 1)
    check(part2(testInput), 1)

    val input = readInput("2015", "Day05")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.count { isNice(it) }

private fun part2(input: List<String>) = input.count { isNicePartTwo(it) }

private val naughtyStrings = setOf("ab", "cd", "pq", "xy")
private val vowels = setOf('a', 'e', 'i', 'o', 'u')

private fun isNice(string: String): Boolean {
    val vowelCount = string.count { it in vowels }
    if (vowelCount < 3) return false
    val hasNaughtyStrings = naughtyStrings.any { it in string }
    if (hasNaughtyStrings) return false
    return string.windowed(2, 1).any { it[0] == it[1] }
}

private fun isNicePartTwo(string: String): Boolean {
    val cond1 = string.windowed(2, 1).any { string.indexOf(it) + 2 <= string.lastIndexOf(it) }
    val cond2 = string.windowed(3, 1).any { it[0] == it[2] }
    return cond1 && cond2
}

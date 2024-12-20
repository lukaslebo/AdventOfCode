package day02

import check
import readInput
import kotlin.math.abs

fun main() {
    val testInput = readInput("2024", "Day02_test")
    check(part1(testInput), 2)
    check(part2(testInput), 4)

    val input = readInput("2024", "Day02")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.map { it.parseReport() }.count { it.isSafe() }

private fun part2(input: List<String>) = input.map { it.parseReport() }.count { it.isSafe(true) }

private data class Report(val levels: List<Int>)

private fun String.parseReport() = Report(split(" ").map { it.toInt() })

private fun Report.isSafe(enableProblemDampener: Boolean = false): Boolean {
    if (levels.isStrictlyMonotonicWithinRateLimit()) return true
    if (!enableProblemDampener) return false
    return levels.indices.any { indexToRemove ->
        levels.filterIndexed { index, _ -> index != indexToRemove }.isStrictlyMonotonicWithinRateLimit()
    }
}

private fun List<Int>.isStrictlyMonotonicWithinRateLimit(): Boolean {
    val pairs = windowed(2)
    val isStrictlyMonotonic = pairs.all { (a, b) -> a < b } || pairs.all { (a, b) -> a > b }
    val isWithinRateLimit = pairs.all { (a, b) -> abs(a - b) <= 3 }
    return isStrictlyMonotonic && isWithinRateLimit
}

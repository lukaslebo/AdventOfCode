package day04

import check
import readInput

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day04_test")
    check(part1(testInput), 2)
    check(part2(testInput), 4)

    val input = readInput("2022", "Day04")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int = input.toRangePairs().count {
    it.first fullyContains it.second || it.second fullyContains it.first
}

private fun part2(input: List<String>): Int = input.toRangePairs().count { it.first overlaps it.second }

private fun List<String>.toRangePairs() = map { line ->
    val (part1, part2) = line.split(',').map { part ->
        val (start, end) = part.split('-').map { it.toInt() }
        start..end
    }
    part1 to part2
}

private infix fun IntRange.fullyContains(other: IntRange) = other.first in this && other.last in this
private infix fun IntRange.overlaps(other: IntRange) =
    first in other || last in other || other.first in this || other.last in this

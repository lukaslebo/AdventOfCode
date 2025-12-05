package day05

import check
import readInput
import splitByEmptyLines
import util.size
import util.withoutOverlaps

fun main() {
    val testInput = readInput("2025", "Day05_test")
    check(part1(testInput), 3)
    check(part2(testInput), 14)

    val input = readInput("2025", "Day05")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val (freshRanges, available) = input.parseIngredients()
    return available.filter { freshRanges.any { range -> it in range } }.size
}

private fun part2(input: List<String>): Long {
    val freshRanges = input.parseIngredients().first
    return freshRanges.withoutOverlaps().sumOf { it.size() }
}

private fun List<String>.parseIngredients(): Pair<List<LongRange>, List<Long>> {
    val (part1, part2) = splitByEmptyLines()
    val freshRanges = part1.map { line ->
        line.split("-").let { it.first().toLong()..it.last().toLong() }
    }
    val available = part2.map { it.toLong() }
    return freshRanges to available
}

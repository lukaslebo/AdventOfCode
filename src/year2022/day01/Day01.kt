package year2022.day01

import check
import readInput

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day01_test")
    check(part1(testInput), 24_000)
    check(part2(testInput), 45_000)

    val input = readInput("2022", "Day01")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int = input.caloriesGroupedByElf.maxOf { it.sum() }

private fun part2(input: List<String>): Int = input.caloriesGroupedByElf
    .map { it.sum() }
    .sorted()
    .takeLast(3)
    .sum()

private val List<String>.caloriesGroupedByElf: List<List<Int>>
    get() = buildList<MutableList<Int>> {
        add(mutableListOf())
        for (line in this@caloriesGroupedByElf) {
            if (line.isBlank()) {
                add(mutableListOf())
            } else {
                last() += line.toInt()
            }
        }
    }

package day13

import check
import readInput
import util.splitByEmptyLines

fun main() {
    val testInput = readInput("2023", "Day13_test")
    check(part1(testInput), 405)
    check(part2(testInput), 400)

    val input = readInput("2023", "Day13")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.solution(smudges = 0)
private fun part2(input: List<String>) = input.solution(smudges = 1)

private fun List<String>.solution(smudges: Int) = splitByEmptyLines().sumOf { pattern ->
    val col = pattern.reflectionIndex(smudges)
    val row = pattern.transpose().reflectionIndex(smudges)
    col + row * 100
}

private fun List<String>.transpose(): List<String> {
    val maxLength = maxOfOrNull { it.length } ?: return emptyList()
    val transposed = MutableList(maxLength) { "" }
    for (line in this) {
        for ((y, c) in line.withIndex()) {
            transposed[y] = transposed[y] + c
        }
    }
    return transposed
}

private fun List<String>.reflectionIndex(smudges: Int = 0): Int {
    val occurrencesByReflectionIndex = flatMap { it.reflectionIndices() }.groupBy { it }.mapValues { it.value.size }
    return occurrencesByReflectionIndex.entries.find { it.value == size - smudges }?.key ?: 0
}

private fun String.reflectionIndices() = (1..lastIndex).mapNotNull {
    val first = substring(0, it).reversed()
    val second = substring(it)
    if (first.startsWith(second) || second.startsWith(first)) it else null
}

package day04

import check
import readInput

fun main() {
    val testInput = readInput("2024", "Day04_test")
    check(part1(testInput), 18)
    check(part2(testInput), 9)

    val input = readInput("2024", "Day04")
    println(part1(input))
    println(part2(input))
    check(part2(input) != 1931)
}

private fun part1(input: List<String>) = input.countXmas()

private fun part2(input: List<String>) = input.countCrossingMas()

private enum class Direction(
    val dx: Int,
    val dy: Int,
) {
    North(0, 1),
    NorthEast(1, 1),
    East(1, 0),
    SouthEast(1, -1),
    South(0, -1),
    SouthWest(-1, -1),
    West(-1, 0),
    NorthWest(-1, 1),
}

private fun List<String>.countXmas(): Int {
    var count = 0
    for ((y, line) in withIndex()) {
        for ((x, c) in line.withIndex()) {
            if (c == 'X') {
                for (dir in Direction.entries) {
                    if (isXmasInDirection(y, x, dir)) count++
                }
            }
        }
    }
    return count
}

private fun List<String>.isXmasInDirection(line: Int, index: Int, direction: Direction): Boolean {
    val wordInDirection = listOfNotNull(
        getOrNull(line)?.getOrNull(index),
        getOrNull(line + direction.dy * 1)?.getOrNull(index + direction.dx * 1),
        getOrNull(line + direction.dy * 2)?.getOrNull(index + direction.dx * 2),
        getOrNull(line + direction.dy * 3)?.getOrNull(index + direction.dx * 3),
    ).joinToString("")
    return wordInDirection == "XMAS"
}

private fun List<String>.countCrossingMas(): Int {
    var count = 0
    for ((y, line) in withIndex()) {
        for ((x, c) in line.withIndex()) {
            if (c == 'A') {
                if (isCenterOfTwoCrossingMas(y, x)) count++
            }
        }
    }
    return count
}

private fun List<String>.isCenterOfTwoCrossingMas(line: Int, index: Int): Boolean {
    val word1 = String(
        charArrayOf(
            (getOrNull(line + -1)?.getOrNull(index + -1) ?: ' '),
            (getOrNull(line + 0)?.getOrNull(index + 0) ?: ' '),
            (getOrNull(line + 1)?.getOrNull(index + 1) ?: ' '),
        )
    )
    val word2 = String(
        charArrayOf(
            (getOrNull(line + 1)?.getOrNull(index + -1) ?: ' '),
            (getOrNull(line + 0)?.getOrNull(index + 0) ?: ' '),
            (getOrNull(line + -1)?.getOrNull(index + 1) ?: ' '),
        )
    )
    return (word1 == "MAS" || word1 == "SAM") &&
            (word2 == "MAS" || word2 == "SAM")
}

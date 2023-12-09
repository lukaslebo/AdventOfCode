package year2023.day09

import check
import readInput

fun main() {
    val testInput = readInput("2023", "Day09_test")
    check(part1(testInput), 114)
    check(part2(testInput), 2)

    val input = readInput("2023", "Day09")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.mapToHistories().sumOf { it.predictNextValue() }
private fun part2(input: List<String>) = input.mapToHistories().sumOf { it.predictPrevValue() }

private fun List<Int>.predictNextValue(): Int {
    if (all { it == 0 }) return 0
    return last() + getChildSequence().predictNextValue()
}

private fun List<Int>.predictPrevValue(): Int {
    if (all { it == 0 }) return 0
    return first() - getChildSequence().predictPrevValue()
}

private fun List<Int>.getChildSequence() = windowed(2).map { (a, b) -> b - a }

private fun List<String>.mapToHistories() = map { line -> line.split(' ').map { it.toInt() } }

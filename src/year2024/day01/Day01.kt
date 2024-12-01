package year2024.day01

import check
import readInput
import kotlin.math.absoluteValue

fun main() {
    val testInput = readInput("2024", "Day01_test")
    check(part1(testInput), 11)
    check(part2(testInput), 31)

    val input = readInput("2024", "Day01")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val (leftNumbers, rightNumbers) = input.getLeftAndRightNumbers()
    return leftNumbers.sorted().zip(rightNumbers.sorted()).sumOf { (leftNumber, rightNumber) ->
        (leftNumber - rightNumber).absoluteValue
    }
}

private fun part2(input: List<String>): Int {
    val (leftNumbers, rightNumbers) = input.getLeftAndRightNumbers()
    return leftNumbers.sumOf { leftNumber ->
        val occurrences = rightNumbers.count { leftNumber == it }
        leftNumber * occurrences
    }
}

private fun List<String>.getLeftAndRightNumbers(): Pair<List<Int>, List<Int>> {
    val leftNumbers = map { line -> line.takeWhile { it != ' ' }.toInt() }
    val rightNumbers = map { line -> line.takeLastWhile { it != ' ' }.toInt() }
    return leftNumbers to rightNumbers
}

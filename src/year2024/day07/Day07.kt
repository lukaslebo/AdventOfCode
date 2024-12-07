package year2024.day07

import check
import readInput

fun main() {
    val testInput = readInput("2024", "Day07_test")
    check(part1(testInput), 3749)
    check(part2(testInput), 11387)

    val input = readInput("2024", "Day07")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.map { it.parseEquation() }
    .filter {
        isAchievableWithOperations(
            result = it.numbers.first(),
            remainingNumbers = it.numbers.drop(1),
            test = it.test,
            supportedOperations = setOf(Operation.Plus, Operation.Times),
        )
    }
    .sumOf { it.test }

private fun part2(input: List<String>) = input.map { it.parseEquation() }
    .filter {
        isAchievableWithOperations(
            result = it.numbers.first(),
            remainingNumbers = it.numbers.drop(1),
            test = it.test,
            supportedOperations = setOf(Operation.Plus, Operation.Times, Operation.Concat),
        )
    }
    .sumOf { it.test }

private data class Equation(
    val test: Long,
    val numbers: List<Long>,
)

private fun String.parseEquation(): Equation {
    val numbers = split(":? ".toRegex()).map { it.toLong() }
    return Equation(numbers.first(), numbers.drop(1))
}

private enum class Operation {
    Plus, Times, Concat;

    fun perform(a: Long, b: Long): Long = when (this) {
        Plus -> a + b
        Times -> a * b
        Concat -> "$a$b".toLong()
    }
}

private fun isAchievableWithOperations(
    result: Long,
    remainingNumbers: List<Long>,
    test: Long,
    supportedOperations: Set<Operation>,
): Boolean {
    if (remainingNumbers.isEmpty()) return result == test
    if (result > test) return false

    val next = remainingNumbers.first()
    val remaining = remainingNumbers.drop(1)
    for (operation in supportedOperations) {
        val isAchievable = isAchievableWithOperations(
            result = operation.perform(result, next),
            remainingNumbers = remaining,
            test = test,
            supportedOperations = supportedOperations,
        )
        if (isAchievable) return true
    }
    return false
}

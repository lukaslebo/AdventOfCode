package day01

import check
import readInput

fun main() {
    val testInput1 = readInput("2023", "Day01_test_part1")
    val testInput2 = readInput("2023", "Day01_test_part2")
    check(part1(testInput1), 142)
    check(part2(testInput2), 281)

    val input = readInput("2023", "Day01")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.sumOf { it.parseCalibrationValue() }

private fun part2(input: List<String>) = input.sumOf { it.parseCalibrationValue(includeSpelledOut = true) }

private fun String.parseCalibrationValue(includeSpelledOut: Boolean = false): Int {
    val nums = indices.mapNotNull { substring(it).getStartingDigitAsNumOrNull(includeSpelledOut) }
    return nums.first() * 10 + nums.last()
}

private fun String.getStartingDigitAsNumOrNull(includeSpelledOut: Boolean = false): Int? {
    val num = first().digitToIntOrNull()
    if (num != null || !includeSpelledOut) return num
    return numsByText.firstNotNullOfOrNull { (numAsWord, num) ->
        num.takeIf { startsWith(numAsWord) }
    }
}

private val numsByText = mapOf(
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9,
)

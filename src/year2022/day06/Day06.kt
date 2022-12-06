package year2022.day06

import check
import readInput

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day06_test")
    check(part1(testInput), 7)
    check(part2(testInput), 19)

    val input = readInput("2022", "Day06")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int = countCharactersAfterFirstSetOfUniqueChars(input.first(), 4)

private fun part2(input: List<String>): Int = countCharactersAfterFirstSetOfUniqueChars(input.first(), 14)

private fun countCharactersAfterFirstSetOfUniqueChars(input: String, numOfUniqueChars: Int): Int =
    numOfUniqueChars + input.windowed(numOfUniqueChars, 1).indexOfFirst { it.toSet().size == numOfUniqueChars }
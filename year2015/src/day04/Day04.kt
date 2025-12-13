package day04

import check
import readInput
import util.md5

fun main() {
    val testInput = readInput("2015", "Day04_test")
    check(part1(testInput), 1048970)
    check(part2(testInput), 5714438)

    val input = readInput("2015", "Day04")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = findSuffixForNextBlock(input, 5)

private fun part2(input: List<String>) = findSuffixForNextBlock(input, 6)

private fun findSuffixForNextBlock(input: List<String>, leadingZeroes: Int): Int {
    val secret = input.first()
    val prefix = "0".repeat(leadingZeroes)
    for (num in 1..Int.MAX_VALUE) {
        if ((secret + num).md5().startsWith(prefix)) return num
    }
    error("No number found for md5 with 5 leading zeroes")
}

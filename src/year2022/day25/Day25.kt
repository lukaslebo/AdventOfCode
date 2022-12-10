package year2022.day25

import check
import readInput

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day25_test")
    check(part1(testInput), 0)
    check(part2(testInput), 0)

    val input = readInput("2022", "Day25")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    return input.size
}

private fun part2(input: List<String>): Int {
    return input.size
}

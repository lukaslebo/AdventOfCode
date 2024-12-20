package day01

import check
import readInput

fun main() {
    val testInput = readInput("2015", "Day01_test")
    check(part1(testInput), 3)

    val input = readInput("2015", "Day01")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.first().let { line ->
    line.count { it == '(' } - line.count { it == ')' }
}

private fun part2(input: List<String>): Int {
    var floor = 0
    var position = 0
    for (c in input.first()) {
        position++
        when (c) {
            '(' -> floor++
            ')' -> floor--
        }
        if (floor == -1) return position
    }
    error("never entering basement")
}

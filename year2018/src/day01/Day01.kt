package day01

import readInput

fun main() {
    val input = readInput("2018", "Day01")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    return input.sumOf { it.toInt() }
}

private fun part2(input: List<String>): Int {
    val calibrations = input.map { it.toInt() }
    val seen = mutableSetOf<Int>()
    var i = 0
    var frequency = 0
    while (frequency !in seen) {
        seen += frequency
        frequency += calibrations[i++ % calibrations.size]
    }
    return frequency
}

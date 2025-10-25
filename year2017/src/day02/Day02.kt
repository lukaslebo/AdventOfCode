package day02

import check
import readInput
import kotlin.math.max
import kotlin.math.min

fun main() {
    val testInput = readInput("2017", "Day02_test")
    check(part1(testInput), 18)

    val input = readInput("2017", "Day02")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.parse().sumOf { it.max() - it.min() }

private fun part2(input: List<String>): Int {
    var result = 0
    for (row in input.parse()) {
        for ((i, a) in row.withIndex()) {
            for ((j, b) in row.withIndex()) {
                if (j <= i) continue
                val max = max(a, b)
                val min = min(a, b)
                if (max % min == 0) {
                    result += max / min
                }
            }
        }
    }
    return result
}

private fun List<String>.parse(): List<List<Int>> = map { line -> line.split("\\s".toRegex()).map { it.toInt() } }

package day02

import check
import readInput

fun main() {
    val testInput = readInput("2015", "Day02_test")
    check(part1(testInput), 58 + 43)
    check(part2(testInput), 34 + 14)

    val input = readInput("2015", "Day02")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.sumOf { line ->
    val (l, w, h) = line.split('x').map { it.toInt() }
    val faces = listOf(l * w, l * h, w * h)
    2 * faces.sum() + faces.min()
}

private fun part2(input: List<String>) = input.sumOf { line ->
    val sides = line.split('x').map { it.toInt() }.sorted()
    2 * sides.take(2).sum() + sides.reduce(Int::times)
}

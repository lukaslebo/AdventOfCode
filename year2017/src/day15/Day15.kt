package day15

import check
import readInput

fun main() {
    val testInput = readInput("2017", "Day15_test")
    check(part1(testInput), 588)
    check(part2(testInput), 309)

    val input = readInput("2017", "Day15")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val (startA, startB) = input.map { it.substringAfterLast(" ").toInt() }
    val genA = Generator(startingValue = startA, factor = 16807)
    val genB = Generator(startingValue = startB, factor = 48271)
    return findMatches(genA, genB, 40_000_000)
}

private fun part2(input: List<String>): Int {
    val (startA, startB) = input.map { it.substringAfterLast(" ").toInt() }
    val genA = Generator(startingValue = startA, factor = 16807, multipleCondition = 4)
    val genB = Generator(startingValue = startB, factor = 48271, multipleCondition = 8)
    return findMatches(genA, genB, 5_000_000)
}

private fun findMatches(genA: Generator, genB: Generator, count: Int): Int {
    val mask = 0xFFFF
    var matches = 0
    repeat(count) {
        val numA = genA.next()
        val numB = genB.next()
        if ((numA and mask) == (numB and mask)) {
            matches++
        }
    }
    return matches
}

private class Generator(startingValue: Int, val factor: Int, val multipleCondition: Int = 1) {
    var previousValue = startingValue

    fun next(): Int {
        do {
            previousValue = ((previousValue.toLong() * factor) % 2147483647).toInt()
        } while (previousValue % multipleCondition != 0)
        return previousValue
    }
}

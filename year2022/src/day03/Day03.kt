package day03

import check
import readInput

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day03_test")
    check(part1(testInput), 157)
    check(part2(testInput), 70)

    val input = readInput("2022", "Day03")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int = input.sumOf {
    it.chunked(it.length / 2).firstCommonChar.priority
}

private fun part2(input: List<String>): Int = input.windowed(3, 3).sumOf {
    it.firstCommonChar.priority
}

private val List<String>.firstCommonChar: Char
    get() = map { it.bitmask }.reduce { acc, mask -> acc and mask }.firstActiveChar

private val String.bitmask: Long
    get() = fold(0) { acc, c -> acc or c.mask }

private val Long.firstActiveChar: Char
    get() {
        for (i in 0..51) {
            if (this and (1L shl i) != 0L) {
                return if (i < 26) 'a' + i
                else 'A' + (i - 26)
            }
        }
        error("no active char")
    }

private val Char.mask: Long
    get() = 1L shl if (isLowerCase()) minus('a') else minus('A') + 26

private val Char.priority: Int
    get() = if (isLowerCase()) minus('a') + 1 else minus('A') + 27

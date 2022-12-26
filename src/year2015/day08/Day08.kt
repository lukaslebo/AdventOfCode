package year2015.day08

import check
import readInput

fun main() {
    val testInput = readInput("2015", "Day08_test")
    check(part1(testInput), 12)
    check(part2(testInput), 19)

    val input = readInput("2015", "Day08")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.sumOf { string ->
    val codeChars = string.length
    val actualChars = string.replace("\\\\", "1").replace("\\\\x[\\da-f]{2}".toRegex(), "1")
        .replace("\\\"", "1").length - 2
    codeChars - actualChars
}

private fun part2(input: List<String>) = input.sumOf { string ->
    val unescapedLength = string.length
    val escapedLength = string.replace("\"", "12").replace("\\", "12").length + 2
    escapedLength - unescapedLength
}
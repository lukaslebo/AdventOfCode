package day09

import check
import readInput

fun main() {
    val testInput = readInput("2017", "Day09_test")
    check(part1(testInput), 3)

    val input = readInput("2017", "Day09")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.first().calculateStreamGroupScore()
private fun part2(input: List<String>) = input.first().countGarbage()

private fun String.calculateStreamGroupScore(): Int {
    var groupNestingLevel = 0
    var score = 0
    processStream(
        onGroupStart = { groupNestingLevel++ },
        onGroupEnd = { score += groupNestingLevel-- }
    )
    return score
}

private fun String.countGarbage(): Int {
    var garbage = 0
    processStream(onGarbageChar = { garbage++ })
    return garbage
}

private fun String.processStream(
    onGarbageChar: (Char) -> Unit = {},
    onGroupStart: () -> Unit = {},
    onGroupEnd: () -> Unit = {},
) {
    var escaped = false
    var garbageStarted = false

    for (c in this) {
        if (escaped) {
            escaped = false
            continue
        }

        when (c) {
            '<' if !garbageStarted -> garbageStarted = true
            '>' if garbageStarted -> garbageStarted = false
            '!' if garbageStarted -> escaped = true
            '{' if !garbageStarted -> onGroupStart()
            '}' if !garbageStarted -> onGroupEnd()
            else -> if (garbageStarted) onGarbageChar(c)
        }
    }
}

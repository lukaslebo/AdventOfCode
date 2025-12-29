package day05

import check
import readInput

fun main() {
    val testInput = readInput("2018", "Day05_test")
    check(part1(testInput), 10)
    check(part2(testInput), 4)

    val input = readInput("2018", "Day05")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    return input.first().polymerReaction().length
}

private fun part2(input: List<String>): Int {
    val polymer = input.first()
    return ('a'.code..'z'.code).minOf {
        polymer.replace(it.toChar().toString(), "", ignoreCase = true).polymerReaction().length
    }
}

private fun String.polymerReaction(): String {
    var polymer = this
    while (true) {
        val nextPolymer = StringBuilder()

        var index = 0
        while (index in polymer.indices) {
            val current = polymer[index]
            if (index == polymer.lastIndex) {
                nextPolymer.append(current)
                break
            }

            val next = polymer[index + 1]
            if (current != next && current.equals(next, ignoreCase = true)) {
                index += 1
            } else {
                nextPolymer.append(current)
            }

            index++
        }

        if (nextPolymer.length == polymer.length) return polymer
        polymer = nextPolymer.toString()
    }
}

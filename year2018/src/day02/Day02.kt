package day02

import check
import readInput

fun main() {
    val testInput = readInput("2018", "Day02_test")
    check(part1(testInput), 12)

    val input = readInput("2018", "Day02")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val ids = input.map { it.toId() }
    return ids.count { it.hasPair } * ids.count { it.hasTripple }
}

private fun part2(input: List<String>): String {
    for (i in input.indices) {
        for (j in i + 1..input.lastIndex) {
            val result = getCommonLetters(input[i], input[j])
            if (result.length == input[i].length - 1) {
                return result
            }
        }
    }
    error("no result found")
}

private data class Id(
    val id: String,
    val hasPair: Boolean,
    val hasTripple: Boolean,
)

private fun String.toId(): Id {
    val groups = groupBy { it }
    return Id(
        id = this,
        hasPair = groups.values.any { it.size == 2 },
        hasTripple = groups.values.any { it.size == 3 },
    )
}

private fun getCommonLetters(a: String, b: String): String {
    return buildString {
        for (i in a.indices) {
            if (a[i] == b[i]) {
                append(a[i])
            }
        }
    }
}

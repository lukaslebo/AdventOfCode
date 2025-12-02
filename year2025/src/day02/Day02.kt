package day02

import check
import readInput

fun main() {
    val testInput = readInput("2025", "Day02_test")
    check(part1(testInput), 1227775554)
    check(part2(testInput), 4174379265)

    val input = readInput("2025", "Day02")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.first().parseRanges()
    .filterOverAll { it.firstHalfEqualsSecondHalf() }
    .sum()

private fun part2(input: List<String>) = input.first().parseRanges()
    .filterOverAll { it.consistsOfRepeatingSequence() }
    .sum()

private fun String.parseRanges() = split(",").map { rangeText ->
    val parts = rangeText.split("-")
    parts.first().toLong()..parts.last().toLong()
}

private fun List<LongRange>.filterOverAll(predicate: (Long) -> Boolean) =
    flatMap { range -> range.filter { predicate(it) } }

private fun Long.firstHalfEqualsSecondHalf(): Boolean {
    val id = toString()
    if (id.length % 2 != 0) return false
    val firstHalf = id.take(id.length / 2)
    val lastHalf = id.takeLast(id.length / 2)
    return firstHalf == lastHalf
}

private fun Long.consistsOfRepeatingSequence(): Boolean {
    val id = toString()
    val n = id.length
    for (sequenceLength in (1..n / 2)) {
        if (n % sequenceLength != 0) continue
        val sequence = id.take(sequenceLength)
        val times = n / sequenceLength
        if (sequence.repeat(times) == id) return true
    }
    return false
}

/**
 * Better performing implementation, that does not create new strings. Although less kotlin-idiomatic.
 */
private fun Long.consistsOfRepeatingSequenceV2(): Boolean {
    val id = toString()
    val n = id.length

    forSequenceLength@
    for (sequenceLength in (1..n / 2)) {
        if (n % sequenceLength != 0) continue
        for (i in sequenceLength until n) {
            if (id[i] != id[i % sequenceLength]) {
                continue@forSequenceLength
            }
        }
        return true
    }
    return false
}

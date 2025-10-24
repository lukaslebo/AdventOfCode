package day20

import check
import readInput

fun main() {
    val testInput = readInput("2016", "Day20_test")
    check(part1(testInput), 3)

    val input = readInput("2016", "Day20")
    println(part1(input))
    println(part2(input))
    check(part2(input) > 124)
}

private val maxAddress = 4294967295

private fun part1(input: List<String>): Long {
    val blockedRanges = input.parseBlockedIpRanges()
    var address = 0L
    while (address < maxAddress) {
        val blockedRange = blockedRanges.find { address in it }
        if (blockedRange != null) {
            address = blockedRange.last + 1
        } else {
            return address
        }
    }
    error("all addresses are blocked")
}

private fun part2(input: List<String>): Long {
    val blockedRanges = input.parseBlockedIpRanges().withoutOverlaps()
    var allowedSize = maxAddress + 1
    for (blockedRange in blockedRanges) {
        allowedSize -= (blockedRange.last - blockedRange.first + 1)
    }
    return allowedSize
}

private fun List<String>.parseBlockedIpRanges(): List<LongRange> = map {
    val (start, end) = it.split("-")
    start.toLong()..end.toLong()
}

private fun List<LongRange>.withoutOverlaps(): List<LongRange> {
    if (isEmpty()) return emptyList()

    val sorted = this.sortedBy { it.first }

    val merged = mutableListOf<LongRange>()
    var current = sorted.first()

    for (next in sorted.drop(1)) {
        if (next.first <= current.last + 1) {
            current = current.first..maxOf(current.last, next.last)
        } else {
            merged += current
            current = next
        }
    }
    merged += current
    return merged
}

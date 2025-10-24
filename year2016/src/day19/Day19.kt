package day19

import check
import readInput

fun main() {
    val testInput = readInput("2016", "Day19_test")
    check(part1(testInput), 3)
    check(part2(testInput), 2)
    println("checks passed")

    val input = readInput("2016", "Day19")
    println(part1(input))

    val part2 = part2(input)
    println(part2)
    check(part2 < 1841611)
}

private fun part1(input: List<String>): Int {
    val elfCount = input.first().toInt()
    var elfCircle = (1..elfCount).toList()
    var offset = 0
    while (elfCircle.size > 1) {
        val lastElf = elfCircle.last()
        elfCircle = elfCircle.filterIndexed { index, _ -> index % 2 == offset }
        offset = if (lastElf == elfCircle.last()) 1 else 0
    }
    return elfCircle.single()
}

private fun part2(input: List<String>): Int {
    val elfCount = input.first().toInt()
    val elfCircle = (1..elfCount).toList()
    val half = elfCircle.size / 2
    val firstHalfQueue = ArrayDeque(elfCircle.take(half))
    val lastHalfQueue = ArrayDeque(elfCircle.takeLast(elfCircle.size - half))
    while (firstHalfQueue.size + lastHalfQueue.size > 1) {
        lastHalfQueue.removeFirst()
        lastHalfQueue += firstHalfQueue.removeFirst()
        if (firstHalfQueue.size < lastHalfQueue.size - 1) {
            firstHalfQueue += lastHalfQueue.removeFirst()
        }
    }
    return lastHalfQueue.single()
}

package day06

import readInput

fun main() {
    val input = readInput("2017", "Day06")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.reallocateUntilCycle().size

private fun part2(input: List<String>): Int {
    val cycles = input.reallocateUntilCycle().toList()
    val cycleStart = cycles.last().toMutableList().reallocate()
    return cycles.dropWhile { it != cycleStart }.size
}

private fun List<String>.reallocateUntilCycle(): Set<List<Int>> {
    val memory = first().split("\t").map { it.toInt() }.toMutableList()
    val seen = mutableSetOf<List<Int>>()
    var cycles = 0
    while (memory !in seen) {
        seen += memory.toList()
        memory.reallocate()
        cycles++

    }
    return seen
}

private fun MutableList<Int>.reallocate(): MutableList<Int> {
    val max = max()
    val maxIndex = indexOf(max)
    this[maxIndex] = 0
    for (offset in 1..max) {
        this[(maxIndex + offset) % size]++
    }
    return this
}

package day24

import check
import readInput

fun main() {
    val testInput = readInput("2017", "Day24_test")
    check(part1(testInput), 31)
    check(part2(testInput), 19)

    val input = readInput("2017", "Day24")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val components = input.parseComponents()
    val bridges = combineToBridge(components)
    return bridges.maxOf { it.sum() }
}

private fun part2(input: List<String>): Int {
    val components = input.parseComponents()
    val bridges = combineToBridge(components)
    val maxSize = bridges.maxOf { it.size }
    return bridges.filter { it.size == maxSize }.maxOf { it.sum() }
}

private fun List<String>.parseComponents() =
    map { line -> line.split("/").map { it.toInt() }.let { it.first() to it.last() } }

private fun combineToBridge(components: List<Pair<Int, Int>>, bridge: List<Int> = emptyList()): List<List<Int>> {
    val port = bridge.lastOrNull() ?: 0
    val candidates = components.filter { it.first == port || it.second == port }
    return if (candidates.isEmpty()) {
        listOf(bridge)
    } else candidates.flatMap { candidate ->
        if (candidate.first == port) {
            combineToBridge(components - candidate, bridge + candidate.first + candidate.second)
        } else {
            combineToBridge(components - candidate, bridge + candidate.second + candidate.first)
        }
    }
}

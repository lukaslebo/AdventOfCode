package day12

import check
import readInput

fun main() {
    val testInput = readInput("2017", "Day12_test")
    check(part1(testInput), 6)

    val input = readInput("2017", "Day12")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.parseGraph().groupStartingFrom(0).size

private fun part2(input: List<String>): Int {
    val graph = input.parseGraph()
    val nodes = graph.keys.toMutableSet()
    val groups = mutableSetOf<Set<Int>>()
    while (nodes.isNotEmpty()) {
        val nextNode = nodes.first()
        val group = graph.groupStartingFrom(nextNode)
        nodes -= group
        groups += group
    }
    return groups.size
}

private fun List<String>.parseGraph(): Map<Int, Set<Int>> {
    val graph = mutableMapOf<Int, Set<Int>>()
    for (line in this) {
        val from = line.substringBefore(" <-> ").toInt()
        val neighbours = line.substringAfter(" <-> ").split(", ").map { it.toInt() }.toSet()
        graph += from to neighbours
    }
    return graph
}

private fun Map<Int, Set<Int>>.groupStartingFrom(node: Int): Set<Int> {
    val seen = mutableSetOf<Int>()
    val queue = ArrayDeque<Int>()
    queue += node
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        seen += current
        queue += getValue(current).filter { it !in seen }
    }
    return seen
}

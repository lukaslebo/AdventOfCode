package year2024.day10

import check
import readInput

fun main() {
    val testInput = readInput("2024", "Day10_test")
    check(part1(testInput), 36)
    check(part2(testInput), 81)

    val input = readInput("2024", "Day10")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.parseNodes().findAllTrails().countUniqueTrails()

private fun part2(input: List<String>) = input.parseNodes().findAllTrails().size

private data class Pos(val x: Int, val y: Int) {
    val adjacent
        get() = setOf(
            Pos(x, y + 1),
            Pos(x + 1, y),
            Pos(x, y - 1),
            Pos(x - 1, y),
        )
}

private data class Node(
    val pos: Pos,
    val height: Int,
)

private fun List<String>.parseNodes(): List<Node> {
    val nodes = mutableListOf<Node>()
    for ((y, line) in withIndex()) {
        for ((x, c) in line.withIndex()) {
            nodes += Node(Pos(x, y), c.digitToInt())
        }
    }
    return nodes
}

private fun List<Node>.findAllTrails(): List<List<Node>> {
    val neighboursByNode = neighboursByNode()
    val trails = mutableListOf<List<Node>>()
    val queue = ArrayDeque<List<Node>>()
    queue += filter { it.height == 0 }.chunked(1)
    while (queue.isNotEmpty()) {
        val hike = queue.removeFirst()
        val currentNode = hike.last()
        if (currentNode.height == 9) {
            trails += hike
            continue
        }

        val neighbours = neighboursByNode[currentNode]?.filter { it !in hike } ?: emptySet()
        queue += neighbours.map { hike + it }
    }
    return trails
}

private fun List<List<Node>>.countUniqueTrails() = groupBy { it.first() to it.last() }.size

private fun List<Node>.neighboursByNode(): Map<Node, Set<Node>> {
    val nodeByPos = associateBy { it.pos }
    return associateWith { node ->
        node.pos.adjacent
            .mapNotNull { nodeByPos[it] }
            .filter { it.height - node.height == 1 }
            .toSet()
    }
}

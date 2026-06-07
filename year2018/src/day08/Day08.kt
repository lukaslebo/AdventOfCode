package day08

import check
import readInput

fun main() {
    val testInput = readInput("2018", "Day08_test")
    check(part1(testInput), 138)
    check(part2(testInput), 66)

    val input = readInput("2018", "Day08")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.parse().allMetadata.sum()
private fun part2(input: List<String>) = input.parse().value

private data class Node(
    val header: List<Int>,
    val children: List<Node>,
    val metadata: List<Int>,
) {
    val size: Int = header.size + children.sumOf { it.size } + metadata.size
    val allMetadata: List<Int> = children.flatMap { it.allMetadata } + metadata
    val value: Int =
        if (children.isEmpty()) metadata.sum() else metadata.sumOf { children.getOrNull(it - 1)?.value ?: 0 }
}

private fun List<String>.parse() = first().split(" ").map { it.toInt() }.parseNode()

private fun List<Int>.parseNode(): Node {
    var childCount = get(0)
    val metadataCount = get(1)
    var data = drop(2)
    val children = mutableListOf<Node>()
    while (childCount > 0) {
        val node = data.parseNode()
        data = data.drop(node.size)
        children += node
        childCount--
    }

    return Node(take(2), children, data.take(metadataCount))
}

package day07

import check
import readInput

fun main() {
    val testInput = readInput("2017", "Day07_test")
    check(part1(testInput), "tknk")
    check(part2(testInput), 60)

    val input = readInput("2017", "Day07")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.parseGraph().getRoot()

private fun part2(input: List<String>): Int {
    val graph = input.parseGraph()
    val reversedGraph = graph.reverseGraph()
    val weightByNode = input.parseWeights()

    val totalWeightByNode = mutableMapOf<String, Int>()

    fun String.getLevel(): Int = (reversedGraph[this]?.getLevel() ?: 0) + 1
    fun String.getTotalWeight() = totalWeightByNode.getValue(this)
    fun calculateTotalWeight(node: String): Int = totalWeightByNode.getOrPut(node) {
        val children = graph.getValue(node)
        val weight = weightByNode.getValue(node)
        weight + children.sumOf { calculateTotalWeight(it) }
    }

    calculateTotalWeight(graph.getRoot())

    val unbalancedSiblings = graph.values
        .filter { it.isNotEmpty() }
        .filter { siblings ->
            val weight = siblings.first().getTotalWeight()
            siblings.drop(1).any { it.getTotalWeight() != weight }
        }
        .maxBy { it.first().getLevel() }

    val (group1, group2) = unbalancedSiblings.groupBy { it.getTotalWeight() }.values.sortedBy { it.size }

    val culprit = group1.first()
    val actualWeight = culprit.getTotalWeight()
    val targetWeight = group2.first().getTotalWeight()
    val delta = targetWeight - actualWeight

    return weightByNode.getValue(culprit) + delta
}

private typealias Graph = Map<String, Set<String>>

private fun List<String>.parseGraph() = associate { line ->
    val from = line.substringBefore(" ")
    val targets = line.substringAfter(" -> ", "").split(", ").filter { it.isNotBlank() }.toSet()
    from to targets
}

private fun List<String>.parseWeights() = associate { line ->
    val from = line.substringBefore(" ")
    val weight = line.substringAfter("(").substringBefore(")").toInt()
    from to weight
}

private fun Graph.reverseGraph(): Map<String, String> {
    val result = mutableMapOf<String, String>()
    for ((node, neighbours) in this) {
        for (neighbour in neighbours) {
            result += neighbour to node
        }
    }
    return result
}

private fun Graph.getRoot(): String {
    val reversedGraph = reverseGraph()
    for (node in keys) {
        if (node !in reversedGraph) {
            return node
        }
    }
    error("graph does not have root node")
}

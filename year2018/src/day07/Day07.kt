package day07

import check
import readInput

fun main() {
    val testInput = readInput("2018", "Day07_test")
    check(part1(testInput), "CABDFE")
    check(part2(testInput), 15)

    val input = readInput("2018", "Day07")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): String {
    val dependencies = input.parseDependencies()
    val nodes = (dependencies.keys + dependencies.values.flatten()).sorted().toMutableSet()
    val solution = mutableListOf<Char>()
    while (nodes.isNotEmpty()) {
        val nextNode = nodes.first { node ->
            val dependenciesForNode = dependencies[node] ?: emptySet()
            dependenciesForNode.all { it in solution }
        }
        solution += nextNode
        nodes -= nextNode
    }
    return solution.joinToString("") { it.toString() }
}

private fun part2(input: List<String>): Int {
    val dependencies = input.parseDependencies()
    val nodes = (dependencies.keys + dependencies.values.flatten()).sorted().toMutableSet()
    val solution = mutableListOf<Char>()
    val baseCost = if (nodes.size < 7) 0 else 60
    var availableWorkers = if (nodes.size < 7) 2 else 4
    var constructionToCost = listOf<Pair<Char, Int>>()
    var time = 0
    while (nodes.isNotEmpty()) {
        val nextNodes = nodes.filter { node ->
            val dependenciesForNode = dependencies[node] ?: emptySet()
            dependenciesForNode.all { it in solution }
        }.take(availableWorkers)
        nodes -= nextNodes.toSet()
        availableWorkers -= nextNodes.size
        constructionToCost = constructionToCost + nextNodes.map { it to (baseCost + (it.code - 'A'.code + 1)) }
        val dt = constructionToCost.minOf { it.second }
        val finished = constructionToCost.filter { it.second == dt }.map { it.first }
        solution += finished
        availableWorkers += finished.size
        constructionToCost = constructionToCost.map { it.first to it.second - dt }.filter { it.second > 0 }
        time += dt
    }
    return time
}

private fun List<String>.parseDependencies() = map { it[36] to it[5] }
    .groupBy { it.first }
    .mapValues { entry -> entry.value.map { it.second }.toSet() }

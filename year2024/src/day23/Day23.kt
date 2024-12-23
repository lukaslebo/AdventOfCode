package day23

import check
import readInput
import util.bronKerbosch

fun main() {
    val testInput = readInput("2024", "Day23_test")
    check(part1(testInput), 7)
    check(part2(testInput), "co,de,ka,ta")

    val input = readInput("2024", "Day23")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.parseGraph().findGroupsOfThree().countStartsWith("t")

private fun part2(input: List<String>) = input.parseGraph().findLargestGroup().password()

private typealias Graph = Map<String, Set<String>>

private fun List<String>.parseGraph(): Graph {
    return flatMap { line ->
        val (a, b) = line.split("-")
        listOf(a to b, b to a)
    }
        .groupBy { it.first }
        .mapValues { (_, v) -> v.map { it.second }.toSet() }
}

private fun Graph.findGroupsOfThree(): Set<Set<String>> {
    val groups = mutableSetOf<Set<String>>()
    for (a in keys) {
        val fromA = getValue(a)
        for (b in fromA) {
            for (c in fromA) {
                val fromC = getValue(c)
                if (b in fromC) groups += setOf(a, b, c)
            }
        }
    }
    return groups
}

private fun Set<Set<String>>.countStartsWith(prefix: String) = count { group -> group.any { it.startsWith(prefix) } }

private fun Graph.findLargestGroup(): Set<String> {
    val cliques = bronKerbosch(potentialVertices = keys)
    return cliques.maxBy { it.size }
}

private fun Set<String>.password() = sorted().joinToString(",")

package year2015.day13

import check
import readInput

fun main() {
    val testInput = readInput("2015", "Day13_test")
    check(part1(testInput), 330)

    val input = readInput("2015", "Day13")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val graph = parseGraph(input)
    return getHappinessForBestSeatingCombination(graph)
}

private fun part2(input: List<String>): Int {
    val graph = parseGraph(input).toMutableMap()
    graph["Me"] = emptyMap()
    return getHappinessForBestSeatingCombination(graph)
}

private fun parseGraph(input: List<String>): Map<String, Map<String, Int>> {
    val graph = hashMapOf<String, MutableMap<String, Int>>()
    val pattern = "(\\w+) would (gain|lose) (\\d+) happiness units by sitting next to (\\w+).".toRegex()
    input.forEach { line ->
        val (_, a, gainOrLose, happyness, b) = pattern.matchEntire(line)?.groupValues
            ?: error("Line not matching pattern: $line")
        val change = (if (gainOrLose == "lose") -1 else 1) * happyness.toInt()
        graph.getOrPut(a) { hashMapOf() } += b to change
    }
    return graph
}

private fun getHappinessForBestSeatingCombination(graph: Map<String, Map<String, Int>>): Int {
    val combinations = getCombinations(graph.keys)
    val totalHappinessByCombination = combinations.associateWith { combination ->
        (combination.windowed(2) + listOf(listOf(combination.first(), combination.last()))).sumOf { (a, b) ->
            (graph.getValue(a)[b] ?: 0) + (graph.getValue(b)[a] ?: 0)
        }
    }
    return totalHappinessByCombination.values.max()
}

private fun getCombinations(remaining: Set<String>, people: List<String> = emptyList()): List<List<String>> {
    if (remaining.isEmpty()) return listOf(people)
    val result = mutableListOf<List<String>>()
    for (person in remaining) {
        result += getCombinations(remaining - person, people + person)
    }
    return result
}
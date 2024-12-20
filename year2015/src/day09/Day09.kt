package day09

import check
import readInput

fun main() {
    val testInput = readInput("2015", "Day09_test")
    check(part1(testInput), 605)
    check(part2(testInput), 982)

    val input = readInput("2015", "Day09")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = distancesByRoute(input).minOf { it.value }

private fun part2(input: List<String>) = distancesByRoute(input).maxOf { it.value }

private fun distancesByRoute(input: List<String>): Map<List<String>, Int> {
    val graph = hashMapOf<String, MutableList<Pair<String, Int>>>()
    input.forEach { line ->
        val (a, _, b, _, distance) = line.split(' ')
        graph.getOrPut(a) { mutableListOf() } += b to distance.toInt()
        graph.getOrPut(b) { mutableListOf() } += a to distance.toInt()
    }

    val queue = ArrayDeque<List<String>>()
    val possibleRoutes = mutableListOf<List<String>>()
    queue += graph.keys.map { listOf(it) }
    while (queue.isNotEmpty()) {
        val route = queue.removeFirst()
        if (route.size == graph.keys.size) {
            possibleRoutes += route
            continue
        }
        for (next in graph[route.last()] ?: emptyList()) {
            if (next.first !in route) {
                queue += route + next.first
            }
        }
    }
    return possibleRoutes.associateWith { route ->
        route.windowed(2) { (from, to) ->
            graph[from]?.find { it.first == to }?.second ?: error("Distance for $from to $to not available")
        }.sum()
    }
}

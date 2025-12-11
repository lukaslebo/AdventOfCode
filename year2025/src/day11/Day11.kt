package day11

import check
import readInput

fun main() {
    val testInputA = readInput("2025", "Day11_test_a")
    val testInputB = readInput("2025", "Day11_test_b")
    check(part1(testInputA), 5)
    check(part2(testInputB), 2)

    val input = readInput("2025", "Day11")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.parseGraph().countPaths(from = "you", to = "out")

private fun part2(input: List<String>) =
    input.parseGraph().countPaths(from = "svr", to = "out", requiredNodes = setOf("dac", "fft"))

private fun Map<String, List<String>>.countPaths(
    from: String,
    to: String,
    requiredNodes: Set<String> = emptySet()
): Long {
    val cache = mutableMapOf<Pair<String, Set<String>>, Long>()

    fun dfs(current: String, visited: Set<String>): Long =
        cache.getOrPut(current to (visited intersect requiredNodes)) {
            if (current == to) {
                if (requiredNodes.all { it in visited }) 1 else 0
            } else getValue(current).sumOf { next ->
                if (next in visited) 0
                else dfs(next, visited + next)
            }
        }

    return dfs(current = from, visited = setOf(from))
}

private fun List<String>.parseGraph() = associate { line ->
    val from = line.substringBefore(":")
    val targets = line.substringAfter(": ").split(" ")
    from to targets
}

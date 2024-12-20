package day12

import check
import readInput

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day12_test")
    check(part1(testInput), 31)
    check(part2(testInput), 29)

    val input = readInput("2022", "Day12")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val (graph, start, target) = parseInput(input) { it <= 1 }
    val shortestPath = findShortestPath(graph, start) { it == target }
    return shortestPath.size - 1
}

private fun part2(input: List<String>): Int {
    val (graph, _, target) = parseInput(input) { it >= -1 }
    val shortestPath = findShortestPath(graph, target) { it.height == 0 }
    return shortestPath.size - 1
}

private fun findShortestPath(graph: Map<Pos, List<Pos>>, start: Pos, isTarget: (Pos) -> Boolean): List<Pos> {
    val stack = ArrayDeque<List<Pos>>()
    val visited = mutableSetOf(start)
    stack.addLast(listOf(start))
    while (stack.isNotEmpty()) {
        val path = stack.removeFirst()
        val current = path.last()
        val ways = graph[current] ?: emptyList()
        for (next in ways) {
            if (next !in visited) {
                visited += next
                val newPath = path + next
                if (isTarget(next)) return newPath
                stack.addLast(newPath)
            }
        }
    }
    error("no path found")
}

private fun parseInput(input: List<String>, isWay: (Int) -> Boolean): PuzzleInput {
    val maxX = input.first().lastIndex
    val maxY = input.lastIndex

    val graph = mutableMapOf<Pos, List<Pos>>()
    lateinit var start: Pos
    lateinit var target: Pos

    input.forEachIndexed { y, line ->
        line.forEachIndexed { x, char ->
            val pos = Pos(x, y, char.height)
            val ways = mutableListOf<Pos>()
            if (char == 'S') start = pos
            if (char == 'E') target = pos
            if (x > 0 && isWay(input[y][x - 1].height - char.height)) ways += Pos(x - 1, y, input[y][x - 1].height)
            if (x < maxX && isWay(input[y][x + 1].height - char.height)) ways += Pos(x + 1, y, input[y][x + 1].height)
            if (y > 0 && isWay(input[y - 1][x].height - char.height)) ways += Pos(x, y - 1, input[y - 1][x].height)
            if (y < maxY && isWay(input[y + 1][x].height - char.height)) ways += Pos(x, y + 1, input[y + 1][x].height)
            graph[pos] = ways
        }
    }

    return PuzzleInput(
        graph = graph,
        start = start,
        target = target,
    )
}

private val Char.height: Int
    get() = when (this) {
        'S' -> 'a'
        'E' -> 'z'
        else -> this
    } - 'a'

private data class Pos(val x: Int, val y: Int, val height: Int)

private data class PuzzleInput(
    val graph: Map<Pos, List<Pos>>,
    val start: Pos,
    val target: Pos,
)

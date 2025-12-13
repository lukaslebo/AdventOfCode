package day13

import algorithms.aStar
import check
import readInput
import kotlin.math.abs

fun main() {
    val testInput = readInput("2016", "Day13_test")
    check(part1(testInput, target = Pos(7, 4)), 11)

    val input = readInput("2016", "Day13")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>, target: Pos = Pos(31, 39)): Int {
    val favoriteNumber = input.first().toInt()
    return aStar(
        from = Pos(1, 1),
        goal = { it == target },
        neighboursWithCost = {
            Pos.directions.map { this + it }
                .filter { it.x >= 0 && it.y >= 0 && !it.isWall(favoriteNumber) }
                .map { it to 1 }
                .toSet()
        },
        heuristic = { it.manhattanDistanceTo(target) },
    )?.cost ?: error("no path found")
}

private fun part2(input: List<String>): Int {
    val favoriteNumber = input.first().toInt()

    data class Node(val pos: Pos, val steps: Int)

    val seen = mutableSetOf<Pos>()
    val queue = ArrayDeque<Node>()
    queue += Node(pos = Pos(1, 1), steps = 0)

    while (queue.isNotEmpty()) {
        val node = queue.removeFirst()
        if (node.steps > 50) continue
        seen += node.pos
        queue += Pos.directions.map { node.pos + it }
            .filter { it.x >= 0 && it.y >= 0 && !it.isWall(favoriteNumber) && it !in seen }
            .map { Node(it, node.steps + 1) }
    }
    return seen.size
}


private data class Pos(val x: Int, val y: Int) {
    companion object {
        val up = Pos(0, -1)
        val down = Pos(0, 1)
        val left = Pos(-1, 0)
        val right = Pos(1, 0)

        val directions = setOf(up, down, left, right)
    }

    operator fun plus(other: Pos) = Pos(x + other.x, y + other.y)
    fun manhattanDistanceTo(other: Pos) = abs(x - other.x) + abs(y - other.y)
}

private fun Pos.isWall(favoriteNumber: Int): Boolean {
    val result = (x * x) + (3 * x) + (2 * x * y) + (y) + (y * y) + favoriteNumber
    return result.countOneBits() % 2 != 0
}

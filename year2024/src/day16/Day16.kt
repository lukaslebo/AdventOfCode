package day16

import check
import readInput
import util.aStar
import util.allBestPaths
import kotlin.math.abs

fun main() {
    val testInput1 = readInput("2024", "Day16_test1")
    val testInput2 = readInput("2024", "Day16_test2")
    check(part1(testInput1), 7036)
    check(part1(testInput2), 11048)
    check(part2(testInput1), 45)
    check(part2(testInput2), 64)

    val input = readInput("2024", "Day16")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.parseRace().findBestPathScore()

private fun part2(input: List<String>) = input.parseRace().countSeatsOnBestPaths()

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

private data class Race(
    val walls: Set<Pos>,
    val start: Pos,
    val end: Pos,
)

private fun List<String>.parseRace(): Race {
    var start: Pos? = null
    var end: Pos? = null
    val walls = mutableSetOf<Pos>()
    for ((y, line) in withIndex()) {
        for ((x, c) in line.withIndex()) {
            when (c) {
                'S' -> start = Pos(x, y)
                'E' -> end = Pos(x, y)
                '#' -> walls += Pos(x, y)
            }
        }
    }
    return Race(
        walls = walls,
        start = start ?: error("start not found"),
        end = end ?: error("end not found"),
    )
}

private data class Node(val pos: Pos, val dir: Pos)

private fun Node.neighboursWithCost(race: Race) = Pos.directions
    .filter { it + dir != Pos(0, 0) }
    .map {
        if (it == dir) Node(pos + dir, dir) to 1
        else Node(pos, it) to 1000
    }
    .filter { it.first.pos !in race.walls }
    .toSet()

private fun Race.findBestPathScore(): Int {
    val endNode = aStar(
        from = Node(start, Pos.right),
        goal = { it.pos == end },
        neighboursWithCost = { neighboursWithCost(this@findBestPathScore) },
        heuristic = { it.pos.manhattanDistanceTo(end) },
    )
    return endNode?.cost ?: error("no path found")
}

private fun Race.countSeatsOnBestPaths(): Int {
    val allBestPaths = allBestPaths(
        from = Node(start, Pos.right),
        goal = { it.pos == end },
        neighboursWithCost = { neighboursWithCost(this@countSeatsOnBestPaths) },
        heuristic = { it.pos.manhattanDistanceTo(end) },
    )
    return allBestPaths.flatMap { it.path() }.map { it.pos }.toSet().size
}

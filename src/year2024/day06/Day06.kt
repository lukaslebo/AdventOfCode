package year2024.day06

import check
import readInput

fun main() {
    val testInput = readInput("2024", "Day06_test")
    check(part1(testInput), 41)
    check(part2(testInput), 6)

    val input = readInput("2024", "Day06")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.parseMap().getGuardRoute().visited.size

private fun part2(input: List<String>) = input.parseMap().findObstaclesThatCreateLoop().size

private data class Pos(val x: Int, val y: Int) {
    fun turnRight() = when (this) {
        up -> right
        right -> down
        down -> left
        left -> up
        else -> error("cant turn right from $this")
    }

    companion object {
        val up = Pos(0, -1)
        val down = Pos(0, 1)
        val left = Pos(-1, 0)
        val right = Pos(1, 0)
    }

    operator fun plus(other: Pos) = Pos(x + other.x, y + other.y)
}

private data class Map(
    val obstacles: Set<Pos>,
    val start: Pos,
    val xRange: IntRange,
    val yRange: IntRange,
)

private data class GuardRoute(
    val visited: Set<Pos>,
    val isLoop: Boolean,
)

private fun List<String>.parseMap(): Map {
    val obstacles = mutableSetOf<Pos>()
    val xRange = first().indices
    val yRange = indices
    var guard: Pos? = null
    for ((y, line) in withIndex()) {
        for ((x, c) in line.withIndex()) {
            if (c == '#') obstacles += Pos(x, y)
            if (c == '^') guard = Pos(x, y)
        }
    }
    return Map(
        obstacles = obstacles,
        start = guard ?: error("guard not found"),
        xRange = xRange,
        yRange = yRange,
    )
}

private fun Map.getGuardRoute(): GuardRoute {
    var pos = start
    var dir = Pos.up
    val visitedPosAndDir = mutableSetOf<Pair<Pos, Pos>>()
    while (pos.x in xRange && pos.y in yRange) {
        if (pos to dir in visitedPosAndDir) {
            return GuardRoute(
                visited = visitedPosAndDir.map { it.first }.toSet(),
                isLoop = true,
            )
        }
        visitedPosAndDir += pos to dir
        val next = pos + dir
        if (next in obstacles) dir = dir.turnRight()
        else pos = next

    }
    return GuardRoute(
        visited = visitedPosAndDir.map { it.first }.toSet(),
        isLoop = false,
    )
}

private fun Map.findObstaclesThatCreateLoop(): List<Pos> {
    val possiblePositions = getGuardRoute().visited - start
    return possiblePositions
        .parallelStream()
        .filter { copy(obstacles = obstacles + it).getGuardRoute().isLoop }
        .toList()
}

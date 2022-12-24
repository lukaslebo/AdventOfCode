package year2022.day24

import check
import readInput

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day24_test")
    check(part1(testInput), 18)
    check(part2(testInput), 54)

    val input = readInput("2022", "Day24")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val (walls, blizzards) = parseWallsAndBlizzards(input)
    val (start, target) = getStartAndTarget(input)
    return getStepsThroughBlizzardValley(start, target, walls, blizzards)
}

private fun part2(input: List<String>): Int {
    val (walls, blizzards) = parseWallsAndBlizzards(input)
    val (start, target) = getStartAndTarget(input)

    val p1 = getStepsThroughBlizzardValley(start, target, walls, blizzards)
    val p2 = getStepsThroughBlizzardValley(target, start, walls, blizzards)
    val p3 = getStepsThroughBlizzardValley(start, target, walls, blizzards)
    return p1 + p2 + p3
}

private fun parseWallsAndBlizzards(input: List<String>): Pair<Set<Pos>, List<Blizzard>> {
    val walls = mutableSetOf<Pos>()
    val blizzards = mutableListOf<Blizzard>()
    input.forEachIndexed { y, line ->
        line.forEachIndexed { x, c ->
            if (c == '#') walls += Pos(x, y)
            if (c != '#' && c != '.') {
                val pos = Pos(x, y)
                val dir = c.toDir()
                blizzards += Blizzard(pos, dir)
            }
        }
    }
    return walls to blizzards
}

private fun getStartAndTarget(input: List<String>): Pair<Pos, Pos> {
    val start = input.first().indexOfFirst { it == '.' }.let { Pos(it, 0) }
    val target = input.last().indexOfFirst { it == '.' }.let { Pos(it, input.lastIndex) }
    return start to target
}

private data class Pos(val x: Int, val y: Int) {
    val possibleMoves
        get() = listOf(
            this, // wait
            Pos(x, y + 1), // N
            Pos(x + 1, y), // E
            Pos(x, y - 1), // S
            Pos(x - 1, y), // W
        )
}

private enum class Direction {
    N, E, S, W
}

private fun Char.toDir() = when (this) {
    '^' -> Direction.N
    '>' -> Direction.E
    'v' -> Direction.S
    '<' -> Direction.W
    else -> error("$this cannot be mapped to a direction")
}

private class Blizzard(var pos: Pos, val dir: Direction) {
    fun move(maxX: Int, maxY: Int) {
        pos = nextBlizzardPosition(pos, dir, maxX, maxY)
    }
}

private fun nextBlizzardPosition(current: Pos, dir: Direction, maxX: Int, maxY: Int): Pos {
    val next = when (dir) {
        Direction.N -> Pos(current.x, current.y - 1)
        Direction.E -> Pos(current.x + 1, current.y)
        Direction.S -> Pos(current.x, current.y + 1)
        Direction.W -> Pos(current.x - 1, current.y)
    }
    return when {
        next.x < 1 -> Pos(maxX, next.y)
        next.x > maxX -> Pos(1, next.y)
        next.y < 1 -> Pos(next.x, maxY)
        next.y > maxY -> Pos(next.x, 1)
        else -> next
    }
}

private fun getStepsThroughBlizzardValley(
    start: Pos,
    target: Pos,
    walls: Set<Pos>,
    blizzards: List<Blizzard>,
): Int {
    val maxX = walls.maxOf { it.x } - 1
    val maxY = walls.maxOf { it.y } - 1
    blizzards.forEach { it.move(maxX, maxY) }
    val yRange = 0..maxY + 1

    var positions = setOf(start)
    var steps = 0
    while (true) {
        val blockedByBlizzard = blizzards.mapTo(hashSetOf()) { it.pos }
        positions = positions.flatMapTo(hashSetOf()) { it.possibleMoves }.filterTo(hashSetOf()) {
            it !in blockedByBlizzard && it !in walls && it.y in yRange
        }
        steps++
        if (target in positions) break
        blizzards.forEach { it.move(maxX, maxY) }
    }

    return steps
}
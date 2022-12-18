package year2022.day18

import check
import readInput
import kotlin.math.abs

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day18_test")
    check(part1(testInput), 64)
    check(part2(testInput), 58)

    val input = readInput("2022", "Day18")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val lavaDroplet = parsePositions(input)
    return calculateSurface(lavaDroplet)
}

private fun part2(input: List<String>): Int {
    val lavaDroplet = parsePositions(input)
    val minX = lavaDroplet.minOf { it.x } - 1
    val maxX = lavaDroplet.maxOf { it.x } + 1
    val minY = lavaDroplet.minOf { it.y } - 1
    val maxY = lavaDroplet.maxOf { it.y } + 1
    val minZ = lavaDroplet.minOf { it.z } - 1
    val maxZ = lavaDroplet.maxOf { it.z } + 1

    val enclosingVolume = getVolume(minX, maxX, minY, maxY, minZ, maxZ)
    // enclosing air volume with bubbles of air on the inside
    val airVolume = enclosingVolume - lavaDroplet

    val startingPos = airVolume.first {
        it.x == minX || it.x == maxX || it.y == minY || it.y == maxY || it.z == minZ || it.z == maxZ
    }
    val outsideAirVolume = getAllReachablePositions(startingPos, airVolume)

    val gasPockets = airVolume - outsideAirVolume

    return calculateSurface(lavaDroplet) - calculateSurface(gasPockets)
}

private fun getAllReachablePositions(
    startingPos: Pos,
    airVolume: Set<Pos>
): Set<Pos> {
    val queue = ArrayDeque<Pos>().apply { add(startingPos) }
    val visited = hashSetOf(startingPos)
    while (queue.isNotEmpty()) {
        val pos = queue.removeFirst()
        for (next in pos.adjacent) {
            if (next !in visited && next in airVolume) {
                visited += next
                queue.addLast(next)
            }
        }
    }
    return visited
}

private fun calculateSurface(positions: Set<Pos>) =
    positions.size * 6 - positions.sumOf { pos ->
        positions.count {
            it touches pos
        }
    }

private fun getVolume(
    minX: Int,
    maxX: Int,
    minY: Int,
    maxY: Int,
    minZ: Int,
    maxZ: Int
): MutableSet<Pos> {
    val volume = mutableSetOf<Pos>()
    for (x in minX..maxX) {
        for (y in minY..maxY) {
            for (z in minZ..maxZ) {
                volume += Pos(x, y, z)
            }
        }
    }
    return volume
}

private fun parsePositions(input: List<String>) = input.mapTo(mutableSetOf()) { line ->
    val (x, y, z) = line.split(',').map { it.toInt() }
    Pos(x, y, z)
}

private data class Pos(val x: Int, val y: Int, val z: Int) {
    infix fun touches(other: Pos): Boolean = (x == other.x && y == other.y && abs(z - other.z) == 1) ||
            (x == other.x && z == other.z && abs(y - other.y) == 1) ||
            (y == other.y && z == other.z && abs(x - other.x) == 1)

    val adjacent: Set<Pos>
        get() = setOf(
            Pos(x + 1, y, z),
            Pos(x, y + 1, z),
            Pos(x, y, z + 1),
            Pos(x - 1, y, z),
            Pos(x, y - 1, z),
            Pos(x, y, z - 1),
        )
}

package day14

import check
import readInput

import kotlin.math.max
import kotlin.math.min

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day14_test")
    check(part1(testInput), 24)
    check(part2(testInput), 93)

    val input = readInput("2022", "Day14")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val (source, terrain) = parseToSourceAndTerrain(input)
    return simulateSandfillAndGetSandCount(source, terrain)
}

private fun part2(input: List<String>): Int {
    val (source, terrain) = parseToSourceAndTerrain(input, extraHeight = 2, generateExtraWidth = true)
    val bottom = terrain.last()
    for (x in bottom.indices) {
        bottom[x] = false
    }
    return simulateSandfillAndGetSandCount(source, terrain)
}

private fun simulateSandfillAndGetSandCount(source: Pos, terrain: Array<Array<Boolean>>): Int {
    var sandCount = 0
    while (true) {
        var sand = source
        while (terrain[sand.y][sand.x]) {
            val down = terrain.get(sand.down()) ?: return sandCount

            if (down) {
                sand = sand.down()
                continue
            }
            val downLeft = terrain.get(sand.downLeft()) ?: return sandCount

            if (downLeft) {
                sand = sand.downLeft()
                continue
            }
            val downRight = terrain.get(sand.downRight()) ?: return sandCount

            if (downRight) {
                sand = sand.downRight()
                continue
            }

            sandCount++
            terrain[sand.y][sand.x] = false
            if (sand == source) return sandCount
        }
    }
}

private data class Pos(val x: Int, val y: Int) {
    fun down() = copy(y = y + 1)
    fun downLeft() = copy(x = x - 1, y = y + 1)
    fun downRight() = copy(x = x + 1, y = y + 1)
}

private fun Array<Array<Boolean>>.get(pos: Pos) = getOrNull(pos.y)?.getOrNull(pos.x)

private fun parseToSourceAndTerrain(
    input: List<String>, extraHeight: Int = 0, generateExtraWidth: Boolean = false
): Pair<Pos, Array<Array<Boolean>>> {
    var minX = Int.MAX_VALUE
    var minY = Int.MAX_VALUE
    var maxX = Int.MIN_VALUE
    var maxY = Int.MIN_VALUE

    val rockPaths = input.map { line ->
        line.split(" -> ").map { hint ->
            val (x, y) = hint.split(',').map { it.toInt() }
            minX = min(minX, x)
            minY = min(minY, y)
            maxX = max(maxX, x)
            maxY = max(maxY, y)
            Pos(x, y)
        }
    }

    val extraWidth = if (generateExtraWidth) 2 * (maxY + 1) else 0
    val size = Pos(maxX - minX + 1 + extraWidth, maxY + 1 + extraHeight)
    val shift = Pos(minX - extraWidth / 2, 0)
    val source = Pos(500 - shift.x, 0 - shift.y)

    val terrain = Array(size.y) { Array(size.x) { true } }
    for (rockPath in rockPaths) {
        rockPath.windowed(2) { pair ->
            val (pos1, pos2) = pair
            if (pos1.x == pos2.x) {
                val start = min(pos1.y, pos2.y)
                val end = max(pos1.y, pos2.y)
                for (y in start..end) {
                    terrain[y][pos1.x - shift.x] = false
                }
            } else {
                val start = min(pos1.x, pos2.x)
                val end = max(pos1.x, pos2.x)
                for (x in start..end) {
                    terrain[pos1.y][x - shift.x] = false
                }
            }
        }
    }
    return source to terrain
}

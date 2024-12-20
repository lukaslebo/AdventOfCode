package day18

import check
import readInput
import util.aStar
import kotlin.math.abs

fun main() {
    val testInput = readInput("2024", "Day18_test")
    check(part1(testInput, corruptionSize = 12), 22)
    check(part2(testInput, initialCorruptionSize = 13), "6,1")

    val input = readInput("2024", "Day18")
    println(part1(input, corruptionSize = 1024))
    println(part2(input, initialCorruptionSize = 1025))
}

private fun part1(input: List<String>, corruptionSize: Int) = input.parseMemoryCorruptionMap()
    .findShortestPath(corruptionSize)!!
    .size - 1

private fun part2(input: List<String>, initialCorruptionSize: Int): String {
    val memoryCorruptionMap = input.parseMemoryCorruptionMap()
    val searchRange = initialCorruptionSize..memoryCorruptionMap.corruptedMemory.lastIndex
    var shortestPath: Set<Pos>? = null

    fun Set<Pos>.hasCorruptedMemory(corruptionSize: Int) =
        memoryCorruptionMap.corruptedMemory.take(corruptionSize).any { it in this }

    for (corruptionSize in searchRange) {
        if (shortestPath != null && !shortestPath.hasCorruptedMemory(corruptionSize)) continue

        shortestPath = memoryCorruptionMap.findShortestPath(corruptionSize)?.toSet()
            ?: return memoryCorruptionMap.corruptedMemory[corruptionSize - 1].toString()
    }
    error("no solution found")
}

private data class Pos(val x: Int, val y: Int) {
    val adjacent
        get() = setOf(
            Pos(x, y + 1),
            Pos(x + 1, y),
            Pos(x, y - 1),
            Pos(x - 1, y),
        )

    fun manhattanDistanceTo(other: Pos) = abs(x - other.x) + abs(y - other.y)
    override fun toString() = "$x,$y"
}

private data class MemoryCorruptionMap(
    val target: Pos,
    val corruptedMemory: List<Pos>,
    val xRange: IntRange,
    val yRange: IntRange,
)

private fun List<String>.parseMemoryCorruptionMap(): MemoryCorruptionMap {
    val bytes = map { line ->
        val (x, y) = line.split(",").map { it.toInt() }
        Pos(x, y)
    }
    val xRange = 0..bytes.maxOf { it.x }
    val yRange = 0..bytes.maxOf { it.y }
    val target = Pos(xRange.last, yRange.last)
    return MemoryCorruptionMap(
        target = target,
        corruptedMemory = bytes,
        xRange = xRange,
        yRange = yRange,
    )
}

private fun MemoryCorruptionMap.findShortestPath(corruptionSize: Int): List<Pos>? {
    val alreadyCorrupted = corruptedMemory.take(corruptionSize).toSet()

    fun Pos.neighboursWithCost() = adjacent
        .filter { it !in alreadyCorrupted && it.x in xRange && it.y in yRange }
        .map { it to 1 }
        .toSet()

    val end = aStar(
        from = Pos(0, 0),
        goal = { it == target },
        neighboursWithCost = Pos::neighboursWithCost,
        heuristic = { it.manhattanDistanceTo(target) },
    )

    return end?.path()
}

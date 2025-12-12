package day12

import check
import readInput
import splitByEmptyLines

fun main() {
    val testInput = readInput("2025", "Day12_test")
    check(part1(testInput), 2)

    val input = readInput("2025", "Day12")
    println(part1(input))
}

private fun part1(input: List<String>): Int {
    val (shapes, regions) = input.parseShapesAndRegions()
    return regions.filter { it.fitsAll(shapes) }.size
}

private fun Region.fitsAll(availableShapes: List<Set<Pos>>): Boolean {
    val area = x * y
    val requiredArea = requiredShapeCounts.zip(availableShapes).sumOf { (n, shape) -> n * shape.size }
    if (area < requiredArea) return false

    val permutatedAndOffsetShapes = availableShapes
        .map { permutate(it) }
        .map { permutations ->
            val result = mutableSetOf<Set<Pos>>()
            for (shape in permutations) {
                for (yOffset in 0..x - 3) {
                    for (xOffset in 0..y - 3) {
                        val offset = Pos(xOffset, yOffset)
                        result += shape.map { it + offset }.toSet()
                    }
                }
            }
            result.toSet()
        }

    fun nextCountsAndShapes(shapeCounts: List<Int>): Pair<List<Int>, Set<Set<Pos>>> {
        for (i in shapeCounts.indices) {
            if (shapeCounts[i] > 0) {
                return shapeCounts.mapIndexed { j, n -> if (j == i) n - 1 else n } to permutatedAndOffsetShapes[i]
            }
        }
        return shapeCounts to emptySet()
    }

    var maxTries = 10000

    fun dfs(
        remainingShapeCounts: List<Int>,
        occupied: Set<Pos> = emptySet(),
        cache: MutableMap<Set<Pos>, Boolean> = mutableMapOf(),
    ): Boolean = cache.getOrPut(occupied) {
        if (maxTries-- <= 0) return@getOrPut false
        if (remainingShapeCounts.all { it == 0 }) return@getOrPut true
        val (nextCounts, shapes) = nextCountsAndShapes(remainingShapeCounts)
        for (shape in shapes) {
            if (shape.any { it in occupied }) continue
            val result = dfs(
                remainingShapeCounts = nextCounts,
                occupied = occupied + shape,
                cache = cache,
            )
            if (result) return@getOrPut true
        }
        return@getOrPut false
    }

    return dfs(requiredShapeCounts)
}

private fun rotate3x3Shape(shape: Set<Pos>): Set<Pos> {
    return shape.map { (x, y) ->
        when {
            x == 0 && y == 0 -> Pos(2, 0)
            x == 1 && y == 0 -> Pos(2, 1)
            x == 2 && y == 0 -> Pos(2, 2)
            x == 0 && y == 1 -> Pos(1, 0)
            x == 1 && y == 1 -> Pos(1, 1)
            x == 2 && y == 1 -> Pos(1, 2)
            x == 0 && y == 2 -> Pos(0, 0)
            x == 1 && y == 2 -> Pos(0, 1)
            x == 2 && y == 2 -> Pos(0, 2)
            else -> error("cant rotate $x, $y")
        }
    }.toSet()
}

private fun flip3x3Shape(shape: Set<Pos>): Set<Pos> {
    return shape.map { (x, y) ->
        when {
            x == 0 && y == 0 -> Pos(2, 0)
            x == 1 && y == 0 -> Pos(1, 0)
            x == 2 && y == 0 -> Pos(0, 0)
            x == 0 && y == 1 -> Pos(2, 1)
            x == 1 && y == 1 -> Pos(1, 1)
            x == 2 && y == 1 -> Pos(0, 1)
            x == 0 && y == 2 -> Pos(2, 2)
            x == 1 && y == 2 -> Pos(1, 2)
            x == 2 && y == 2 -> Pos(0, 2)
            else -> error("cant flip $x, $y")
        }
    }.toSet()
}

private fun permutate(shape: Set<Pos>): Set<Set<Pos>> {
    var shape = shape
    var flipped = flip3x3Shape(shape)
    val result = mutableSetOf(shape, flipped)
    repeat(3) {
        shape = rotate3x3Shape(shape)
        flipped = rotate3x3Shape(flipped)
        result += shape
        result += flipped
    }
    return result
}

private data class Pos(val x: Int, val y: Int) {
    operator fun plus(other: Pos) = Pos(x + other.x, y + other.y)
}

private data class Region(val x: Int, val y: Int, val requiredShapeCounts: List<Int>)

private fun List<String>.parseShapesAndRegions(): Pair<List<Set<Pos>>, List<Region>> {
    val parts = splitByEmptyLines()
    val shapes = parts.dropLast(1).map { lines ->
        lines.drop(1).mapIndexed { y, line ->
            line.mapIndexed { x, c -> if (c == '#') Pos(x, y) else null }
        }.flatten().filterNotNull().toSet()
    }
    val regions = parts.last().map { line ->
        val (x, y) = line.substringBefore(":").split("x").map { it.toInt() }
        val requiredShapeCounts = line.substringAfter(": ").split(" ").map { it.toInt() }
        Region(x, y, requiredShapeCounts)
    }
    return shapes to regions
}

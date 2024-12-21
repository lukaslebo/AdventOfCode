package day21

import check
import readInput

fun main() {
    val testInput = readInput("2024", "Day21_test")
    check(part1(testInput), 126384)

    val input = readInput("2024", "Day21")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = complexity(input, depth = 2)
private fun part2(input: List<String>) = complexity(input, depth = 25)

private fun complexity(input: List<String>, depth: Int) = input.sumOf { code ->
    val num = code.removeSuffix("A").toInt()
    val possibleDirectionsOnNumPad = directionsOnNumPad(code)
    val minLength = possibleDirectionsOnNumPad.minOf { directions ->
        minRequiredPressesOnDirPad(directions, depth)
    }
    minLength * num
}

private fun directionsOnNumPad(
    code: String,
    pos: Pos = numPad.getValue('A'),
): Set<String> {
    if (code.isEmpty()) return setOf("")
    val nextPos = numPad.getValue(code.first())
    val possibleMoves = numPad.possibleMoves(pos, nextPos)
    val directions = directionsOnNumPad(code.drop(1), nextPos)
    return possibleMoves.flatMapTo(mutableSetOf()) { possibleMove ->
        directions.map { possibleMove + it }
    }
}

private fun minRequiredPressesOnDirPad(
    directions: String,
    depth: Int,
    cache: MutableMap<CacheKey, Long> = mutableMapOf(),
): Long {
    var total = 0L
    var current = 'A'
    for (key in directions) {
        total += minRequiredPressesOnDirPad(current, key, depth, cache)
        current = key
    }
    return total
}

private fun minRequiredPressesOnDirPad(
    start: Char,
    end: Char,
    depth: Int,
    cache: MutableMap<CacheKey, Long>,
): Long {
    if (depth == 1) {
        return dirPad.possibleMoves(dirPad.getValue(start), dirPad.getValue(end)).minOf { it.length.toLong() }
    }
    val key = CacheKey(
        start = start,
        end = end,
        depth = depth,
    )
    return cache.getOrPut(key) {
        dirPad.possibleMoves(dirPad.getValue(start), dirPad.getValue(end)).minOf { directions ->
            minRequiredPressesOnDirPad(directions, depth - 1, cache)
        }
    }
}

private fun Map<Char, Pos>.possibleMoves(start: Pos, end: Pos): Set<String> {
    val dX = end.x - start.x
    val dY = end.y - start.y
    val moveX = if (dX == 0) "" else if (dX < 0) "<".repeat(-dX) else ">".repeat(dX)
    val moveY = if (dY == 0) "" else if (dY < 0) "v".repeat(-dY) else "^".repeat(dY)
    val directionsPreferringX = moveX + moveY + "A"
    val directionsPreferringY = moveY + moveX + "A"
    return setOfNotNull(
        (directionsPreferringX).takeIf { Pos(end.x, start.y) in values },
        (directionsPreferringY).takeIf { Pos(start.x, end.y) in values },
    )
}

private data class Pos(val x: Int, val y: Int)

private data class CacheKey(
    val start: Char,
    val end: Char,
    val depth: Int,
)

private val numPad = mapOf(
    '0' to Pos(1, 0),
    'A' to Pos(2, 0),
    '1' to Pos(0, 1),
    '2' to Pos(1, 1),
    '3' to Pos(2, 1),
    '4' to Pos(0, 2),
    '5' to Pos(1, 2),
    '6' to Pos(2, 2),
    '7' to Pos(0, 3),
    '8' to Pos(1, 3),
    '9' to Pos(2, 3),
)

private val dirPad = mapOf(
    '<' to Pos(0, 0),
    'v' to Pos(1, 0),
    '>' to Pos(2, 0),
    '^' to Pos(1, 1),
    'A' to Pos(2, 1),
)

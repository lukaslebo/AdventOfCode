package day18

import check
import readInput

fun main() {
    val testInput = readInput("2018", "Day18_test")
    check(part1(testInput), 1147)

    val input = readInput("2018", "Day18")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    var map = input.parseTilesByPos()
    repeat(10) {
        map = map.develop()
    }
    return map.values.count { it == Tile.Tree } * map.values.count { it == Tile.Lumberyard }
}

private fun part2(input: List<String>): Int {
    var map = input.parseTilesByPos()
    val seen = mutableMapOf<Map<Pos, Tile>, Int>()

    var minute = 0

    while (minute < 1_000_000_000) {
        val previous = seen[map]

        if (previous != null) {
            val cycleLength = minute - previous
            val remaining = 1_000_000_000 - minute
            val skip = remaining / cycleLength

            if (skip > 0) {
                minute += skip * cycleLength
                continue
            }
        }

        seen[map] = minute
        map = map.develop()
        minute++
    }

    return map.values.count { it == Tile.Tree } *
            map.values.count { it == Tile.Lumberyard }
}

private data class Pos(val x: Int, val y: Int) {
    operator fun plus(other: Pos) = Pos(x + other.x, y + other.y)

    fun adjacent() = listOf(
        Pos(x = 0, y = -1),
        Pos(x = 1, y = -1),
        Pos(x = 1, y = 0),
        Pos(x = 1, y = 1),
        Pos(x = 0, y = 1),
        Pos(x = -1, y = 1),
        Pos(x = -1, y = 0),
        Pos(x = -1, y = -1),
    ).map { this + it }
}

private enum class Tile(val symbol: Char) {
    Open('.'),
    Tree('|'),
    Lumberyard('#');

    fun develop(adjacent: List<Tile>): Tile {
        return when (this) {
            Open -> if (adjacent.count { it == Tree } >= 3) Tree else Open
            Tree -> if (adjacent.count { it == Lumberyard } >= 3) Lumberyard else Tree
            Lumberyard -> if (adjacent.any { it == Lumberyard } && adjacent.any { it == Tree }) Lumberyard else Open
        }
    }

    companion object {
        fun bySymbol(symbol: Char) = entries.first { it.symbol == symbol }
    }
}

private fun Map<Pos, Tile>.develop() = map { (pos, tile) ->
    val adjacent = pos.adjacent().mapNotNull { this[it] }
    pos to tile.develop(adjacent)
}.toMap()

private fun List<String>.parseTilesByPos(): Map<Pos, Tile> {
    val map = mutableMapOf<Pos, Tile>()
    for ((y, line) in withIndex()) {
        for ((x, c) in line.withIndex()) {
            map += Pos(x, y) to Tile.bySymbol(c)
        }
    }
    return map
}

private fun Map<Pos, Tile>.prettyPrint() {
    val xRange = keys.minOf { it.x }..keys.maxOf { it.x }
    val yRange = keys.minOf { it.y }..keys.maxOf { it.y }
    for (y in yRange) {
        for (x in xRange) {
            print(getValue(Pos(x, y)).symbol)
        }
        println()
    }
    println()
}

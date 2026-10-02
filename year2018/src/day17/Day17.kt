package day17

import check
import readInput

fun main() {
    val testInput = readInput("2018", "Day17_test")
    check(part1(testInput), 57)
    check(part2(testInput), 29)

    val input = readInput("2018", "Day17")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val source = Pos(x = 500, y = 0)
    val blocked = input.parseClayPositions()
    val water = fillWithWater(source, blocked)
    return water.size
}

private fun part2(input: List<String>): Int {
    val source = Pos(x = 500, y = 0)
    val blocked = input.parseClayPositions()
    val water = fillWithWater(source, blocked)
    return water.values.count { it == Tile.Settled }
}

private data class Pos(val x: Int, val y: Int) {
    operator fun plus(other: Pos) = Pos(x + other.x, y + other.y)
    fun up() = Pos(x = x, y = y - 1)
    fun down() = Pos(x = x, y = y + 1)
    fun left() = Pos(x = x - 1, y = y)
    fun right() = Pos(x = x + 1, y = y)
}

private data class Edge(
    val edge: Pos,
    val blocked: Boolean
)

private enum class Tile(val symbol: String) {
    Flowing("|"),
    Settled("~"),
}

private fun List<String>.parseClayPositions() = flatMap { line ->
    val (c, start, end) = line.split("(=|,|[.]{2})".toRegex()).mapNotNull { it.toIntOrNull() }
    (start..end).map { if (line.startsWith("x")) Pos(x = c, y = it) else Pos(x = it, y = c) }
}.toSet()

private fun fillWithWater(source: Pos, blocked: Set<Pos>): Map<Pos, Tile> {
    val yRange = 0..blocked.maxOf { it.y }

    val water = mutableMapOf<Pos, Tile>()
    val sources = ArrayDeque<Pos>()
    sources += source

    fun isBlocked(pos: Pos): Boolean =
        pos in blocked || water[pos] == Tile.Settled

    fun findEdge(start: Pos, dir: Pos.() -> Pos): Edge {
        var current = start
        var hasSupport = isBlocked(current.down())
        while (hasSupport) {
            val next = current.dir()
            if (isBlocked(next)) return Edge(current, true)
            hasSupport = isBlocked(next.down())
            current = next
        }
        return Edge(current, false)
    }

    while (sources.isNotEmpty()) {
        val source = sources.removeFirst()

        var pos = source
        while (pos.y in yRange && !isBlocked(pos)) {
            water[pos] = Tile.Flowing
            pos = pos.down()
        }

        if (pos.y !in yRange) continue
        pos = pos.up()

        while (pos.y in yRange) {
            val left = findEdge(start = pos, dir = Pos::left)
            val right = findEdge(start = pos, dir = Pos::right)
            val tile = if (left.blocked && right.blocked) Tile.Settled else Tile.Flowing
            for (x in left.edge.x..right.edge.x) {
                water[Pos(x, pos.y)] = tile
            }

            if (!left.blocked || !right.blocked) {
                if (!left.blocked) sources += left.edge
                if (!right.blocked) sources += right.edge
                break
            }
            pos = pos.up()
        }
    }
    water.remove(source)
    return water
}

private fun printMap(water: Map<Pos, Tile>, blocked: Set<Pos>) {
    val source = Pos(x = 500, y = 0)
    val yRange = 0..blocked.maxOf { it.y }
    val xRange = blocked.minOf { it.x } - 1..blocked.maxOf { it.x } + 1
    for (y in yRange) {
        for (x in xRange) {
            val pos = Pos(x, y)
            val tile = water[pos]
            when (pos) {
                source -> print("+")
                in blocked -> print("#")
                else -> print(tile?.symbol ?: ".")
            }
        }
        println()
    }
    println()
}

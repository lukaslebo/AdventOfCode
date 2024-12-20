package day20

import check
import readInput
import kotlin.math.abs

fun main() {
    val testInput = readInput("2024", "Day20_test")
    check(part1(testInput, minTimeSaving = 1), 44)
    check(part2(testInput, minTimeSaving = 50), 285)

    val input = readInput("2024", "Day20")
    println(part1(input, minTimeSaving = 100))
    println(part2(input, minTimeSaving = 100))
}

private var debug = false

private fun part1(input: List<String>, minTimeSaving: Int) = input.parseRace()
    .countCheats(minTimeSaving = minTimeSaving, maxCheatTime = 2)

private fun part2(input: List<String>, minTimeSaving: Int) = input.parseRace()
    .countCheats(minTimeSaving = minTimeSaving, maxCheatTime = 20)

private data class Pos(val x: Int, val y: Int) {
    val adjacent
        get() = setOf(
            Pos(x, y + 1),
            Pos(x + 1, y),
            Pos(x, y - 1),
            Pos(x - 1, y),
        )

    fun manhattanDistanceTo(other: Pos) = abs(x - other.x) + abs(y - other.y)
}

private data class Race(
    val tiles: Set<Pos>,
    val start: Pos,
    val end: Pos,
)

private fun List<String>.parseRace(): Race {
    val tiles = mutableSetOf<Pos>()
    var start: Pos? = null
    var end: Pos? = null
    for ((y, line) in withIndex()) {
        for ((x, c) in line.withIndex()) {
            when (c) {
                'S' -> start = Pos(x, y)
                'E' -> end = Pos(x, y)
                '.' -> tiles += Pos(x, y)
            }
        }
    }
    return Race(
        tiles = tiles + start!! + end!!,
        start = start,
        end = end,
    )
}

private fun Race.countCheats(minTimeSaving: Int, maxCheatTime: Int): Int {
    val timeByTileFromStart = timeByTileFrom(start)
    val timeByTileToEnd = timeByTileFrom(end)

    val timeWithoutCheats = timeByTileToEnd[start]!!

    val timeSavings = mutableListOf<Int>()
    for (tileA in tiles) {
        for (tileB in tiles) {
            val requiredCheatTime = tileA.manhattanDistanceTo(tileB)
            if (requiredCheatTime > maxCheatTime) continue

            val timeToTile = timeByTileFromStart[tileA] ?: continue
            val timeToEnd = timeByTileToEnd[tileB] ?: continue
            val totalTime = timeToTile + requiredCheatTime + timeToEnd

            val timeSaving = timeWithoutCheats - totalTime
            if (timeSaving >= minTimeSaving) timeSavings += timeSaving
        }
    }
    if (debug) println(timeSavings.debugStatements())
    return timeSavings.size
}

private fun Race.timeByTileFrom(start: Pos): Map<Pos, Int> {
    val timeByTileFrom = mutableMapOf<Pos, Int>()
    val queue = ArrayDeque<Pair<Pos, Int>>()
    queue += start to 0
    while (queue.isNotEmpty()) {
        val (pos, time) = queue.removeFirst()
        if (pos in timeByTileFrom) continue
        timeByTileFrom[pos] = time
        queue += pos.adjacent.filter { it in tiles }.map { it to time + 1 }
    }
    return timeByTileFrom
}

private fun List<Int>.debugStatements() = groupingBy { it }
    .eachCount()
    .entries
    .sortedBy { it.key }
    .joinToString(separator = "\n", postfix = "\n") {
        if (it.value == 1) "There is one cheat that saves ${it.key} picoseconds."
        else "There are ${it.value} cheats that save ${it.key} picoseconds."
    }

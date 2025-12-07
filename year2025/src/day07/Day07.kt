package day07

import check
import readInput

fun main() {
    val testInput = readInput("2025", "Day07_test")
    check(part1(testInput), 21)
    check(part2(testInput), 40)

    val input = readInput("2025", "Day07")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.parseTachyonMap().launchBeam().splitCount
private fun part2(input: List<String>) = input.parseTachyonMap().launchBeam().timelines

private fun TachyonMap.launchBeam(): LaunchedBeam {
    val beams = mutableMapOf(start to 1L)
    val maxY = emptySpace.maxOf { it.y }
    var splitCount = 0
    var y = start.y
    while (y < maxY) {
        for ((beam, count) in beams.filter { it.key.y == y }) {
            val nextBeam = beam + Pos.down
            if (nextBeam in splitters) {
                splitCount++
                beams.merge(nextBeam + Pos.right, count, Long::plus)
                beams.merge(nextBeam + Pos.left, count, Long::plus)
            } else {
                beams.merge(nextBeam, count, Long::plus)
            }
        }
        y++
    }
    return LaunchedBeam(
        beams = beams,
        splitCount = splitCount,
        timelines = beams.filter { it.key.y == maxY }.values.sum(),
    )
}

private data class TachyonMap(
    val start: Pos,
    val splitters: Set<Pos>,
    val emptySpace: Set<Pos>,
)

private data class LaunchedBeam(
    val beams: Map<Pos, Long>,
    val splitCount: Int,
    val timelines: Long,
)

private data class Pos(val x: Int, val y: Int) {
    companion object {
        val down = Pos(0, 1)
        val left = Pos(-1, 0)
        val right = Pos(1, 0)
    }

    operator fun plus(other: Pos) = Pos(x = x + other.x, y = y + other.y)
}

private fun List<String>.parseTachyonMap(): TachyonMap {
    var start = Pos(0, 0)
    val splitters = mutableSetOf<Pos>()
    val emptySpace = mutableSetOf<Pos>()
    for ((y, line) in withIndex()) {
        for ((x, c) in line.withIndex()) {
            val pos = Pos(x, y)
            when (c) {
                '^' -> splitters += pos
                '.' -> emptySpace += pos
                'S' -> start = pos
            }
        }
    }
    return TachyonMap(
        start = start,
        splitters = splitters,
        emptySpace = emptySpace + start,
    )
}

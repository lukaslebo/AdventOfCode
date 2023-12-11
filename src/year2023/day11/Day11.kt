package year2023.day11

import check
import readInput

fun main() {
    val testInput1 = readInput("2023", "Day11_test")
    check(part1(testInput1), 374)
    check(part2(testInput1, expansionFactor = 10), 1030)
    check(part2(testInput1, expansionFactor = 100), 8410)

    val input = readInput("2023", "Day11")
    println(part1(input))
    println(part2(input, expansionFactor = 1_000_000))
}

private fun part1(input: List<String>): Long {
    val (emptyRows, emptyCols) = input.findEmptyRowsAndCols()
    return input
        .findAllGalaxies()
        .createPairs()
        .sumOf {
            it.first.distanceTo(
                other = it.second,
                emptyRows = emptyRows,
                emptyCols = emptyCols,
                expansionFactor = 2,
            )
        }
}

private fun part2(input: List<String>, expansionFactor: Int): Long {
    val (emptyRows, emptyCols) = input.findEmptyRowsAndCols()
    return input
        .findAllGalaxies()
        .createPairs()
        .sumOf {
            it.first.distanceTo(
                other = it.second,
                emptyRows = emptyRows,
                emptyCols = emptyCols,
                expansionFactor = expansionFactor,
            )
        }
}

private data class Pos(val x: Int, val y: Int)

private fun List<String>.findAllGalaxies(): Set<Pos> {
    val galaxies = mutableSetOf<Pos>()
    for ((y, line) in withIndex()) {
        for ((x, c) in line.withIndex()) {
            if (c == '#') {
                galaxies += Pos(x, y)
            }
        }
    }
    return galaxies
}

private fun Set<Pos>.createPairs(): Set<Pair<Pos, Pos>> {
    val pairs = mutableSetOf<Set<Pos>>()
    for (a in this) {
        for (b in this) {
            if (a != b) {
                pairs += setOf(a, b)
            }
        }
    }
    return pairs.map { Pair(it.first(), it.last()) }.toSet()
}

private fun Pos.distanceTo(other: Pos, emptyRows: List<Int>, emptyCols: List<Int>, expansionFactor: Int): Long {
    val xRange = x.coerceAtMost(other.x)..x.coerceAtLeast(other.x)
    val yRange = y.coerceAtMost(other.y)..y.coerceAtLeast(other.y)
    val yExpansion = emptyRows.count { it in yRange }
    val xExpansion = emptyCols.count { it in xRange }
    return (xRange.last - xRange.first) +
            (yRange.last - yRange.first) +
            xExpansion * (expansionFactor - 1L) +
            yExpansion * (expansionFactor - 1L)
}

private fun List<String>.findEmptyRowsAndCols(): Pair<List<Int>, List<Int>> {
    val emptyRows = mutableListOf<Int>()
    val emptyCols = mutableListOf<Int>()
    for ((y, line) in withIndex()) {
        if (line.all { it == '.' }) {
            emptyRows += y
        }
    }
    for (x in first().indices) {
        if (all { it[x] == '.' }) {
            emptyCols += x
        }
    }
    return emptyRows to emptyCols
}

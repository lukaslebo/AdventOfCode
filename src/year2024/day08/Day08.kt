package year2024.day08

import check
import readInput

fun main() {
    val testInput = readInput("2024", "Day08_test")
    check(part1(testInput), 14)
    check(part2(testInput), 34)

    val input = readInput("2024", "Day08")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.parseAntennaMap().findUniqueAntinodes(maxAntinodesInDirection = 1).size

private fun part2(input: List<String>) = input.parseAntennaMap().findUniqueAntinodes().size

private data class Pos(val x: Int, val y: Int) {
    operator fun plus(other: Pos) = Pos(x + other.x, y + other.y)
    operator fun minus(other: Pos) = Pos(x - other.x, y - other.y)
}

private data class AntennaMap(
    val xRange: IntRange,
    val yRange: IntRange,
    val antennasByFrequency: Map<Char, List<Pos>>,
)

private fun List<String>.parseAntennaMap(): AntennaMap {
    val antennasByFrequency = mutableMapOf<Char, MutableList<Pos>>()
    for ((y, line) in withIndex()) {
        for ((x, c) in line.withIndex()) {
            if (c == '.') continue
            antennasByFrequency.getOrPut(c) { mutableListOf() } += Pos(x, y)
        }
    }

    return AntennaMap(
        xRange = first().indices,
        yRange = indices,
        antennasByFrequency = antennasByFrequency,
    )
}

private fun AntennaMap.findUniqueAntinodes(maxAntinodesInDirection: Int = Int.MAX_VALUE): Set<Pos> {
    val includeAntenna = maxAntinodesInDirection > 1
    val antinodes = mutableSetOf<Pos>()
    for (antennasOnSameFrequency in antennasByFrequency.values) {
        for (antennaA in antennasOnSameFrequency) {
            for (antennaB in antennasOnSameFrequency) {
                if (antennaA == antennaB) continue
                val distance = antennaA - antennaB
                if (includeAntenna) antinodes += antennaA
                var antinode = antennaA + distance
                var nodesInDirection = 1
                while (
                    antinode.x in xRange &&
                    antinode.y in yRange &&
                    nodesInDirection <= maxAntinodesInDirection
                ) {
                    antinodes += antinode
                    antinode += distance
                    nodesInDirection++
                }
            }
        }
    }
    return antinodes
}

package day06

import check
import readInput
import kotlin.math.abs

fun main() {
    val testInput = readInput("2018", "Day06_test")
    check(part1(testInput), 17)
    check(part2(testInput), 16)

    val input = readInput("2018", "Day06")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val posList = input.parsePositions()
    val (xRange, yRange) = posList.ranges()
    val indexToArea = mutableMapOf<Int, MutableSet<Pos>>()
    val infiniteIndex = mutableSetOf<Int>()
    for (x in xRange) {
        for (y in yRange) {
            val pos = Pos(x, y)
            val distanceAndIndex = posList.withIndex().map { pos.manhattanDistanceTo(it.value) to it.index }
            val minDistance = distanceAndIndex.minOf { it.first }
            val index = distanceAndIndex.singleOrNull { it.first == minDistance }?.second ?: continue
            indexToArea.getOrPut(index) { mutableSetOf() } += pos
            if (x == xRange.first || x == xRange.last || y == yRange.first || y == yRange.last) {
                infiniteIndex += index
            }
        }
    }
    return indexToArea.filter { it.key !in infiniteIndex }.maxOf { it.value.size }
}

private fun part2(input: List<String>): Int {
    val safeDistance = if (input.size < 7) 31 else 9999
    val posList = input.parsePositions()
    val (xRange, yRange) = posList.ranges()
    val safePositions = mutableSetOf<Pos>()
    for (x in xRange) {
        for (y in yRange) {
            val pos = Pos(x, y)
            val totalDistance = posList.sumOf { it.manhattanDistanceTo(pos) }
            if (totalDistance > safeDistance) continue
            safePositions += pos
        }
    }
    return safePositions.size
}

private data class Pos(val x: Int, val y: Int) {
    fun manhattanDistanceTo(other: Pos) = abs(x - other.x) + abs(y - other.y)
}

private fun List<String>.parsePositions() = map { line ->
    val (x, y) = line.split(", ").map { it.toInt() }
    Pos(x, y)
}

private fun List<Pos>.ranges() = minOf { it.x }..maxOf { it.x } to minOf { it.y }..maxOf { it.y }

package day08

import check
import readInput
import kotlin.math.pow
import kotlin.math.sqrt

fun main() {
    val testInput = readInput("2025", "Day08_test")
    check(part1(testInput, n = 10), 40)
    check(part2(testInput), 25272)

    val input = readInput("2025", "Day08")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>, n: Int = 1000): Int {
    val positions = input.parsePositions()
    val connections = positions.makeConnectionsSortedByDistance().take(n)
    val groups = connections.toGroups()
    return groups.map { it.size }.sortedDescending().take(3).reduce(Int::times)
}

private fun part2(input: List<String>): Long {
    val positions = input.parsePositions()
    val connections = positions.makeConnectionsSortedByDistance()
    val (a, b) = connections.takeUntilOneGroup().last()
    return a.x * b.x
}

private data class Pos(
    val x: Long,
    val y: Long,
    val z: Long,
) {
    fun distanceTo(other: Pos) = sqrt(
        (x - other.x).toDouble().pow(2) +
                (y - other.y).toDouble().pow(2) +
                (z - other.z).toDouble().pow(2)
    )
}

private fun List<String>.parsePositions() = map { line ->
    val (x, y, z) = line.split(",").map { it.toLong() }
    Pos(x, y, z)
}

private fun List<Pos>.makeConnectionsSortedByDistance(): List<Pair<Pos, Pos>> {
    val result = mutableSetOf<Set<Pos>>()
    for (a in this) {
        for (b in this) {
            if (a == b) continue
            result += setOf(a, b)
        }
    }
    return result.map { it.first() to it.last() }.sortedBy { it.first.distanceTo(it.second) }
}

private fun List<Pair<Pos, Pos>>.toGroups(): List<Set<Pos>> {
    val groups = mutableListOf<Set<Pos>>()
    for ((a, b) in this) {
        val existingGroups = groups.filter { a in it || b in it }
        groups -= existingGroups.toSet()
        groups += (existingGroups.flatten() + a + b).toSet()
    }
    return groups
}

private fun List<Pair<Pos, Pos>>.takeUntilOneGroup(): List<Pair<Pos, Pos>> {
    val requiredSize = flatMap { setOf(it.first, it.second) }.toSet().size
    val groups = mutableListOf<Set<Pos>>()
    var i = 0
    do {
        val (a, b) = get(i++)
        val existingGroups = groups.filter { a in it || b in it }.toSet()
        groups -= existingGroups
        groups += (existingGroups.flatten() + a + b).toSet()
    } while (groups.first().size < requiredSize)
    return take(i)
}

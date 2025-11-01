package day14

import check
import day10.createKnotHash
import readInput

fun main() {
    val testInput = readInput("2017", "Day14_test")
    check(part1(testInput), 8108)
    check(part2(testInput), 1242)

    val input = readInput("2017", "Day14")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val key = input.first()
    val grid = createGrid(key)
    return grid.sumOf { row -> row.count { it } }
}

private fun part2(input: List<String>): Int {
    val key = input.first()
    val grid = createGrid(key)
    val regions = mutableSetOf<Set<Pos>>()
    val posToCheck = grid.indices.flatMap { y -> grid.first().indices.map { x -> Pos(x, y) } }
        .filter { grid[it.y][it.x] }
        .toMutableSet()
    while (posToCheck.isNotEmpty()) {
        val next = posToCheck.first()
        val region = grid.fillFrom(next)
        posToCheck -= region
        regions += region
    }
    return regions.size
}

private fun createGrid(key: String) = (0..127).map { row ->
    val knotHash = createKnotHash("$key-$row")
    knotHash
        .map { it.toString().hexToInt().toString(2).padStart(4, '0') }
        .joinToString("")
        .map { it != '0' }
}

private data class Pos(val x: Int, val y: Int) {
    companion object {
        val up = Pos(0, -1)
        val down = Pos(0, 1)
        val left = Pos(-1, 0)
        val right = Pos(1, 0)

        val directions = setOf(up, down, left, right)
    }

    operator fun plus(other: Pos) = Pos(x + other.x, y + other.y)
}

private fun List<List<Boolean>>.fillFrom(start: Pos): Set<Pos> {
    val region = mutableSetOf<Pos>()
    val queue = ArrayDeque<Pos>()
    queue += start
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        region += current
        queue += Pos.directions.map { current + it }
            .filter { it.y in indices && it.x in first().indices && this[it.y][it.x] && it !in region }
    }
    return region
}

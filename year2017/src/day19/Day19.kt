package day19

import check
import readInput

fun main() {
    val testInput = readInput("2017", "Day19_test")
    check(part1(testInput), "ABCDEF")
    check(part2(testInput), 38)

    val input = readInput("2017", "Day19")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): String {
    val path = walkPath(input)
    return path.map { input[it] }.filter { it.category == CharCategory.UPPERCASE_LETTER }.joinToString(separator = "")
}

private fun part2(input: List<String>) = walkPath(input).size

private fun walkPath(input: List<String>): List<Pos> {
    val start = Pos(x = input.first().indexOfFirst { it != ' ' }, y = 0)
    var dir = Pos.down

    val path = mutableListOf<Pos>()
    var current = start
    while (current in input) {
        path += current

        dir = listOf(dir, Pos.up, Pos.down, Pos.right, Pos.left).distinct().firstOrNull {
            (current + it) in input && input[current + it] != ' ' && it + dir != Pos(x = 0, y = 0)
        } ?: break

        current += dir
    }

    return path
}

private data class Pos(val x: Int, val y: Int) {
    companion object {
        val up = Pos(0, -1)
        val down = Pos(0, 1)
        val left = Pos(-1, 0)
        val right = Pos(1, 0)
    }

    operator fun plus(other: Pos) = Pos(x = x + other.x, y = y + other.y)
}

private operator fun List<String>.contains(pos: Pos) = pos.y in indices && pos.x in this[pos.y].indices
private operator fun List<String>.get(pos: Pos) = this[pos.y][pos.x]

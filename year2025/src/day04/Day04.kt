package day04

import check
import readInput

fun main() {
    val testInput = readInput("2025", "Day04_test")
    check(part1(testInput), 13)
    check(part2(testInput), 43)

    val input = readInput("2025", "Day04")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val paperRolls = input.parsePaperRollsPositions()
    return paperRolls.count { paperRolls.isRemovable(it) }
}

private fun part2(input: List<String>): Int {
    val paperRolls = input.parsePaperRollsPositions()
    val removedRolls = mutableSetOf<Pos>()

    do {
        val removableRolls = paperRolls.filter { paperRolls.isRemovable(it) }.toSet()
        removedRolls += removableRolls
        paperRolls -= removableRolls
    } while (removableRolls.isNotEmpty())

    return removedRolls.size
}

private fun Set<Pos>.isRemovable(paperRoll: Pos) =
    Pos.directionsWithDiagonals.map { it + paperRoll }.filter { it in this }.size < 4

private data class Pos(val x: Int, val y: Int) {
    companion object {
        val up = Pos(0, -1)
        val down = Pos(0, 1)
        val left = Pos(-1, 0)
        val right = Pos(1, 0)

        val directionsWithDiagonals = listOf(up, up + right, right, right + down, down, down + left, left, left + up)
    }

    operator fun plus(other: Pos) = Pos(x = x + other.x, y = y + other.y)
}

private fun List<String>.parsePaperRollsPositions(): MutableSet<Pos> {
    val paperRolls = mutableSetOf<Pos>()
    for ((y, line) in withIndex()) {
        for ((x, c) in line.withIndex()) {
            if (c == '@') paperRolls += Pos(x = x, y = y)
        }
    }
    return paperRolls
}

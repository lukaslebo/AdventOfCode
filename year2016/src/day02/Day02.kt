package day02

import check
import readInput

fun main() {
    val testInput = readInput("2016", "Day02_test")
    check(part1(testInput), 1985)
    check(part2(testInput), "5DB3")

    val input = readInput("2016", "Day02")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    var pos = Pos(1, 1)
    return input.joinToString("") { moves ->
        for (dir in moves.chunked(1).map(Dir::valueOf)) {
            pos = pos.move(dir).constrainIn(0..2)
        }
        numPad[pos]
    }.toInt()
}

private fun part2(input: List<String>): String {
    var pos = Pos(0, 2)
    return input.joinToString("") { moves ->
        for (dir in moves.chunked(1).map(Dir::valueOf)) {
            val nextPos = pos.move(dir)
            if (crazyNumPad[nextPos] != null) {
                pos = nextPos
            }
        }
        requireNotNull(crazyNumPad[pos])
    }
}

private data class Pos(val x: Int, val y: Int) {
    fun move(dir: Dir): Pos = when (dir) {
        Dir.U -> Pos(x, y - 1)
        Dir.D -> Pos(x, y + 1)
        Dir.L -> Pos(x - 1, y)
        Dir.R -> Pos(x + 1, y)
    }

    fun constrainIn(range: IntRange): Pos {
        return Pos(x.coerceIn(range), y.coerceIn(range))
    }
}

private enum class Dir {
    U, D, L, R
}

private val numPad = listOf(
    listOf("1", "2", "3"),
    listOf("4", "5", "6"),
    listOf("7", "8", "9"),
)

private val crazyNumPad = listOf(
    listOf(null, null, "1", null, null),
    listOf(null, "2", "3", "4", null),
    listOf("5", "6", "7", "8", "9"),
    listOf(null, "A", "B", "C", null),
    listOf(null, null, "D", null, null),
)

private operator fun List<List<String>>.get(pos: Pos) = this[pos.y][pos.x]

@JvmName("nullableGet")
private operator fun List<List<String?>>.get(pos: Pos) = getOrNull(pos.y)?.getOrNull(pos.x)

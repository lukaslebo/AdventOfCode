package day01

import check
import readInput
import kotlin.math.abs

fun main() {
    check(part1(listOf("R2, L3")), 5)
    check(part1(listOf("R2, R2, R2")), 2)
    check(part1(listOf("R5, L5, R5, R3")), 12)
    check(part2(listOf("R8, R4, R4, R8")), 4)

    val input = readInput("2016", "Day01")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    var dir = Dir.U
    val path = mutableListOf(Pos(0, 0))

    for ((turn, steps) in input.parseInstructions()) {
        dir = dir.turn(turn)
        path += path.last().move(dir, steps)
    }

    return abs(path.last().x) + abs(path.last().y)
}

private fun part2(input: List<String>): Int {
    var dir = Dir.U
    val path = mutableListOf(Pos(0, 0))

    for ((turn, steps) in input.parseInstructions()) {
        dir = dir.turn(turn)
        path += path.last().move(dir, steps)
    }

    return path.groupBy { it }.entries.first { it.value.size > 1 }.key.let { (x, y) -> abs(x) + abs(y) }
}

private fun List<String>.parseInstructions() = first().split(", ").map { instruction ->
    val turn = Dir.valueOf(instruction.first().toString())
    val steps = instruction.drop(1).toInt()
    turn to steps
}

private enum class Dir {
    U, D, L, R;

    fun turn(dir: Dir): Dir {
        if (dir == L) {
            return when (this) {
                U -> L
                L -> D
                D -> R
                R -> U
            }
        }
        if (dir == R) {
            return when (this) {
                U -> R
                R -> D
                D -> L
                L -> U
            }
        }
        error("Cannot turn to $dir")
    }
}

private data class Pos(val x: Int, val y: Int) {
    fun move(dir: Dir): Pos {
        return when (dir) {
            Dir.U -> copy(y = y + 1)
            Dir.D -> copy(y = y - 1)
            Dir.L -> copy(x = x + 1)
            Dir.R -> copy(x = x - 1)
        }
    }

    fun move(dir: Dir, steps: Int): List<Pos> {
        val path = mutableListOf<Pos>()
        repeat(steps) {
            path += (path.lastOrNull() ?: this).move(dir)
        }
        return path
    }
}

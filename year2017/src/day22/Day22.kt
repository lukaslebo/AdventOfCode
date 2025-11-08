package day22

import check
import readInput

fun main() {
    val testInput = readInput("2017", "Day22_test")
    check(part1(testInput), 5587)
    check(part2(testInput), 2511944)

    val input = readInput("2017", "Day22")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val infected = input.parseInfectedNodePositions().toMutableSet()
    var virusPos = input.centerPos()
    var virusDir = Dir.Up
    var infections = 0

    repeat(10_000) {
        if (virusPos in infected) {
            virusDir = virusDir.turnRight()
            infected -= virusPos
        } else {
            virusDir = virusDir.turnLeft()
            infected += virusPos
            infections++
        }
        virusPos = virusPos.move(virusDir)
    }

    return infections
}

private fun part2(input: List<String>): Int {
    val infected = input.parseInfectedNodePositions().toMutableSet()
    val weakened = mutableSetOf<Pos>()
    val flagged = mutableSetOf<Pos>()
    var virusPos = input.centerPos()
    var virusDir = Dir.Up
    var infections = 0

    repeat(10_000_000) {
        when (virusPos) {
            in infected -> {
                infected -= virusPos
                flagged += virusPos
                virusDir = virusDir.turnRight()
            }

            in weakened -> {
                weakened -= virusPos
                infected += virusPos
                infections++
            }

            in flagged -> {
                flagged -= virusPos
                virusDir = virusDir.turnLeft().turnLeft()
            }

            else -> {
                weakened += virusPos
                virusDir = virusDir.turnLeft()
            }
        }
        virusPos = virusPos.move(virusDir)
    }

    return infections
}

private data class Pos(val x: Int, val y: Int) {
    fun move(dir: Dir): Pos = when (dir) {
        Dir.Up -> Pos(x, y - 1)
        Dir.Down -> Pos(x, y + 1)
        Dir.Left -> Pos(x - 1, y)
        Dir.Right -> Pos(x + 1, y)
    }

    operator fun plus(other: Pos) = Pos(x = x + other.x, y = y + other.y)
}

private enum class Dir {
    Up, Down, Left, Right;

    fun turnRight() = when (this) {
        Up -> Right
        Down -> Left
        Left -> Up
        Right -> Down
    }

    fun turnLeft() = when (this) {
        Up -> Left
        Down -> Right
        Left -> Down
        Right -> Up
    }
}

private fun List<String>.parseInfectedNodePositions() = flatMapIndexed { y, line ->
    line.mapIndexedNotNull { x, c ->
        if (c == '#') Pos(x, y) else null
    }
}

private fun List<String>.centerPos() = Pos(size / 2, first().length / 2)

package day03

import check
import readInput
import kotlin.math.absoluteValue

fun main() {
    val testInput = readInput("2017", "Day03_test")
    check(part1(testInput), 31)

    val input = readInput("2017", "Day03")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = createMemorySpiral(input.first().toInt(), SpiralMode.Sequence)
    .keys.last().let { it.x.absoluteValue + it.y.absoluteValue }

private fun part2(input: List<String>) = createMemorySpiral(input.first().toInt(), SpiralMode.SumOfAdjacent)
    .values.max()

private data class Pos(val x: Int, val y: Int) {
    companion object {
        val up = Pos(0, -1)
        val down = Pos(0, 1)
        val left = Pos(-1, 0)
        val right = Pos(1, 0)

        private val adjacentDirs = listOf(up, up + right, right, right + down, down, down + left, left, left + up)
        val spiralDirs = listOf(Pos.right, Pos.up, Pos.left, Pos.down)
    }

    fun adjacent() = adjacentDirs.map { this + it }

    operator fun plus(other: Pos) = Pos(x + other.x, y + other.y)
}

private enum class SpiralMode {
    Sequence, SumOfAdjacent
}

private fun createMemorySpiral(memory: Int, mode: SpiralMode): Map<Pos, Int> {
    var current = Pos(0, 0)
    var num = 1
    var dir = 0
    val numsByPos = mutableMapOf(current to num)
    while (num < memory) {
        current += Pos.spiralDirs[dir]
        num = when (mode) {
            SpiralMode.Sequence -> num + 1
            SpiralMode.SumOfAdjacent -> current.adjacent().sumOf { numsByPos[it] ?: 0 }
        }
        numsByPos += current to num
        val nextDir = (dir + 1) % 4
        val nextDirPos = current + Pos.spiralDirs[nextDir]
        if (nextDirPos !in numsByPos) {
            dir = nextDir
        }
    }
    return numsByPos
}

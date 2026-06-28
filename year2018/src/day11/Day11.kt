package day11

import check
import readInput
import kotlin.math.max

fun main() {
    val testInput = readInput("2018", "Day11_test")
    check(part1(testInput), "21,61")
    check(part2(testInput), "232,251,12")

    val input = readInput("2018", "Day11")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): String {
    val grid = makeGrid(serialNumber = input.first().toInt(), size = 300)
    return grid.findSubgridWithMaxPowerLevel(minSize = 3, maxSize = 3).format(includeSize = false)
}

private fun part2(input: List<String>): String {
    val grid = makeGrid(serialNumber = input.first().toInt(), size = 300)
    return grid.findSubgridWithMaxPowerLevel(minSize = 1, maxSize = 300).format(includeSize = true)
}

private fun List<List<Int>>.findSubgridWithMaxPowerLevel(minSize: Int, maxSize: Int): Result {
    var max = Result(0, 0, 0, 0)
    for (y in 1..size - minSize) {
        for (x in 1..size - minSize) {
            var power = 0
            var subgridSize = 0

            fun incrementSubgrid() {
                for (dy in 0 until subgridSize) {
                    power += this[y + dy - 1][x + subgridSize - 1]
                }
                for (dx in 0..subgridSize) {
                    power += this[y + subgridSize - 1][x + dx - 1]
                }
                subgridSize++
            }

            val limitedMaxSize = maxSize.coerceAtMost(300 - max(x, y) + 1)
            while (subgridSize < limitedMaxSize) {
                incrementSubgrid()
                if (subgridSize >= minSize && power > max.power) {
                    max = Result(x, y, subgridSize, power)
                }
            }
        }
    }
    return max
}

private data class Result(
    val x: Int,
    val y: Int,
    val size: Int,
    val power: Int,
) {
    fun format(includeSize: Boolean) = if (includeSize) "$x,$y,$size" else "$x,$y"
}

private fun makeGrid(serialNumber: Int, size: Int): List<List<Int>> {
    val grid = mutableListOf<List<Int>>()
    for (y in 1..size) {
        grid += (1..size).map { x -> powerLevel(x, y, serialNumber) }
    }
    return grid
}

private fun powerLevel(x: Int, y: Int, serialNumber: Int): Int {
    val rackId = x + 10
    val power = (rackId * y + serialNumber) * rackId
    val powerLevel = ((power / 100) % 10)
    return powerLevel - 5
}

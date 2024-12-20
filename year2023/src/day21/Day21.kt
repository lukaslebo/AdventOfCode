package day21

import check
import readInput

fun main() {
    val testInput = readInput("2023", "Day21_test")
    check(part1(testInput, 6), 16)

    val input = readInput("2023", "Day21")
    println(part1(input, 64))
    println(part2(input))
}

private fun part1(input: List<String>, steps: Int) = input.parseGarden().possibleLocationsAfterSteps(steps)

private fun part2(input: List<String>, steps: Long = 26501365): Long {
    val garden = input.parseGarden()
    val gardenWidth = input.size
    val x1 = gardenWidth / 2
    val x2 = gardenWidth / 2 + gardenWidth
    val x3 = gardenWidth / 2 + gardenWidth * 2
    val y1 = garden.possibleLocationsAfterSteps(x1)
    val y2 = garden.possibleLocationsAfterSteps(x2)
    val y3 = garden.possibleLocationsAfterSteps(x3)
    val x1Norm = x1 / gardenWidth
    val x2Norm = x2 / gardenWidth
    val x3Norm = x3 / gardenWidth
    val a = (y1 * (x3Norm - x2Norm) + y2 * (x1Norm - x3Norm) + y3 * (x2Norm - x1Norm)) /
            ((x1Norm - x2Norm) * (x2Norm - x3Norm) * (x3Norm - x1Norm))
    val b = (a * (x2Norm * x2Norm - x1Norm * x1Norm) + y1 - y2) / (x1Norm - x2Norm)
    val c = y1 - a * x1Norm * x1Norm - b * x1Norm
    val xNorm = steps / gardenWidth
    return (a * xNorm * xNorm) + (b * xNorm) + c
}

private data class Pos(val x: Int, val y: Int) {
    fun up() = Pos(x, y - 1)
    fun down() = Pos(x, y + 1)
    fun left() = Pos(x - 1, y)
    fun right() = Pos(x + 1, y)

    fun neighbours() = listOf(up(), down(), left(), right())

    fun constrainInRange(xRange: IntRange, yRange: IntRange): Pos {
        val xSize = xRange.last - xRange.first + 1
        val ySize = yRange.last - yRange.first + 1
        val newX = ((x % xSize) + xSize) % xSize
        val newY = ((y % ySize) + ySize) % ySize
        return Pos(newX, newY)
    }
}

private data class Garden(
    val start: Pos,
    val stones: Set<Pos>,
    val xRange: IntRange,
    val yRange: IntRange,
)

private fun List<String>.parseGarden(): Garden {
    var start = Pos(0, 0)
    val stones = mutableSetOf<Pos>()
    for ((y, line) in withIndex()) {
        for ((x, c) in line.withIndex()) {
            if (c == '#') {
                stones += Pos(x, y)
            }
            if (c == 'S') {
                start = Pos(x, y)
            }
        }
    }
    return Garden(start, stones, indices, first().indices)
}

private fun Garden.possibleLocationsAfterSteps(steps: Int): Int {
    val locations = mutableSetOf<Pos>()
    val visited = mutableSetOf<Pos>()
    val stack = ArrayDeque<Pair<Pos, Int>>()
    stack += start to steps
    while (stack.isNotEmpty()) {
        val (pos, remainingSteps) = stack.removeFirst()
        // Only when there is a even number of remaining steps we can return to this tile
        if (remainingSteps % 2 == 0) locations += pos
        if (remainingSteps == 0) continue
        val neighbours = pos.neighbours().filter { it.constrainInRange(xRange, yRange) !in stones && it !in visited }
        for (next in neighbours) {
            visited += next
            stack += next to remainingSteps - 1
        }
    }
    return locations.size
}

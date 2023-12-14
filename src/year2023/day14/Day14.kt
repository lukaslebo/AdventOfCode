package year2023.day14

import check
import readInput

fun main() {
    val testInput = readInput("2023", "Day14_test")
    check(part1(testInput), 136)
    check(part2(testInput), 64)

    val input = readInput("2023", "Day14")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.parseDish().tilt(Tilt.North).beamLoad()

private fun part2(input: List<String>): Int {
    var dish = input.parseDish()
    val beamLoadSequence = mutableListOf<Int>()
    var cycles = 1000000000
    var patternSize: Int? = null
    while (cycles > 0) {
        dish = dish.tilt(Tilt.North).tilt(Tilt.West).tilt(Tilt.South).tilt(Tilt.East)
        cycles--

        beamLoadSequence += dish.beamLoad()
        patternSize = patternSize ?: beamLoadSequence.patternSize()
        if (patternSize != null) {
            cycles %= patternSize
        }
    }
    return dish.beamLoad()
}

private data class Pos(val x: Int, val y: Int) {
    fun north() = Pos(x, y - 1)
    fun south(): Pos = Pos(x, y + 1)
    fun east(): Pos = Pos(x + 1, y)
    fun west(): Pos = Pos(x - 1, y)
}

private data class Dish(
    val solidRocks: Set<Pos>,
    val roundRocks: Set<Pos>,
    val xRange: IntRange,
    val yRange: IntRange,
)

private fun List<String>.parseDish(): Dish {
    val solidRocks = mutableSetOf<Pos>()
    val roundRocks = mutableSetOf<Pos>()
    val maxX = first().indices
    val maxY = indices
    for ((y, line) in withIndex()) {
        for ((x, c) in line.withIndex()) {
            when (c) {
                '#' -> solidRocks += Pos(x, y)
                'O' -> roundRocks += Pos(x, y)
            }
        }
    }
    return Dish(solidRocks, roundRocks, maxX, maxY)
}

private enum class Tilt(
    val slide: Pos.() -> Pos,
    val sort: Set<Pos>.() -> List<Pos>,
) {
    North(Pos::north, { sortedBy { it.y } }),
    South(Pos::south, { sortedByDescending { it.y } }),
    West(Pos::west, { sortedBy { it.x } }),
    East(Pos::east, { sortedByDescending { it.x } }),
}

private fun Dish.tilt(tilt: Tilt): Dish {
    val newRoundRocks = mutableSetOf<Pos>()
    with(tilt) {
        for (rock in roundRocks.sort()) {
            var pos = rock
            var slidedPos = pos.slide()
            while (slidedPos.x in xRange && slidedPos.y in yRange && slidedPos !in solidRocks && slidedPos !in newRoundRocks) {
                pos = pos.slide()
                slidedPos = slidedPos.slide()
            }
            newRoundRocks += pos
        }
    }
    return copy(roundRocks = newRoundRocks)
}

private fun Dish.beamLoad(): Int {
    return roundRocks.sumOf { yRange.last - it.y + 1 }
}

private fun Dish.print() {
    for (y in xRange) {
        for (x in yRange) {
            val symbol = when (Pos(x, y)) {
                in solidRocks -> '#'
                in roundRocks -> 'O'
                else -> '.'
            }
            print(symbol)
        }
        println()
    }
}

// Pattern size starts at 2 to prevent repeating numbers from counting as pattern
private fun List<Int>.patternSize(minSize: Int = 2): Int? {
    if (size < minSize * 2) return null
    for (patternSize in minSize..size / 2) {
        val part1 = reversed().subList(0, patternSize)
        val part2 = reversed().subList(patternSize, patternSize * 2)
        if (part1.zip(part2).all { it.first == it.second }) {
            return patternSize
        }
    }
    return null
}

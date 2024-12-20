package day23

import check
import readInput

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day23_test")
    check(part1(testInput), 110)
    check(part2(testInput), 20)

    val input = readInput("2022", "Day23")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val (positions, _) = getPositionsAndRoundsAfterStableDiffusion(input, 10)

    val minX = positions.minOf { it.x }
    val maxX = positions.maxOf { it.x }
    val minY = positions.minOf { it.y }
    val maxY = positions.maxOf { it.y }

    return (maxX - minX + 1) * (maxY - minY + 1) - positions.size
}

private fun part2(input: List<String>): Int {
    val (_, rounds) = getPositionsAndRoundsAfterStableDiffusion(input)
    return rounds
}

private fun getPositionsAndRoundsAfterStableDiffusion(
    input: List<String>,
    maxRounds: Int = Int.MAX_VALUE
): Pair<Set<Pos>, Int> {
    val positions = parseElfPositions(input).toMutableSet()

    var round = 1
    while (round <= maxRounds) {
        val nextByElf = positions.associateWith { elf ->
            when (round % 4) {
                1 -> when {
                    elf.surroundingPositions.none { it in positions } -> elf
                    elf.northernPositions.none { it in positions } -> Pos(elf.x, elf.y - 1)
                    elf.southernPositions.none { it in positions } -> Pos(elf.x, elf.y + 1)
                    elf.westernPositions.none { it in positions } -> Pos(elf.x - 1, elf.y)
                    elf.easternPositions.none { it in positions } -> Pos(elf.x + 1, elf.y)
                    else -> elf
                }

                2 -> when {
                    elf.surroundingPositions.none { it in positions } -> elf
                    elf.southernPositions.none { it in positions } -> Pos(elf.x, elf.y + 1)
                    elf.westernPositions.none { it in positions } -> Pos(elf.x - 1, elf.y)
                    elf.easternPositions.none { it in positions } -> Pos(elf.x + 1, elf.y)
                    elf.northernPositions.none { it in positions } -> Pos(elf.x, elf.y - 1)
                    else -> elf
                }

                3 -> when {
                    elf.surroundingPositions.none { it in positions } -> elf
                    elf.westernPositions.none { it in positions } -> Pos(elf.x - 1, elf.y)
                    elf.easternPositions.none { it in positions } -> Pos(elf.x + 1, elf.y)
                    elf.northernPositions.none { it in positions } -> Pos(elf.x, elf.y - 1)
                    elf.southernPositions.none { it in positions } -> Pos(elf.x, elf.y + 1)
                    else -> elf
                }

                0 -> when {
                    elf.surroundingPositions.none { it in positions } -> elf
                    elf.easternPositions.none { it in positions } -> Pos(elf.x + 1, elf.y)
                    elf.northernPositions.none { it in positions } -> Pos(elf.x, elf.y - 1)
                    elf.southernPositions.none { it in positions } -> Pos(elf.x, elf.y + 1)
                    elf.westernPositions.none { it in positions } -> Pos(elf.x - 1, elf.y)
                    else -> elf
                }

                else -> error("!")
            }
        }
        val moves = nextByElf.entries.groupBy { it.value }.entries
            .filter { it.value.size == 1 }
            .map { it.value.first() }
            .filter { it.key != it.value }

        if (moves.isEmpty()) break
        moves.forEach { move ->
            val (from, to) = move
            positions -= from
            positions += to
        }
        round++
    }
    return positions to round
}

private data class Pos(val x: Int, val y: Int) {
    val surroundingPositions
        get() = setOf(
            Pos(x - 1, y - 1),
            Pos(x, y - 1),
            Pos(x + 1, y - 1),
            Pos(x + 1, y),
            Pos(x + 1, y + 1),
            Pos(x, y + 1),
            Pos(x - 1, y + 1),
            Pos(x - 1, y),
        )
    val northernPositions
        get() = setOf(Pos(x - 1, y - 1), Pos(x, y - 1), Pos(x + 1, y - 1))
    val southernPositions
        get() = setOf(Pos(x - 1, y + 1), Pos(x, y + 1), Pos(x + 1, y + 1))
    val westernPositions
        get() = setOf(Pos(x - 1, y - 1), Pos(x - 1, y), Pos(x - 1, y + 1))
    val easternPositions
        get() = setOf(Pos(x + 1, y - 1), Pos(x + 1, y), Pos(x + 1, y + 1))
}

private fun parseElfPositions(input: List<String>): Set<Pos> {
    val positions = mutableSetOf<Pos>()
    input.forEachIndexed { y, line ->
        line.forEachIndexed { x, c ->
            if (c == '#') positions += Pos(x, y)
        }
    }
    return positions
}

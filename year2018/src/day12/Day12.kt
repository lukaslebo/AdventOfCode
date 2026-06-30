package day12

import check
import readInput

fun main() {
    val testInput = readInput("2018", "Day12_test")
    check(part1(testInput), 325)

    val input = readInput("2018", "Day12")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Long {
    var potState = input.parsePotState()
    repeat(20) {
        potState = potState.evolve()
    }
    return potState.score()
}

private fun part2(input: List<String>): Long {
    var potState = input.parsePotState()
    val cache = mutableMapOf<String, Pair<Long, Long>>()
    var generations = 0L
    while (generations < 50_000_000_000L) {
        val (cachedOffset, cachedGenerations) = cache[potState.state] ?: (null to null)
        if (cachedOffset != null && cachedGenerations != null) {
            val remainingGenerations = 50_000_000_000L - generations
            val deltaGenerations = generations - cachedGenerations
            val cycles = remainingGenerations / deltaGenerations
            val deltaOffset = potState.offset - cachedOffset
            generations += cycles * deltaGenerations
            potState = potState.copy(offset = potState.offset + deltaOffset * cycles)
        } else {
            cache[potState.state] = potState.offset to generations
            potState = potState.evolve()
            generations++
        }

    }
    return potState.score()
}

private fun List<String>.parsePotState() = PotState(
    state = first().removePrefix("initial state: "),
    offset = 0,
    plantPatterns = filter { it.endsWith(" => #") }.map { it.substringBefore(" => ") }.toSet(),
    emptyPatterns = filter { it.endsWith(" => #.") }.map { it.substringBefore(" => ") }.toSet(),
)

private data class PotState(
    val state: String,
    val offset: Long,
    val plantPatterns: Set<String>,
    val emptyPatterns: Set<String>,
)

private fun PotState.evolve(): PotState {
    val current = "....$state...."
    val next = buildString {
        for (i in 0..current.length - 5) {
            val sequence = current.substring(i, i + 5)
            append(if (sequence in plantPatterns) "#" else ".")
        }
    }
    return copy(
        state = next.trim('.'),
        offset = offset - 2 + next.takeWhile { it == '.' }.length,
    )
}

private fun PotState.score() = state.mapIndexed { index, s ->
    if (s == '#') index + offset else 0
}.sum()

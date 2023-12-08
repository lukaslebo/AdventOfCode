package year2023.day08

import check
import lcm
import readInput

fun main() {
    val testInput1 = readInput("2023", "Day08_test_part1")
    val testInput2 = readInput("2023", "Day08_test_part2")
    check(part1(testInput1), 6)
    check(part2(testInput2), 6)

    val input = readInput("2023", "Day08")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Long {
    val pathsByPos = input.getPathsByPos()
    return pathsByPos.countStepsUntil(
        startingPos = "AAA",
        instructions = input.getInstructions(),
    ) { it == "ZZZ" }
}

private fun part2(input: List<String>): Long {
    val pathsByPos = input.getPathsByPos()
    val startingPositions = pathsByPos.keys.filter { it.endsWith("A") }
    return startingPositions.map { startingPos ->
        pathsByPos.countStepsUntil(
            startingPos = startingPos,
            instructions = input.getInstructions(),
        ) { it.endsWith("Z") }
    }.lcm()
}

private fun Map<String, Pair<String, String>>.countStepsUntil(
    startingPos: String,
    instructions: Instructions,
    predicate: (String) -> Boolean,
): Long {
    var currentPos = startingPos
    var steps = 0L
    while (!predicate(currentPos)) {
        currentPos = getValue(currentPos).take(instructions.next())
        steps++
    }
    return steps
}

private enum class Direction { L, R }
private data class Instructions(val seq: List<Direction>, var index: Int = 0) {
    fun next(): Direction = seq[index++ % seq.size]
}

private fun Pair<String, String>.take(dir: Direction): String = if (dir == Direction.L) first else second

private fun List<String>.getInstructions() = Instructions(first().map { if (it == 'L') Direction.L else Direction.R })
private fun List<String>.getPathsByPos() = drop(2).associate { line ->
    val pos = line.substring(0, 3)
    val left = line.substring(7, 10)
    val right = line.substring(12, 15)
    pos to Pair(left, right)
}

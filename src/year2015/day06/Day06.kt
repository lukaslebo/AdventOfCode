package year2015.day06

import readInput

fun main() {
    val input = readInput("2015", "Day06")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val grid = Array(1000) { Array(1000) { false } }
    input.executeActionsOnGrid(grid) { action, value ->
        when (action) {
            Action.Toggle -> !value
            Action.TurnOn -> true
            Action.TurnOff -> false
        }
    }
    return grid.sumOf { row -> row.count { it } }
}

private fun part2(input: List<String>): Int {
    val grid = Array(1000) { Array(1000) { 0 } }
    input.executeActionsOnGrid(grid) { action, value ->
        (value + when (action) {
            Action.Toggle -> 2
            Action.TurnOn -> 1
            Action.TurnOff -> -1
        }).coerceAtLeast(0)
    }
    return grid.sumOf { it.sum() }
}

private enum class Action {
    Toggle, TurnOn, TurnOff;

    companion object {
        fun of(action: String) = when (action) {
            "toggle" -> Toggle
            "turn on" -> TurnOn
            "turn off" -> TurnOff
            else -> error("action $action not supported")
        }
    }
}

private val pattern = "(toggle|turn off|turn on) (\\d+,\\d+) through (\\d+,\\d+)".toRegex()

private fun <T> List<String>.executeActionsOnGrid(grid: Array<Array<T>>, action: (Action, T) -> T) {
    forEach { instruction ->
        val (_, action, coord1, coord2) = pattern.matchEntire(instruction)?.groupValues
            ?: error("Pattern not matching $instruction")
        val (x1, y1) = coord1.split(',').map { it.toInt() }
        val (x2, y2) = coord2.split(',').map { it.toInt() }
        for (y in y1..y2) {
            for (x in x1..x2) {
                grid[y][x] = action(Action.of(action), grid[y][x])
            }
        }
    }
}
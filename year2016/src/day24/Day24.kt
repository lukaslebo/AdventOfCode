package day24

import algorithms.aStar
import check
import readInput
import kotlin.math.abs

fun main() {
    val testInput = readInput("2016", "Day24_test")
    check(part1(testInput), 14)

    val input = readInput("2016", "Day24")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.parseAirDuctMap().findShortestPathToVisitAllNums()
private fun part2(input: List<String>) = input.parseAirDuctMap().findShortestPathToVisitAllNums(returnToStart = true)

private fun AirDuctMap.findShortestPathToVisitAllNums(returnToStart: Boolean = false): Int {
    val distinctNumPairs = posByNum.keys.distinctNumPairs()
    val distanceByNumPair = distinctNumPairs.flatMap { (fromNum, toNum) ->
        val from = posByNum.getValue(fromNum)
        val goal = posByNum.getValue(toNum)
        val distance = aStar(
            from = from,
            goal = { it == goal },
            neighboursWithCost = { neighbours().filter { it !in walls }.map { it to 1 }.toSet() },
            heuristic = { it.manhattanDistanceTo(goal) },
        )!!.cost
        listOf("$fromNum$toNum" to distance, "$toNum$fromNum" to distance)
    }.toMap()

    val paths = posByNum.keys.paths().filter { it.startsWith("0") }.map { if (returnToStart) it + "0" else it }
    return paths.minOf { path ->
        path.windowed(2) { distanceByNumPair.getValue(it.toString()) }.sum()
    }
}

private data class Pos(val x: Int, val y: Int) {
    companion object {
        val up = Pos(0, -1)
        val down = Pos(0, 1)
        val left = Pos(-1, 0)
        val right = Pos(1, 0)

        val directions = setOf(up, down, left, right)
    }

    operator fun plus(other: Pos) = Pos(x + other.x, y + other.y)

    fun manhattanDistanceTo(other: Pos) = abs(x - other.x) + abs(y - other.y)
    fun neighbours() = directions.map { this + it }
}

private data class AirDuctMap(
    val walls: Set<Pos>,
    val posByNum: Map<Int, Pos>,
)

private fun List<String>.parseAirDuctMap(): AirDuctMap {
    val walls = mutableSetOf<Pos>()
    val posByNum = mutableMapOf<Int, Pos>()

    for ((y, line) in withIndex()) {
        for ((x, c) in line.withIndex()) {
            when (c) {
                '#' -> walls += Pos(x, y)
                '.' -> Unit
                else -> posByNum += c.digitToInt() to Pos(x, y)
            }
        }
    }
    return AirDuctMap(walls, posByNum)
}

private fun Set<Int>.distinctNumPairs(): Set<Pair<Int, Int>> {
    val nums = sorted()
    return nums.flatMapIndexed { i, n ->
        nums.drop(i + 1).map { n to it }
    }.toSet()
}

private fun Set<Int>.paths(): Set<String> {
    if (isEmpty()) return setOf("")
    val result = mutableSetOf<String>()
    for (num in this) {
        val next = this - num
        result += next.paths().map { "$num$it" }
    }
    return result
}

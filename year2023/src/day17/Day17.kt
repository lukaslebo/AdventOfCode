package day17

import check
import readInput
import util.aStar
import kotlin.math.abs

fun main() {
    val testInput = readInput("2023", "Day17_test")
    check(part1(testInput), 102)
    check(part2(testInput), 94)

    val input = readInput("2023", "Day17")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.heatLoss(minStraightSteps = 0, maxStraightSteps = 3)
private fun part2(input: List<String>) = input.heatLoss(minStraightSteps = 4, maxStraightSteps = 10)

private data class Pos(val x: Int, val y: Int) {
    fun reverse() = Pos(-x, -y)

    companion object {
        val up = Pos(0, -1)
        val down = Pos(0, 1)
        val left = Pos(-1, 0)
        val right = Pos(1, 0)
    }

    operator fun plus(other: Pos) = Pos(x + other.x, y + other.y)
    infix fun distanceTo(other: Pos) = abs(x - other.x) + abs(y - other.y)

    override fun toString() = "($x,$y)"
}

private data class Node(
    val pos: Pos,
    val straightSteps: Int,
    val dir: Pos?,
) {
    fun move(moveDir: Pos) = Node(
        pos = pos + moveDir,
        straightSteps = if (moveDir == dir) (straightSteps + 1) else 1,
        dir = moveDir,
    )
}

private fun List<String>.heatLoss(minStraightSteps: Int, maxStraightSteps: Int): Int {
    val xRange = first().indices
    val yRange = indices

    fun Node.neighboursWithHeatLoss() = sequenceOf(Pos.up, Pos.down, Pos.left, Pos.right)
        .filter { it.reverse() != dir }
        .map { move(it) }
        .filter { it.pos.x in xRange && it.pos.y in yRange }
        .filter { straightSteps >= minStraightSteps || (it.dir == dir || dir == null) }
        .filter { it.straightSteps <= maxStraightSteps }
        .mapTo(mutableSetOf()) { it to heatLoss(it.pos) }

    val target = Pos(first().lastIndex, lastIndex)
    val from = Node(Pos(0, 0), 0, null)
    val cruciblePath = aStar(
        from = from,
        goal = { it.pos == target && it.straightSteps >= minStraightSteps },
        heuristic = { it.pos distanceTo target },
        neighboursWithCost = Node::neighboursWithHeatLoss,
    )
    //printPath(cruciblePath.path().map(Node::pos))
    return cruciblePath?.cost ?: error("no path found")
}

private fun List<String>.heatLoss(pos: Pos) = get(pos.y)[pos.x].digitToInt()

private fun List<String>.printPath(path: List<Pos>, displayPathOnly: Boolean = false) {
    for ((y, line) in withIndex()) {
        for ((x, c) in line.withIndex()) {
            val map = if (Pos(x, y) in path) '#' else c
            val pathOnly = if (Pos(x, y) in path) c else ' '
            print(if (displayPathOnly) pathOnly else map)
        }
        println()
    }
}

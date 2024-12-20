package day12

import check
import readInput

fun main() {
    val testInput1 = readInput("2024", "Day12_test_part1")
    val testInput2a = readInput("2024", "Day12_test_part2a")
    val testInput2b = readInput("2024", "Day12_test_part2b")
    check(part1(testInput1), 1930)
    check(part2(testInput2a), 236)
    check(part2(testInput2b), 368)

    val input = readInput("2024", "Day12")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.parseGarden()
    .getPlantZones()
    .sumOf { it.calculateFencingCost() }

private fun part2(input: List<String>) = input.parseGarden()
    .getPlantZones()
    .sumOf { it.calculateFencingCost(applyDiscount = true) }

private data class Pos(val x: Int, val y: Int) {
    val adjacent
        get() = setOf(
            Pos(x, y + 1),
            Pos(x + 1, y),
            Pos(x, y - 1),
            Pos(x - 1, y),
        )
}

private data class Node(val pos: Pos, val plant: Char)

private fun List<String>.parseGarden(): List<Node> {
    val garden = mutableListOf<Node>()
    for ((y, line) in withIndex()) {
        for ((x, c) in line.withIndex()) {
            garden += Node(Pos(x, y), c)
        }
    }
    return garden
}

private fun List<Node>.getPlantZones(): List<Set<Node>> {
    val nodeByPos = associateBy { it.pos }
    val zones = mutableListOf<Set<Node>>()
    val visited = mutableSetOf<Pos>()
    for (pos in map { it.pos }) {
        if (pos in visited) continue

        val queue = ArrayDeque<Pos>()
        queue += pos
        val plantInCurrentZone = nodeByPos.getValue(pos).plant
        val zone = mutableSetOf<Node>()
        while (queue.isNotEmpty()) {
            val node = nodeByPos[queue.removeFirst()] ?: continue
            if (node.plant != plantInCurrentZone || node in zone) continue
            zone += node
            visited += node.pos
            queue += node.pos.adjacent
        }
        zones += zone
    }
    return zones
}

private fun Set<Node>.calculateFencingCost(applyDiscount: Boolean = false): Int {
    val zone = map { it.pos }
    val edgeSegments = mutableSetOf<Pair<Pos, Pos>>()
    for (pos in zone) {
        for (neighbor in pos.adjacent) {
            if (neighbor !in zone) {
                edgeSegments += pos to neighbor
            }
        }
    }
    return if (applyDiscount) zone.size * edgeSegments.countStraightEdges() else zone.size * edgeSegments.size
}

private fun Set<Pair<Pos, Pos>>.countStraightEdges(): Int {
    fun List<Int>.countContinuous() = sorted().windowed(2).count { (a, b) -> b > a + 1 } + 1
    val verticalEdges = groupBy { it.first.x to it.second.x }
        .filter { it.key.first != it.key.second }
        .values
        .sumOf { list -> list.map { it.first.y }.countContinuous() }
    val horizontalEdges = groupBy { it.first.y to it.second.y }
        .filter { it.key.first != it.key.second }
        .values
        .sumOf { list -> list.map { it.first.x }.countContinuous() }
    return verticalEdges + horizontalEdges
}

package day25

import algorithms.aStar
import check
import readInput

fun main() {
    val testInput = readInput("2023", "Day25_test")
    check(part1(testInput), 54)

    val input = readInput("2023", "Day25")
    part1(input)
}

private fun part1(input: List<String>): Int {
    val graph = input.parseGraph()
    val vertices = graph.distinctVertices()

    fun String.getNeighboursWithCostIgnoring(ignore: Set<Pair<String, String>>) = graph.getValue(this)
        .filter { it to this !in ignore && this to it !in ignore }
        .mapTo(mutableSetOf()) { it to 1 }

    fun Pair<String, String>.findPathAroundIgnoring(ignore: Set<Pair<String, String>>) = aStar(
        from = first,
        goal = { it == second },
        neighboursWithCost = { getNeighboursWithCostIgnoring(ignore) },
    )?.path()

    for (vertexA in vertices) {
        val ignoreA = setOf(vertexA)
        val verticesOnPathA = vertexA.findPathAroundIgnoring(ignoreA)?.zipWithNext() ?: continue
        for (vertexB in verticesOnPathA) {
            val ignoreAB = setOf(vertexA, vertexB)
            val verticesOnPathB = vertexB.findPathAroundIgnoring(ignoreAB)?.zipWithNext() ?: continue
            for (vertexC in verticesOnPathB) {
                val ignoreABC = setOf(vertexA, vertexB, vertexC)
                val foundPath = vertexC.findPathAroundIgnoring(ignoreABC) != null
                if (!foundPath) {
                    val part1 = graph.fill(vertexA.first, ignoreABC)
                    val part2 = graph.fill(vertexA.second, ignoreABC)
                    if (part1.intersect(part2).isEmpty() && (part1 + part2) == graph.keys) {
                        println("${part1.size} * ${part2.size} = ${part1.size * part2.size}")
                        return part1.size * part2.size
                    }
                }
            }
        }
    }
    error("unable to find solution")
}

private fun List<String>.parseGraph(): Map<String, Set<String>> {
    val vertices = flatMap { line ->
        val nodes = line.split(": ", " ")
        val from = nodes.first()
        val destinations = nodes.drop(1)
        destinations.map { from to it }
    }
    val graph = mutableMapOf<String, MutableSet<String>>()
    for (vertex in vertices) {
        graph.getOrPut(vertex.first) { mutableSetOf() } += vertex.second
        graph.getOrPut(vertex.second) { mutableSetOf() } += vertex.first
    }
    return graph
}

private fun Map<String, Set<String>>.distinctVertices(): List<Pair<String, String>> {
    val vertices = mutableSetOf<Pair<String, String>>()
    forEach { (from, destinations) ->
        destinations.forEach { to ->
            vertices += from to to
        }
    }
    return vertices.distinctBy { setOf(it.first, it.second) }
}

private fun Map<String, Set<String>>.fill(from: String, ignoring: Set<Pair<String, String>>): Set<String> {
    val visited = mutableSetOf(from)
    val stack = ArrayDeque<String>()
    stack += from
    while (stack.isNotEmpty()) {
        val current = stack.removeFirst()
        val adjacent = getValue(current)
            .filter { current to it !in ignoring && it to current !in ignoring && it !in visited }
        visited += adjacent
        stack += adjacent
    }
    return visited
}

package year2023.day23

import check
import readInput

fun main() {
    val testInput = readInput("2023", "Day23_test")
    check(part1(testInput), 94)
    check(part2(testInput), 154)

    val input = readInput("2023", "Day23")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.parseHikingMap().longestHike()

private fun part2(input: List<String>) = input.parseHikingMap().removeSlopes().longestHike()

private fun HikingMap.longestHike() = createGraph().longestHike(start, target) ?: error("no path found")

private data class Pos(val x: Int, val y: Int) {
    fun neighbours() = listOf(Pos(x, y - 1), Pos(x, y + 1), Pos(x - 1, y), Pos(x + 1, y))
}

private data class HikingMap(
    val trail: Set<Pos>,
    /** Positions of the slope with the position one has to go next */
    val slopes: Map<Pos, Pos>,
    val start: Pos,
    val target: Pos,
)

private fun List<String>.parseHikingMap(): HikingMap {
    val trail = mutableSetOf<Pos>()
    val slopes = mutableMapOf<Pos, Pos>()
    for ((y, line) in withIndex()) {
        for ((x, c) in line.withIndex()) {
            when (c) {
                '.' -> trail += Pos(x, y)
                'v' -> slopes += Pos(x, y) to Pos(x, y + 1)
                '^' -> slopes += Pos(x, y) to Pos(x, y - 1)
                '>' -> slopes += Pos(x, y) to Pos(x + 1, y)
                '<' -> slopes += Pos(x, y) to Pos(x - 1, y)
            }
        }
    }
    return HikingMap(
        trail = trail,
        slopes = slopes,
        start = trail.single { it.y == 0 },
        target = trail.single { it.y == lastIndex },
    )
}

private fun HikingMap.removeSlopes() = copy(trail = trail + slopes.keys, slopes = emptyMap())

private fun HikingMap.createGraph(): Map<Pos, Set<Pair<Pos, Int>>> {
    val nodes = trail.filter { trailPos ->
        trailPos.neighbours().count { it in trail || it in slopes } > 2
    } + start + target

    val graph = nodes.associateWith { mutableSetOf<Pair<Pos, Int>>() }

    fun pathsWithDistance(pos: Pos, distance: Int = 0, visited: Set<Pos> = setOf(pos)): List<Pair<Pos, Int>> {
        if (pos in nodes && distance > 0) return listOf(pos to distance)
        return pos.neighbours()
            .filter { next -> (next in trail || (next in slopes && pos != slopes[next])) && next !in visited }
            .flatMap { next ->
                pathsWithDistance(pos = next, distance = distance + 1, visited = visited + next)
            }
    }

    for (node in nodes) {
        graph.getValue(node) += pathsWithDistance(node)
    }

    return graph
}

private fun Map<Pos, Set<Pair<Pos, Int>>>.longestHike(pos: Pos, target: Pos, visited: Set<Pos> = setOf(pos)): Int? {
    return if (pos == target) 0
    else getValue(pos)
        .filter { it.first !in visited }
        .mapNotNull { (next, distance) ->
            val longestHike = longestHike(next, target, visited + next) ?: return@mapNotNull null
            distance + longestHike
        }
        .maxOrNull()
}

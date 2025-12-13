package day17

import algorithms.aStar
import check
import readInput
import util.md5
import kotlin.math.abs
import kotlin.math.max

fun main() {
    val testInput = readInput("2016", "Day17_test")
    check(part1(testInput), "DRURDRUDDLLDLUURRDULRLDUUDDDRR")
    check(part2(testInput), 830)

    val input = readInput("2016", "Day17")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): String {
    val passcode = input.first()
    val start = PosAndPath(Pos(0, 0))
    val target = Pos(3, 3)

    val path = aStar(
        from = start,
        goal = { it.pos == target },
        neighboursWithCost = {
            PosAndPath.availableDirections(passcode = passcode, currentPath = path)
                .map { this + it }
                .filter { it.pos.x in 0..3 && it.pos.y in 0..3 }
                .map { it to 1 }
                .toSet()
        },
        heuristic = { it.pos.manhattanDistanceTo(target) },
    )?.value?.path

    return path ?: error("No path found")
}

private fun part2(input: List<String>): Int {
    val passcode = input.first()
    val target = Pos(3, 3)

    var longestPathSize = 0

    val queue = ArrayDeque<PosAndPath>()
    queue += PosAndPath(Pos(0, 0))

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        if (current.pos == target) {
            longestPathSize = max(longestPathSize, current.path.length)
            continue
        }

        queue += PosAndPath.availableDirections(passcode = passcode, currentPath = current.path)
            .map { current + it }
            .filter { it.pos.x in 0..3 && it.pos.y in 0..3 }
    }

    return longestPathSize
}

private data class Pos(
    val x: Int,
    val y: Int,
) {
    companion object {
        val up = Pos(0, -1)
        val down = Pos(0, 1)
        val left = Pos(-1, 0)
        val right = Pos(1, 0)
    }

    operator fun plus(other: Pos) = Pos(x + other.x, y + other.y)
    fun manhattanDistanceTo(other: Pos) = abs(x - other.x) + abs(y - other.y)
}

private data class PosAndPath(
    val pos: Pos,
    val path: String = "",
) {
    companion object {
        private val up = PosAndPath(Pos.up, "U")
        private val down = PosAndPath(Pos.down, "D")
        private val left = PosAndPath(Pos.left, "L")
        private val right = PosAndPath(Pos.right, "R")

        private val openDoors = 'b'.code..'f'.code

        fun availableDirections(passcode: String, currentPath: String): List<PosAndPath> {
            val hash = (passcode + currentPath).md5()
            return listOfNotNull(
                up.takeIf { hash[0].code in openDoors },
                down.takeIf { hash[1].code in openDoors },
                left.takeIf { hash[2].code in openDoors },
                right.takeIf { hash[3].code in openDoors },
            )
        }
    }

    operator fun plus(other: PosAndPath) = PosAndPath(pos + other.pos, path + other.path)
}

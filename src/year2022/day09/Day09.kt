package year2022.day09

import check
import readInput

import kotlin.math.abs

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("2022", "Day09_test_part1")
    val testInput2 = readInput("2022", "Day09_test_part2")
    check(part1(testInput1), 13)
    check(part2(testInput2), 36)

    val input = readInput("2022", "Day09")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = findTailPathUniquePositionCount(input, 2)

private fun part2(input: List<String>) = findTailPathUniquePositionCount(input, 10)

private fun findTailPathUniquePositionCount(input: List<String>, size: Int): Int {
    val snake = List(size) { Knot() }
    val tailPath = mutableSetOf<Knot>()

    for (line in input) {
        val (dir, amount) = line.split(' ')
        repeat(amount.toInt()) {
            snake.first().move(dir)
            snake.windowed(2) { pair ->
                val (h, t) = pair
                t.follow(h)
            }
            tailPath += snake.last().copy()
        }

    }
    return tailPath.size
}

private data class Knot(private var x: Int = 0, private var y: Int = 0) {
    fun move(dir: String) {
        when (dir) {
            "R" -> x++
            "L" -> x--
            "U" -> y++
            "D" -> y--
        }
    }

    fun follow(other: Knot) {
        val dx = other.x - x
        val dy = other.y - y

        if (abs(dx) <= 1 && abs(dy) <= 1) return

        if (dx == 0 || dy == 0) {
            when {
                dx > 1 -> x++
                dx < -1 -> x--
                dy > 1 -> y++
                dy < -1 -> y--
            }
        } else {
            if (dx > 0) x++ else x--
            if (dy > 0) y++ else y--
        }
    }
}
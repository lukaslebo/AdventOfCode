package year2015.day03

import check
import readInput

fun main() {
    val testInput = readInput("2015", "Day03_test")
    check(part1(testInput), 4)
    check(part2(testInput), 3)

    val input = readInput("2015", "Day03")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    var santasPosition = Pos(0, 0)
    val houses = mutableSetOf(santasPosition)
    for (dir in input.first()) {
        santasPosition = santasPosition.move(dir)
        houses += santasPosition
    }
    return houses.size
}

private fun part2(input: List<String>): Int {
    var santasPosition = Pos(0, 0)
    var roboPosition = Pos(0, 0)
    val houses = mutableSetOf(santasPosition)
    var index = 0
    for (dir in input.first()) {
        if (index++ % 2 == 0) {
            santasPosition = santasPosition.move(dir)
            houses += santasPosition
        } else {
            roboPosition = roboPosition.move(dir)
            houses += roboPosition
        }
    }
    return houses.size
}

private data class Pos(val x: Int, val y: Int) {
    fun move(dir: Char) = when (dir) {
        '>' -> Pos(x + 1, y)
        '<' -> Pos(x - 1, y)
        '^' -> Pos(x, y - 1)
        'v' -> Pos(x, y + 1)
        else -> error("direction $dir not supported")
    }
}
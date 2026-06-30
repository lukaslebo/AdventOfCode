package day13

import check
import readInput

fun main() {
    val testInput1 = readInput("2018", "Day13_test_part1")
    val testInput2 = readInput("2018", "Day13_test_part2")
    check(part1(testInput1), "7,3")
    check(part2(testInput2), "6,4")

    val input = readInput("2018", "Day13")
    println(part1(input))
    println(part2(input))
    check(part2(input) != "32,83")
}

private fun part1(input: List<String>): String {
    val (carts, tracks) = input.parseCartsAndTracks()
    while (true) {
        for (cart in carts) {
            cart.moveAlong(tracks)
            val crashed = carts.any { it != cart && it.pos == cart.pos }
            if (crashed) {
                return cart.pos.format()
            }
        }
    }
}

private fun part2(input: List<String>): String {
    val (carts, tracks) = input.parseCartsAndTracks()
    while (carts.size > 1) {
        val crashed = mutableSetOf<Cart>()
        for (cart in carts) {
            if (cart in crashed) continue
            cart.moveAlong(tracks)
            val crashedWith = carts.filter { it !in crashed }.find { it != cart && it.pos == cart.pos }
            if (crashedWith != null) {
                crashed += cart
                crashed += crashedWith
            }
        }
        carts -= crashed
    }
    return carts.first().pos.format()
}

private fun Cart.moveAlong(tracks: List<String>) {
    dir = when (tracks[pos.y][pos.x]) {
        '/' -> when (dir) {
            Dir.Up -> Dir.Right
            Dir.Right -> Dir.Up
            Dir.Down -> Dir.Left
            Dir.Left -> Dir.Down
        }

        '\\' -> when (dir) {
            Dir.Up -> Dir.Left
            Dir.Right -> Dir.Down
            Dir.Down -> Dir.Right
            Dir.Left -> Dir.Up
        }

        '+' -> {
            when (intersectionsSeen++ % 3) {
                0 -> dir.turnLeft()
                1 -> dir
                else -> dir.turnRight()
            }
        }

        else -> dir
    }
    pos += dir
}

private data class Pos(val x: Int, val y: Int) {
    operator fun plus(other: Pos) = Pos(x + other.x, y + other.y)
    operator fun plus(dir: Dir) = this + dir.pos

    fun format() = "$x,$y"
}

private enum class Dir(val pos: Pos) {
    Up(Pos(0, -1)),
    Right(Pos(1, 0)),
    Down(Pos(0, 1)),
    Left(Pos(-1, 0));

    fun turnLeft() = when (this) {
        Up -> Left
        Right -> Up
        Down -> Right
        Left -> Down
    }

    fun turnRight() = when (this) {
        Up -> Right
        Right -> Down
        Down -> Left
        Left -> Up
    }
}

private data class Cart(var pos: Pos, var dir: Dir, var intersectionsSeen: Int = 0)

private fun List<String>.parseCartsAndTracks(): Pair<MutableList<Cart>, List<String>> {
    val carts = mutableListOf<Cart>()
    for ((y, line) in withIndex()) {
        for ((x, c) in line.withIndex()) {
            carts += when (c) {
                '^' -> Cart(pos = Pos(x, y), dir = Dir.Up)
                '>' -> Cart(pos = Pos(x, y), dir = Dir.Right)
                'v' -> Cart(pos = Pos(x, y), dir = Dir.Down)
                '<' -> Cart(pos = Pos(x, y), dir = Dir.Left)
                else -> continue
            }
        }
    }
    val tracks = map { line ->
        line
            .replace("^", "|")
            .replace("v", "|")
            .replace(">", "-")
            .replace("<", "-")
    }
    return carts to tracks
}

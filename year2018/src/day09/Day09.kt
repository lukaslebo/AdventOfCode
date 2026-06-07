package day09

import check
import readInput

fun main() {
    val testInput = readInput("2018", "Day09_test")
    check(part1(testInput), 37305)

    val input = readInput("2018", "Day09")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Long {
    val (players, lastMarbleScore) = input.first().parseGame()
    return playGame(players, lastMarbleScore)
}

private fun part2(input: List<String>): Long {
    val (players, lastMarbleScore) = input.first().parseGame()
    return playGame(players, lastMarbleScore * 100)
}

private fun String.parseGame() = split(" ").let { it[0].toInt() to it[6].toLong() }

private fun playGame(players: Int, lastMarbleScore: Long): Long {
    class Node(val value: Long) {
        lateinit var prev: Node
        lateinit var next: Node
    }

    val scores = LongArray(players)

    val zero = Node(0)
    zero.prev = zero
    zero.next = zero

    var current = zero

    for (marble in 1..lastMarbleScore) {
        val player = ((marble - 1) % players).toInt()

        if (marble % 23L == 0L) {
            repeat(7) {
                current = current.prev
            }

            scores[player] += marble + current.value

            current.prev.next = current.next
            current.next.prev = current.prev

            current = current.next
        } else {
            val left = current.next
            val right = left.next

            val node = Node(marble)

            node.prev = left
            node.next = right

            left.next = node
            right.prev = node

            current = node
        }
    }

    return scores.max()
}

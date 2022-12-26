package year2015.day11

import check
import readInput

fun main() {
    val testInput = readInput("2015", "Day11_test")
    check(part1(testInput), "abcdffaa")

    val input = readInput("2015", "Day11")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = incrementPassword(input.first())

private fun part2(input: List<String>) = incrementPassword(incrementPassword(input.first()))

private fun incrementPassword(input: String): String {
    val oldPassword = input.map { it - 'a' }
    val password = oldPassword.toMutableList()
    val confusingLetters = setOf('i' - 'a', 'o' - 'a', 'l' - 'a')
    while (true) {
        val cond1 = password.windowed(3).any { it[0] + 1 == it[1] && it[1] + 1 == it[2] }
        val cond2 = password.none { it in confusingLetters }
        val cond3 = password.windowed(2).windowed(2).count { (a, b) ->
            (a[0] == a[1] && a[0] != b[0]) || (b[0] == b[1] && a[0] != b[0])
        } >= 2
        if (cond1 && cond2 && cond3 && password != oldPassword) return password.toText()

        password[7] = password[7] + 1
        for (i in password.indices.reversed()) {
            if (password[i] > 25) {
                password[i] = 0
                password[i - 1] = password[i - 1] + 1
            } else break
        }
    }
}

private fun List<Int>.toText() = joinToString("") { ('a' + it).toString() }

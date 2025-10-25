package day01

import check
import readInput

fun main() {
    val testInput = readInput("2017", "Day01_test")
    check(part1(testInput), 9)

    val input = readInput("2017", "Day01")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    return input.first().map { it.digitToInt() }.let { it + it.first() }.windowed(2).filter { (a, b) -> a == b }
        .sumOf { it.first() }
}

private fun part2(input: List<String>): Int {
    val captcha = input.first().map { it.digitToInt() }
    val half = captcha.size / 2
    val firstHalfQueue = ArrayDeque(captcha.take(half))
    val lastHalfQueue = ArrayDeque(captcha.takeLast(captcha.size - half))
    var result = 0
    repeat(captcha.size) {
        if (firstHalfQueue.first() == lastHalfQueue.first()) {
            result += firstHalfQueue.first()
        }
        lastHalfQueue += firstHalfQueue.removeFirst()
        firstHalfQueue += lastHalfQueue.removeFirst()
    }
    return result
}

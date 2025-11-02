package day17

import check
import readInput
import java.util.ArrayDeque

fun main() {
    val testInput = readInput("2017", "Day17_test")
    check(part1(testInput), 638)

    val input = readInput("2017", "Day17")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val stepSize = input.first().toInt()
    return runSpinLock(stepSize, 2017).nextAfter(2017)
}

private fun part2(input: List<String>): Int {
    val stepSize = input.first().toInt()
    return runSpinLock(stepSize, 50_000_000).nextAfter(0)
}

private fun runSpinLock(stepSize: Int, spins: Int): List<Int> {
    val buffer = ArrayDeque<Int>()
    buffer += 0
    for (n in 1..spins) {
        buffer.rotate(stepSize)
        buffer += n
    }
    return buffer.toList()
}

private fun ArrayDeque<Int>.rotate(n: Int) {
    repeat(n) {
        addLast(removeFirst())
    }
}

private fun List<Int>.nextAfter(n: Int) = get((indexOf(n) + 1) % size)

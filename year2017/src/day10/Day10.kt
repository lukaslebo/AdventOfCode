package day10

import check
import readInput
import kotlin.math.absoluteValue

fun main() {
    check(part1(listOf("3,4,1,5"), size = 5), 12)
    check(part2(listOf("1,2,4")), "63960835bcdc130f0b66d7ff4f6a5a8e")

    val input = readInput("2017", "Day10")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>, size: Int = 256): Int {
    val lengths = input.first().split(",").map { it.trim().toInt() }
    val list = (0 until size).toList()
    return list.knotHash(lengths).take(2).reduce(Int::times)
}

private fun part2(input: List<String>) = createKnotHash(input.first())

fun createKnotHash(input: String, times: Int = 64): String {
    val lengths = input.map { it.code } + listOf(17, 31, 73, 47, 23)
    val sparseHash = (0..255).toList().knotHash(lengths, times)
    val denseHash = sparseHash.windowed(size = 16, step = 16) { it.reduce(Int::xor) }
    return denseHash.joinToString("") { it.toHexString().takeLast(2) }
}

private fun List<Int>.knotHash(lengths: List<Int>, times: Int = 1): List<Int> {
    var list = this
    var skip = 0
    var totalRotation = 0
    repeat(times) {
        for (length in lengths) {
            val rotation = -(length + skip++)
            list = list.reverse(length).rotate(rotation)
            totalRotation += rotation
        }
    }
    return list.rotate(-totalRotation)
}

private fun List<Int>.rotate(n: Int): List<Int> {
    val offset = n.absoluteValue % size
    val remaining = size - offset
    return if (n >= 0) takeLast(offset) + take(remaining)
    else takeLast(remaining) + take(offset)
}

private fun List<Int>.reverse(n: Int) = take(n).reversed() + takeLast(size - n)

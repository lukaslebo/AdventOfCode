package day15

import check
import readInput

fun main() {
    val testInput = readInput("2016", "Day15_test")
    check(part1(testInput), 5)

    val input = readInput("2016", "Day15")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val discs = input.map { it.parseDisc() }
    return timeToPressButton(discs)
}

private fun part2(input: List<String>): Int {
    val discs = input.map { it.parseDisc() } + Disc(positions = 11, position = 0)
    return timeToPressButton(discs)
}

private data class Disc(
    val positions: Int,
    var position: Int,
) {
    fun tick(times: Int = 1) {
        if (times == 0) return
        repeat(times) {
            position = (position + 1) % positions
        }
    }
}

private fun timeToPressButton(discs: List<Disc>): Int {
    for ((index, disc) in discs.withIndex()) {
        disc.tick(times = index)
    }
    var time = 0
    while (!discs.all { it.position == 0 }) {
        discs.forEach { it.tick() }
        time++
    }
    return time - 1
}

private fun String.parseDisc(): Disc {
    val pattern =
        "Disc #(?<id>\\d+) has (?<positions>\\d+) positions; at time=0, it is at position (?<position>\\d+).".toRegex()
    val match = pattern.matchEntire(this)!!
    return Disc(
        positions = match.groups["positions"]!!.value.toInt(),
        position = match.groups["position"]!!.value.toInt(),
    )
}

package year2023.day06

import check
import readInput

fun main() {
    val testInput1 = readInput("2023", "Day06_test")
    check(part1(testInput1), 288)
    check(part2(testInput1), 71503)

    val input = readInput("2023", "Day06")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val times = input.first().toNumbers()
    val distances = input.last().toNumbers()
    return times.zip(distances)
        .map { (time, distance) -> countWaysToWinRace(time, distance) }
        .reduce { acc, i -> acc * i }
}

private fun part2(input: List<String>): Int {
    val time = input.first().toNumber()
    val recordDistance = input.last().toNumber()
    return countWaysToWinRace(time, recordDistance)
}

private fun countWaysToWinRace(time: Long, recordDistance: Long): Int {
    fun winsRace(holdTime: Long) = holdTime * (time - holdTime) > recordDistance
    val holdTimes = 0..time
    return holdTimes.count { winsRace(it) }
}

private fun String.toNumbers(): List<Long> = split(" ").mapNotNull { it.trim().toLongOrNull() }
private fun String.toNumber(): Long = filter { it.isDigit() }.toLong()

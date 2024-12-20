package day14

import check
import readInput
import java.lang.Integer.min

fun main() {
    val testInput = readInput("2015", "Day14_test")
    check(part1(testInput, 1000), 1120)
    check(part2(testInput, 1000), 688)

    val input = readInput("2015", "Day14")
    println(part1(input, 2503))
    println(part2(input, 2503))
}

private fun part1(input: List<String>, seconds: Int): Int {
    return parseStatsByReindeer(input).maxOf { it.value.distanceAfter(seconds) }
}

private fun part2(input: List<String>, seconds: Int): Int {
    val statsByReindeer = parseStatsByReindeer(input)
    val scoresByReindeer = statsByReindeer.keys.associateWith { 0 }.toMutableMap()
    for (s in 1..seconds) {
        val winningReindeer = parseStatsByReindeer(input).maxBy { it.value.distanceAfter(s) }.key
        scoresByReindeer[winningReindeer] = scoresByReindeer.getValue(winningReindeer) + 1
    }
    return scoresByReindeer.values.max()
}

private val pattern = "(\\w+) can fly (\\d+) km/s for (\\d+) seconds, but then must rest for (\\d+) seconds.".toRegex()
private fun parseStatsByReindeer(input: List<String>) = input.associate {
    val (_, name, speed, time, restTime) = pattern.matchEntire(it)?.groupValues ?: error("Does not match pattern: $it")
    name to Triple(speed.toInt(), time.toInt(), restTime.toInt())
}

private fun Triple<Int, Int, Int>.distanceAfter(seconds: Int): Int {
    val (speed, time, restTime) = this
    val cycles = seconds / (time + restTime)
    val remainingTime = seconds % (time + restTime)
    return cycles * speed * time + min(remainingTime, time) * speed
}

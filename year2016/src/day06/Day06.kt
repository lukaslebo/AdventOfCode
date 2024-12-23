package day06

import check
import readInput

fun main() {
    val testInput = readInput("2016", "Day06_test")
    check(part1(testInput), "easter")
    check(part2(testInput), "advent")

    val input = readInput("2016", "Day06")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.invert().map { it.mostCommonCharacter() }.joinToString("")
private fun part2(input: List<String>) = input.invert().map { it.leastCommonCharacter() }.joinToString("")

private fun String.mostCommonCharacter() = groupingBy { it }.eachCount().maxBy { it.value }.key
private fun String.leastCommonCharacter() = groupingBy { it }.eachCount().minBy { it.value }.key

private fun List<String>.invert(): List<String> {
    val result = first().indices.mapTo(mutableListOf()) { "" }
    for (i in first().indices) {
        for (j in indices) {
            result[i] = result[i] + get(j)[i]
        }
    }
    return result
}

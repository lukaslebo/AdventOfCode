package day18

import check
import readInput

fun main() {
    val testInput = readInput("2016", "Day18_test")
    check(part1(testInput, rowCount = 10), 38)

    val input = readInput("2016", "Day18")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>, rowCount: Int = 40) = calculateSafeTilesIn(input.first(), rowCount)
private fun part2(input: List<String>) = calculateSafeTilesIn(input.first(), 400000)

private fun calculateSafeTilesIn(firstRow: String, rowCount: Int): Int {
    val map = mutableListOf(firstRow)
    while (map.size < rowCount) {
        map += map.last().evolve()
    }
    return map.sumOf { it.countSafeTiles() }
}

private val trapPatterns = setOf("^^.", ".^^", "^..", "..^")

private fun String.evolve() = ".$this.".windowed(3) {
    if (it in trapPatterns) "^" else "."
}.joinToString("")

private fun String.countSafeTiles() = count { it == '.' }

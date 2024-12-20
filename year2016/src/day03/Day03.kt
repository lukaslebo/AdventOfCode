package day03

import readInput

fun main() {
    val input = readInput("2016", "Day03")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.parseFromRows().countValidTriangles()

private fun part2(input: List<String>) = input.parseFromCols().countValidTriangles()

private fun List<String>.parseFromRows(): List<Triple<Int, Int, Int>> = map { line ->
    line.split(" ")
        .mapNotNull { part -> part.trim().takeIf { it.isNotBlank() }?.toInt() }
        .let { (a, b, c) -> Triple(a, b, c) }
}

private fun List<String>.parseFromCols(): List<Triple<Int, Int, Int>> = parseFromRows()
    .let { list -> list.map { it.first } + list.map { it.second } + list.map { it.third } }
    .chunked(3)
    .map { (a, b, c) -> Triple(a, b, c) }

private fun List<Triple<Int, Int, Int>>.countValidTriangles() = count {
    val (a, b, c) = it.toList().sorted()
    a + b > c
}

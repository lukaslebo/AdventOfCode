package day21

import mirrorLeftRight
import readInput
import rotate180
import rotate270
import rotate90
import kotlin.math.sqrt

fun main() {
    val input = readInput("2017", "Day21")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = countPixelsAfter(iterations = 5, rules = input.parseRules())
private fun part2(input: List<String>) = countPixelsAfter(iterations = 18, rules = input.parseRules())

private fun countPixelsAfter(iterations: Int, rules: Map<PixelGrid, PixelGrid>): Int {
    var pixelGrid = ".#./..#/###".toPixelGrid()
    repeat(iterations) {
        pixelGrid = pixelGrid.evolve(rules)
    }
    return pixelGrid.countPixel()
}

private typealias PixelGrid = List<List<Boolean>>

private fun PixelGrid.evolve(rules: Map<PixelGrid, PixelGrid>): PixelGrid {
    val n = if (size % 2 == 0) 2 else 3
    return divideBy(n).map { rules.getValue(it) }.merge()
}

private fun PixelGrid.divideBy(n: Int): List<PixelGrid> {
    val parts = mutableListOf<PixelGrid>()
    val gc = size / n
    for (y0 in 0 until gc) {
        for (x0 in 0 until gc) {
            val y = y0 * n
            val x = x0 * n
            parts += subList(y, y + n).map { it.subList(x, x + n) }
        }
    }
    return parts
}

private fun List<PixelGrid>.merge(): PixelGrid {
    val n = sqrt(size.toDouble()).toInt()
    return windowed(size = n, step = n) { parts ->
        val result = mutableListOf<List<Boolean>>()
        for (y in parts.first().indices) {
            result += parts.flatMap { it[y] }
        }
        result
    }.flatten()
}

private fun PixelGrid.countPixel(): Int = sumOf { line -> line.count { it } }

private fun String.toPixelGrid(): PixelGrid = split("/").map { line -> line.map { it == '#' } }

private fun List<String>.parseRules(): Map<PixelGrid, PixelGrid> {
    return flatMap { line ->
        val (input, output) = line.split(" => ").map { it.toPixelGrid() }
        val mirrored = input.mirrorLeftRight()
        listOf(
            input to output,
            input.rotate90() to output,
            input.rotate180() to output,
            input.rotate270() to output,
            mirrored to output,
            mirrored.rotate90() to output,
            mirrored.rotate180() to output,
            mirrored.rotate270() to output,
        )
    }.toMap()
}

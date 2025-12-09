package day09

import check
import readInput
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

fun main() {
    val testInputA = readInput("2025", "Day09_test_a")
    check(part1(testInputA), 50)
    check(part2(testInputA), 24)

    // Make sure solution is not naive (see https://leonardschuetz.ch/blog/aoc-2025/day9/#addendum)
    val testInputB = readInput("2025", "Day09_test_b")
    check(part1(testInputB), 80)
    check(part2(testInputB), 32)

    val input = readInput("2025", "Day09")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Long {
    val positions = input.parsePositions()
    var maxRectangle = 0L
    for ((a, b) in positions.createPairs()) {
        val rectangle = rectangleArea(a, b)
        maxRectangle = max(maxRectangle, rectangle)
    }
    return maxRectangle
}

private fun part2(input: List<String>): Long {
    val positions = input.parsePositions()
    val polygonEdges = (positions + positions.first()).windowed(2).map { (a, b) -> a to b }
    var maxRectangle = 0L
    for ((a, b) in positions.createPairs()) {
        val rectangle = rectangleArea(a, b)
        if (rectangle < maxRectangle) continue
        val diagonal = (a to b).diagonal()
        val point = diagonal.middle()
        if (polygonEdges.any { it intersects diagonal } || !point.isInside(polygonEdges)) continue
        maxRectangle = max(maxRectangle, rectangle)
    }
    return maxRectangle
}

private data class Pos(val x: Long, val y: Long)

private fun List<String>.parsePositions() = map { line ->
    val (x, y) = line.split(",").map { it.toLong() }
    Pos(x, y)
}

private fun List<Pos>.createPairs() = flatMapIndexed { i, a ->
    subList(i + 1, size).map { b -> a to b }
}

private fun rectangleArea(a: Pos, b: Pos): Long {
    val dx = (a.x - b.x).absoluteValue + 1
    val dy = (a.y - b.y).absoluteValue + 1
    return dx * dy
}

private fun Pair<Pos, Pos>.min() = Pos(min(first.x, second.x), min(first.y, second.y))
private fun Pair<Pos, Pos>.max() = Pos(max(first.x, second.x), max(first.y, second.y))
private fun Pair<Pos, Pos>.middle() = Pos((first.x + second.x) / 2, (first.y + second.y) / 2)
private fun Pair<Pos, Pos>.diagonal() = min() to max()

private infix fun Pair<Pos, Pos>.intersects(line: Pair<Pos, Pos>): Boolean {
    val (minX1, minY1) = min()
    val (maxX1, maxY1) = max()

    val (minX2, minY2) = line.min()
    val (maxX2, maxY2) = line.max()

    val noIntersection = minX1 >= maxX2 || maxX1 <= minX2 || minY1 >= maxY2 || maxY1 <= minY2
    return !noIntersection
}

private fun Pos.isInside(polygonEdges: List<Pair<Pos, Pos>>): Boolean {
    val up = this to Pos(x, Long.MIN_VALUE)
    val down = this to Pos(x, Long.MAX_VALUE)
    val left = this to Pos(Long.MIN_VALUE, y)
    val right = this to Pos(Long.MAX_VALUE, y)

    return polygonEdges.count { it intersects up } % 2 != 0 &&
            polygonEdges.count { it intersects down } % 2 != 0 &&
            polygonEdges.count { it intersects left } % 2 != 0 &&
            polygonEdges.count { it intersects right } % 2 != 0
}

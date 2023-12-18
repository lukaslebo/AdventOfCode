package year2023.day18

import check
import readInput
import kotlin.math.absoluteValue

fun main() {
    val testInput = readInput("2023", "Day18_test")
    check(part1(testInput), 62)
    check(part2(testInput), 952408144115)

    val input = readInput("2023", "Day18")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.parsePolygonEdges().polygonArea()
private fun part2(input: List<String>) = input.repairInstructions().parsePolygonEdges().polygonArea()

private data class Pos(val x: Long, val y: Long) {
    fun up(steps: Int = 1) = Pos(x, y - steps)
    fun down(steps: Int = 1) = Pos(x, y + steps)
    fun left(steps: Int = 1) = Pos(x - steps, y)
    fun right(steps: Int = 1) = Pos(x + steps, y)

    fun moveInDir(dir: String, steps: Int = 1) = when (dir) {
        "U" -> up(steps)
        "D" -> down(steps)
        "L" -> left(steps)
        "R" -> right(steps)
        else -> error("unknown $dir")
    }
}

@OptIn(ExperimentalStdlibApi::class)
private fun List<String>.repairInstructions(): List<String> {
    fun String.toDir() = when (this@toDir) {
        "3" -> "U"
        "1" -> "D"
        "2" -> "L"
        "0" -> "R"
        else -> error("Cannot convert ${this@toDir}")
    }

    return map { line ->
        val (steps, dir) = line.split(' ').last()
            .removePrefix("(#").removeSuffix(")")
            .chunked(5)
            .let {
                it.first().hexToInt() to it.last().toDir()
            }
        "$dir $steps"
    }
}

private fun List<String>.parsePolygonEdges() = dropLast(1).fold(mutableListOf(Pos(0,0))) { edges, line ->
    val (dir, steps) = line.split(' ')
    edges += edges.last().moveInDir(dir, steps.toInt())
    edges
}

private fun List<Pos>.polygonArea(): Long {
    val vertices = (this + first()).windowed(2)
    val area = vertices.sumOf { (a,b) -> (b.x + a.x) * (b.y - a.y) }.absoluteValue / 2
    val border = vertices.sumOf { (a, b) -> (b.x - a.x + b.y - a.y).absoluteValue }
    return area + border / 2 + 1
}

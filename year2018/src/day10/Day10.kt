package day10

import readInput

fun main() {
    val input = readInput("2018", "Day10")
    part1(input)
}

private fun part1(input: List<String>) {
    val stars = input.parseStars()
    val limit = if (stars.size > 31) 10 else 7
    var time = 0
    while (stars.height() > limit) {
        stars.tick()
        time++
    }
    stars.print()
    println(time)
}


private data class Pos(val x: Long, val y: Long) {
    operator fun plus(other: Pos) = Pos(x + other.x, y + other.y)
    operator fun times(factor: Int) = Pos(x * factor, y * factor)

}

private class Star(var pos: Pos, val velocity: Pos)

private fun List<String>.parseStars() = map { line ->
    val (x, y, vx, vy) = line.replace("position=<", "")
        .replace("> velocity=<", ", ")
        .replace(">", "")
        .split(",")
        .map { it.trim().toLong() }
    Star(Pos(x, y), Pos(vx, vy))
}

private fun List<Star>.tick(times: Int = 1) = forEach { it.pos += it.velocity * times }

private fun List<Star>.print() {
    val starPositions = map { it.pos }.toSet()
    val xRange = minOf { it.pos.x }..maxOf { it.pos.x }
    val yRange = minOf { it.pos.y }..maxOf { it.pos.y }
    if (xRange.last - xRange.first > 1000 || yRange.last - yRange.first > 1000) error("picture too big")
    for (y in yRange) {
        for (x in xRange) {
            val c = if (Pos(x, y) in starPositions) '#' else '.'
            print(c)
        }
        println()
    }
}

private fun List<Star>.height() = maxOf { it.pos.y } - minOf { it.pos.y }

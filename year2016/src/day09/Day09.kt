package day09

import check
import readInput

fun main() {
    check(part1("X(8x2)(3x3)ABCY"), 18)
    check(part2("(25x3)(3x3)ABC(2x3)XY(5x2)PQRSTX(18x9)(3x2)TWO(5x7)SEVEN"), 445)
    check(part2("(27x12)(20x12)(13x14)(7x10)(1x12)A"), 241920)

    val input = readInput("2016", "Day09").first()
    println(part1(input))
    println(part2(input))
}

private fun part1(input: String) = calculateDecompressedSize(input)

private fun part2(input: String) = calculateDecompressedSize(input, nestedCompression = true)

private fun calculateDecompressedSize(data: String, nestedCompression: Boolean = false): Long {
    val markerPattern = "\\((?<length>\\d+)x(?<times>\\d+)\\)".toRegex()
    val weights = data.map { 1 }.toMutableList()
    var size = 0L
    var i = 0
    while (i < data.length) {
        val match = markerPattern.matchAt(data, i)
        if (match != null) {
            val length = match.groups["length"]!!.value.toInt()
            val times = match.groups["times"]!!.value.toInt()
            i = match.range.last
            if (nestedCompression) {
                for (j in (i + 1..i + length)) {
                    weights[j] = weights[j] * times
                }
            } else {
                size += length * times
                i += length
            }
        } else {
            size += weights[i]
        }
        i++
    }
    return size
}

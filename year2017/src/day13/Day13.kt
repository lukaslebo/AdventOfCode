package day13

import check
import readInput

fun main() {
    val testInput = readInput("2017", "Day13_test")
    check(part1(testInput), 24)
    check(part2(testInput), 10)

    val input = readInput("2017", "Day13")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val scannerByDepth = input.map { it.parseSecurityScanner() }.associateBy { it.depth }
    val maxDepth = scannerByDepth.keys.max()
    var severity = 0
    for (posAndTime in 0..maxDepth) {
        val scanner = scannerByDepth[posAndTime] ?: continue
        if (scanner.getPos(posAndTime) == 0) {
            severity += scanner.depth * scanner.range
        }
    }
    return severity
}

private fun part2(input: List<String>): Int {
    val scannerByDepth = input.map { it.parseSecurityScanner() }.associateBy { it.depth }
    val maxDepth = scannerByDepth.keys.max()

    fun isCaught(delay: Int): Boolean {
        for (pos in 0..maxDepth) {
            val scanner = scannerByDepth[pos] ?: continue
            if (scanner.getPos(pos + delay) == 0) {
                return true
            }
        }
        return false
    }

    var delay = 0
    while (isCaught(delay)) delay++


    return delay
}

private data class SecurityScanner(
    val depth: Int,
    val range: Int,
) {
    fun getPos(time: Int): Int {
        val roundTripPos = time % (2 * range - 2)
        return if (roundTripPos < range) roundTripPos else range - (roundTripPos - range) - 2
    }
}

private fun String.parseSecurityScanner(): SecurityScanner {
    val (depth, range) = split(": ")
    return SecurityScanner(depth = depth.toInt(), range = range.toInt())
}

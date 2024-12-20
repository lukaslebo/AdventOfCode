package day25

import check
import readInput
import kotlin.math.abs
import kotlin.math.pow

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day25_test")
    check(part1(testInput), "2=-1=0")

    val input = readInput("2022", "Day25")
    println(part1(input))
}

private fun part1(input: List<String>): String {
    return input.sumOf { it.toDecimal() }.toSnafu()
}

private fun String.toDecimal(): Long {
    val snafu = this
    var num = 0L
    for (i in snafu.indices) {
        val c = snafu[i]
        num += c.times() * 5.0.pow(snafu.lastIndex - i).toLong()
    }
    return num
}

private fun Char.times() = when (this) {
    '0' -> 0
    '1' -> 1
    '2' -> 2
    '-' -> -1
    '=' -> -2
    else -> error("Factor not available for $this")
}

private fun Long.toSnafu(): String {
    var exp = 0
    var pow = 5.0.pow(exp++).toLong()
    var maxNumWithRemainingExp = 2 * pow

    while (this > pow) {
        pow = 5.0.pow(exp++).toLong()
        maxNumWithRemainingExp += 2 * pow
    }
    pow = 5.0.pow(exp).toLong()
    maxNumWithRemainingExp += 2 * pow

    var total = 0L
    var snafu = ""
    while (exp >= 0) {
        pow = 5.0.pow(exp--).toLong()
        maxNumWithRemainingExp -= 2 * pow

        val diff = abs(total - this)
        var times = diff / pow
        val minRequiredNumInRemainingExp = if (diff > pow) diff - pow else diff
        if (exp > 0 && times < 2 && minRequiredNumInRemainingExp > maxNumWithRemainingExp) {
            times++
        }

        if (total > this) {
            total -= times * pow
            snafu += when (times) {
                0L -> "0"
                1L -> "-"
                2L -> "="
                else -> error("!")
            }
        } else {
            total += times * pow
            snafu += when (times) {
                0L -> "0"
                1L -> "1"
                2L -> "2"
                else -> error("!")
            }
        }
    }
    return snafu.trimStart { it == '0' }
}

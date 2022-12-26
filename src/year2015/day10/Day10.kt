package year2015.day10

import readInput

fun main() {
    val input = readInput("2015", "Day10")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = lookAndSaySequenceLength(input.first(), 40)

private fun part2(input: List<String>) = lookAndSaySequenceLength(input.first(), 50)

private fun lookAndSaySequenceLength(input: String, rounds: Int): Int {
    var num = input
    repeat(rounds) {
        val segments = mutableListOf<String>()
        var segment = num.first()
        var segmentSize = 1
        for (c in num.substring(1)) {
            if (c == segment) {
                segmentSize++
            } else {
                segments += segmentSize.toString() + segment
                segment = c
                segmentSize = 1
            }
        }
        segments += segmentSize.toString() + segment
        num = segments.joinToString("")
    }

    return num.length
}

package day05

import readInput

fun main() {
    val input = readInput("2017", "Day05")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.countSteps { it + 1 }
private fun part2(input: List<String>) = input.countSteps { if (it >= 3) it - 1 else it + 1 }

private fun List<String>.countSteps(offsetModification: (Int) -> Int): Int {
    val offsets = map { it.toInt() }.toMutableList()
    var index = 0
    var steps = 0
    while (index in offsets.indices) {
        val offset = offsets[index]
        offsets[index] = offsetModification(offset)
        index += offset
        steps++
    }
    return steps
}

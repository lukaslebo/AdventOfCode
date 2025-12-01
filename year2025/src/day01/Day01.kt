package day01

import check
import readInput
import kotlin.math.absoluteValue

fun main() {
    val testInput = readInput("2025", "Day01_test")
    check(part1(testInput), 3)
    check(part2(testInput), 6)

    val input = readInput("2025", "Day01")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val rotations = input.map { it.parseRotation() }
    var zeros = 0
    rotateSafeDial(rotations, onRotationCompleted = { if (it == 0) zeros++ })
    return zeros
}

private fun part2(input: List<String>): Int {
    val rotations = input.map { it.parseRotation() }
    var zeros = 0
    rotateSafeDial(rotations, onClick = { if (it == 0) zeros++ })
    return zeros
}

private fun rotateSafeDial(
    rotations: List<Int>,
    onClick: (safeDial: Int) -> Unit = {},
    onRotationCompleted: (safeDial: Int) -> Unit = {},
) {
    var safeDial = 50
    for (rotation in rotations) {
        val increment = rotation.coerceIn(-1..1)
        repeat(rotation.absoluteValue) {
            safeDial += increment
            safeDial %= 100
            onClick(safeDial)
        }
        onRotationCompleted(safeDial)
    }
}

private fun String.parseRotation() = drop(1).toInt() * if (startsWith("L")) -1 else 1

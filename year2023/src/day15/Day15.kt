package day15

import check
import readInput

fun main() {
    val testInput = readInput("2023", "Day15_test")
    check(part1(testInput), 1320)
    check(part2(testInput), 145)

    val input = readInput("2023", "Day15")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.first().split(',').sumOf { it.hash() }
private fun part2(input: List<String>): Int {
    val boxes = List(256) { LinkedHashMap<String, Int>() }
    for (operation in input.parseOperations()) {
        val box = boxes[operation.label.hash()]
        when (operation) {
            is Put -> box[operation.label] = operation.focalLength
            is Remove -> box.remove(operation.label)
        }
    }
    return boxes.focalPower()
}

private fun String.hash(): Int = fold(0) { acc, c ->
    ((acc + c.code) * 17) % 256
}

private sealed interface Operation {
    val label: String
}

private data class Remove(override val label: String) : Operation
private data class Put(override val label: String, val focalLength: Int) : Operation

private fun List<String>.parseOperations() = first().split(',').map { line ->
    if (line.endsWith("-")) Remove(line.dropLast(1))
    else Put(line.substringBefore("="), line.substringAfter("=").toInt())
}

private fun List<Map<String, Int>>.focalPower() = flatMapIndexed { boxIndex, box ->
    box.entries.mapIndexed { lensIndex, (_, focusLength) ->
        (boxIndex + 1) * (lensIndex + 1) * focusLength
    }
}.sum()

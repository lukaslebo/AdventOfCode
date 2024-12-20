package day13

import check
import readInput

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day13_test")
    check(part1(testInput), 13)
    check(part2(testInput), 140)

    val input = readInput("2022", "Day13")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int = input.toPairs()
    .mapIndexed { i, p ->
        val (left, right) = p
        val inOrder = compareSignals(left, right) == -1
        inOrder to i + 1
    }
    .filter { it.first }
    .sumOf { it.second }

private fun part2(input: List<String>): Int {
    val packet1 = "[[2]]"
    val packet2 = "[[6]]"
    val sortedInput = (input + packet1 + packet2).filter { it.isNotEmpty() }.sortedWith(signalComparator)
    val packetPos1 = sortedInput.indexOf(packet1) + 1
    val packetPos2 = sortedInput.indexOf(packet2) + 1
    return packetPos1 * packetPos2
}

private val signalComparator = Comparator<String> { a, b -> compareSignals(a, b) }

private fun compareSignals(left: String, right: String): Int {
    val leftSignal = left.removeOuterBrackets()
    val rightSignal = right.removeOuterBrackets()

    var index = 0
    var leftPacket = leftSignal.getPacket(index)
    var rightPacket = rightSignal.getPacket(index)

    while (leftPacket != null && rightPacket != null) {
        if (leftPacket.isArray() || rightPacket.isArray()) {
            val result = compareSignals(leftPacket, rightPacket)
            if (result != 0) return result
        } else {
            val comparison = leftPacket.toInt().compareTo(rightPacket.toInt())
            if (comparison != 0) return comparison
        }
        index++
        leftPacket = leftSignal.getPacket(index)
        rightPacket = rightSignal.getPacket(index)
    }
    return when {
        leftPacket == null && rightPacket != null -> -1
        leftPacket != null -> 1
        else -> 0
    }
}


private fun List<String>.toPairs() = (this + "").chunked(3).map { Pair(it[0], it[1]) }

private fun String.isArray() = startsWith("[")

private fun String.removeOuterBrackets() = if (isArray()) substring(1, lastIndex) else this

private fun String.getPacket(index: Int): String? {
    if (isEmpty()) return null
    var brackets = 0
    val commas = mutableListOf<Int>()
    forEachIndexed { i, char ->
        when (char) {
            '[' -> brackets++
            ']' -> brackets--
            ',' -> if (brackets == 0) commas += i
        }
    }
    if (index > commas.size) return null
    val start = if (index == 0) 0 else commas[index - 1] + 1
    val end = if (index > commas.lastIndex) length else commas[index]
    return substring(start, end)
}

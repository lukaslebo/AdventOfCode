package day21

import check
import readInput
import kotlin.math.absoluteValue

fun main() {
    val testInput = readInput("2016", "Day21_test")
    check(part1(testInput, password = "abcde"), "decab")
    check(part2(testInput, scrambledPassword = "decab"), "abcde")

    val input = readInput("2016", "Day21")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>, password: String = "abcdefgh"): String {
    return input.map { it.parseScramble() }.fold(password) { text, scramble ->
        scramble.scramble(text)
    }
}

private fun part2(input: List<String>, scrambledPassword: String = "fbgdceah"): String {
    val reversedScrambles = input.map { it.parseScramble() }.reversed()
    return reversedScrambles.fold(scrambledPassword) { text, scramble ->
        scramble.undo(text)
    }
}

private sealed interface Scramble {
    fun scramble(text: String): String
    fun undo(text: String): String
}

private data class SwapPositions(
    val posA: Int,
    val posB: Int,
) : Scramble {
    override fun scramble(text: String) = text.swapPositions(posA, posB)
    override fun undo(text: String) = scramble(text)
}

private data class SwapLetters(
    val letterA: String,
    val letterB: String,
) : Scramble {
    override fun scramble(text: String) = text.swapPositions(text.indexOf(letterA), text.indexOf(letterB))
    override fun undo(text: String) = scramble(text)
}

private data class RotateRight(val n: Int) : Scramble {
    override fun scramble(text: String) = text.rotate(n)
    override fun undo(text: String) = text.rotate(-n)
}

private data class RotateLeft(val n: Int) : Scramble {
    override fun scramble(text: String) = text.rotate(-n)
    override fun undo(text: String) = text.rotate(n)
}

private data class RotateBasedOnPosition(val letter: String) : Scramble {
    override fun scramble(text: String): String {
        val n = text.indexOf(letter)
        val additional = if (n >= 4) 2 else 1
        val steps = n + additional
        return text.rotate(steps)
    }

    override fun undo(text: String): String {
        return (0..text.lastIndex).map { text.rotate(-it) }.first { scramble(it) == text }
    }
}

private data class ReverseRange(val range: IntRange) : Scramble {
    override fun scramble(text: String) = text.take(range.first) +
            text.substring(range).reversed() +
            text.substring(range.last + 1)

    override fun undo(text: String) = scramble(text)
}

private data class MovePosition(val from: Int, val to: Int) : Scramble {
    override fun scramble(text: String) = move(text = text, from = from, to = to)
    override fun undo(text: String) = move(text = text, from = to, to = from)

    private fun move(text: String, from: Int, to: Int): String {
        val list = text.toMutableList()
        val letter = list.removeAt(from)
        list.add(to, letter)
        return list.joinToString("")
    }
}

private fun String.swapPositions(posA: Int, posB: Int): String {
    val array = toCharArray()
    val tmp = array[posA]
    array[posA] = array[posB]
    array[posB] = tmp
    return array.concatToString()
}

private fun String.rotate(n: Int): String {
    val offset = n.absoluteValue % length
    val remaining = length - offset
    return if (n >= 0) takeLast(offset) + take(remaining)
    else takeLast(remaining) + take(offset)
}

private fun String.parseScramble(): Scramble {
    val parts = split(" ")
    return when {
        startsWith("swap position") -> SwapPositions(parts[2].toInt(), parts.last().toInt())
        startsWith("swap letter") -> SwapLetters(parts[2], parts.last())
        startsWith("rotate right") -> RotateRight(parts[2].toInt())
        startsWith("rotate left") -> RotateLeft(parts[2].toInt())
        startsWith("rotate based on position of letter") -> RotateBasedOnPosition(parts.last())
        startsWith("reverse positions") -> ReverseRange(parts[2].toInt()..parts.last().toInt())
        startsWith("move position") -> MovePosition(parts[2].toInt(), parts.last().toInt())
        else -> error("could not parse scramble: $this")
    }
}

package year2015.day12

import readInput

fun main() {
    val input = readInput("2015", "Day12")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.first()
    .split("(\\[|]|,|:|\\{|})".toRegex())
    .sumOf { it.toIntOrNull() ?: 0 }

private fun part2(input: List<String>) = sumOfNotRed(input.first())

private fun sumOfNotRed(input: String): Int {
    if (input.isArray()) {
        return input.removeBrackets().children().sumOf { it.toIntOrNull() ?: sumOfNotRed(it) }
    }
    if (input.isObject()) {
        val children = input.removeBrackets().children()
        val hasRed = children.any { it.startsWith("\"red\":") || it.endsWith(":\"red\"") }
        if (hasRed) return 0
        val values = children.map { it.substringAfter(':') }
        return values.sumOf { it.toIntOrNull() ?: sumOfNotRed(it) }
    }
    return input.toIntOrNull() ?: 0
}

private fun String.isObject() = startsWith("{")
private fun String.isArray() = startsWith("[")
private fun String.removeBrackets() = substring(1, lastIndex)
private fun String.children(): List<String> {
    if (isEmpty()) return emptyList()

    val children = mutableListOf<String>()
    var start = 0
    var bracketCount = 0
    this.forEachIndexed { i, c ->
        when (c) {
            '{', '[' -> bracketCount++
            '}', ']' -> bracketCount--
        }
        if (c == ',' && bracketCount == 0) {
            children += substring(start, i)
            start = i + 1
        }
    }
    children += substring(start, length)
    return children
}
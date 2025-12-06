package day06

import check
import readInput

fun main() {
    val testInput = readInput("2025", "Day06_test").restoreTrailingSpaces()
    check(part1(testInput), 4277556)
    check(part2(testInput), 3263827)

    val input = readInput("2025", "Day06").restoreTrailingSpaces()
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Long {
    fun List<String>.parseNums() = dropLast(1).map { line ->
        line.trim().split("\\s+".toRegex()).map { it.toLong() }
    }

    fun List<String>.parseOperations() = last().trim().split("\\s+".toRegex())

    val numbers = input.parseNums()
    val operations = input.parseOperations()

    val results = mutableListOf<Long>()
    for (i in numbers.first().indices) {
        val operation = operations[i]
        results += when (operation) {
            "+" -> numbers.sumOf { it[i] }
            "*" -> numbers.map { it[i] }.reduce(Long::times)
            else -> error("unknown operation $operation")
        }
    }
    return results.sum()
}

private fun part2(input: List<String>): Long {
    val result = mutableListOf<Long>()
    val nums = mutableListOf<Long>()
    for (i in input.first().indices.reversed()) {
        nums += input.dropLast(1).joinToString("") { it[i].toString() }.trim().toLongOrNull() ?: continue
        val operation = input.last()[i]
        result += when (operation) {
            '+' -> nums.sum()
            '*' -> nums.reduce(Long::times)
            else -> continue
        }
        nums.clear()
    }
    return result.sum()
}

/** Restores trailing spaces removed by IDE */
private fun List<String>.restoreTrailingSpaces(): List<String> {
    val maxLength = maxOf { it.length }
    return map { it.padEnd(maxLength, ' ') }
}

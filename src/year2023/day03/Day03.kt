package year2023.day03

import check
import readInput

fun main() {
    val testInput = readInput("2023", "Day03_test")
    check(part1(testInput), 4361)
    check(part2(testInput), 467835)

    val input = readInput("2023", "Day03")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val partNums = mutableListOf<Int>()
    for (y in input.indices) {
        val line = input[y]
        val nums = line.splitIntoNums()
        var startIndex = 0
        partNums += nums.filter {
            val numIndex = line.indexOf(it.toString(), startIndex)
            val length = it.toString().length
            startIndex = numIndex + length
            val adjacentParts = input.getAdjacentParts(numIndex, y, length)
            adjacentParts.isNotEmpty()
        }
    }
    return partNums.sum()
}

private fun part2(input: List<String>): Int {
    val gearRatios = mutableListOf<Int>()
    for (y in input.indices) {
        val line = input[y]
        val gearIndices = line.getAllIndexOf('*')
        gearRatios += gearIndices.map { input.getSurroundingNumbers(it, y) }
            .filter { it.size == 2 }
            .map { (num1, num2) -> num1 * num2 }
    }
    return gearRatios.sum()
}

private fun String.splitIntoNums() = split("[^0-9]".toRegex()).mapNotNull { it.toIntOrNull() }

private fun List<String>.getAdjacentParts(x: Int, y: Int, xSize: Int): List<Char> {
    val adjacent = mutableListOf<Char?>()
    for (yi in y - 1..y + 1) {
        for (xi in x - 1..(x + xSize)) {
            adjacent += getOrNull(y - 1)?.getOrNull(xi)
            adjacent += getOrNull(y)?.getOrNull(xi)
            adjacent += getOrNull(y + 1)?.getOrNull(xi)
        }
    }
    return adjacent.filterNotNull().filter { !it.isDigit() && it != '.' }
}

private fun String.getAllIndexOf(char: Char): List<Int> {
    val indices = mutableListOf<Int>()
    var startIndex = 0
    while (true) {
        val index = indexOf(char, startIndex)
        if (index == -1) {
            return indices
        }
        indices += index
        startIndex = index + 1
    }
}

private fun List<String>.getSurroundingNumbers(x: Int, y: Int): List<Int> {
    val nums = mutableListOf<Int>()
    for (yi in y - 1..y + 1) {
        val line = getOrNull(yi) ?: continue
        val numsOnLine = line.splitIntoNums()
        var startIndex = 0
        for (num in numsOnLine) {
            val numIndex = line.indexOf(num.toString(), startIndex)
            startIndex = numIndex + num.toString().length
            val xCoverageByNum = (numIndex - 1..startIndex)
            if (x in xCoverageByNum) {
                nums += num
            }
        }
    }
    return nums
}

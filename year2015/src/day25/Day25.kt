package day25

import check
import readInput

fun main() {
    val testInput = readInput("2015", "Day25_test")
    check(part1(testInput), 11661866)

    val input = readInput("2015", "Day25")
    println(part1(input))
}

private const val FIRST_CODE = 20151125L
private const val MOD = 33554393L
private const val FACTOR = 252533L

private fun part1(input: List<String>): Int {
    val (row, col) = input.first().split(',').map { it.toInt() }
    val num = toNum(row, col)
    var code = FIRST_CODE

    repeat(num - 1) {
        code = (code * FACTOR) % MOD
    }
    return code.toInt()
}

private fun toNum(targetRow: Int, targetCol: Int): Int {
    var num = 1
    var row = 1
    var col = 1
    var maxRow = 1
    while (true) {
        while (row > 1) {
            row--
            col++
            num++
            if (targetRow == row && targetCol == col) return num
        }

        if (row == 1) {
            row = ++maxRow
            col = 1
            num++
            if (targetRow == row && targetCol == col) return num
        }
    }
}

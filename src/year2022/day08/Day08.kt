package year2022.day08

import check
import readInput

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day08_test")
    check(part1(testInput), 21)
    check(part2(testInput), 8)

    val input = readInput("2022", "Day08")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    return parseTreeMap(input).countVisibleTrees()
}

private fun part2(input: List<String>): Int {
    return parseTreeMap(input).getHighestTreeScenicScore()
}

private fun parseTreeMap(input: List<String>): List<List<Int>> {
    return input.map { line ->
        line.map { it.digitToInt() }
    }
}

private fun List<List<Int>>.countVisibleTrees(): Int {
    val maxY = lastIndex
    val maxX = first().lastIndex

    var count = 2 * (maxY + maxX)
    for (y in 1 until maxY) {
        for (x in 1 until maxX) {
            if (isVisible(y, x)) count++
        }
    }

    return count
}

private fun List<List<Int>>.isVisible(y: Int, x: Int): Boolean {
    val height = get(y)[x]
    var isVisibleTop = true
    var isVisibleBottom = true
    var isVisibleLeft = true
    var isVisibleRight = true
    for (topY in 0 until y) {
        if (get(topY)[x] >= height) {
            isVisibleTop = false
            break
        }
    }
    if (isVisibleTop) return true
    for (bottomY in y + 1..lastIndex) {
        if (get(bottomY)[x] >= height) {
            isVisibleBottom = false
            break
        }
    }
    if (isVisibleBottom) return true
    for (leftX in 0 until x) {
        if (get(y)[leftX] >= height) {
            isVisibleLeft = false
            break
        }
    }
    if (isVisibleLeft) return true
    for (rightX in x + 1..first().lastIndex) {
        if (get(y)[rightX] >= height) {
            isVisibleRight = false
            break
        }
    }
    return isVisibleRight
}

private fun List<List<Int>>.getHighestTreeScenicScore(): Int {
    val maxY = lastIndex
    val maxX = first().lastIndex

    val scores = mutableListOf<Int>()
    for (y in 1 until maxY) {
        for (x in 1 until maxX) {
            scores += computeTreeScenicScore(y, x)
        }
    }

    return scores.max()
}

private fun List<List<Int>>.computeTreeScenicScore(y: Int, x: Int): Int {
    val height = get(y)[x]
    var countTop = 0
    var countBottom = 0
    var countLeft = 0
    var countRight = 0
    for (topY in (0 until y).reversed()) {
        countTop++
        if (get(topY)[x] >= height) break
    }
    for (bottomY in (y + 1..lastIndex)) {
        countBottom++
        if (get(bottomY)[x] >= height) break
    }
    for (leftX in (0 until x).reversed()) {
        countLeft++
        if (get(y)[leftX] >= height) break
    }
    for (rightX in x + 1..first().lastIndex) {
        countRight++
        if (get(y)[rightX] >= height) break
    }
    return countTop * countBottom * countLeft * countRight
}

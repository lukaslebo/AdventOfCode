package day14

import check
import readInput

fun main() {
    val testInput = readInput("2018", "Day14_test")
    check(part1(testInput), 5941429882L)
    check(part2(listOf("51589")), 9)
    check(part2(listOf("01245")), 5)
    check(part2(listOf("92510")), 18)
    check(part2(listOf("59414")), 2018)

    val input = readInput("2018", "Day14")
    println(part1(input))
    println(part2(input))
    // 552860567 too high
}

private fun part1(input: List<String>): Long {
    val requiredRecipes = input.first().toInt()
    val recipeScoreBoard = RecipeScoreBoard()
    while (recipeScoreBoard.recipes.size < requiredRecipes + 10) {
        recipeScoreBoard.createRecipe()
    }
    return recipeScoreBoard.recipes.takeLast(10).joinToString("").toLong()
}

private fun part2(input: List<String>): Int {
    val requiredSequence = input.first().map { it.digitToInt() }
    val recipeScoreBoard = RecipeScoreBoard()
    while (true) {
        recipeScoreBoard.createRecipe()
        if (recipeScoreBoard.endsWith(requiredSequence)) {
            return recipeScoreBoard.recipes.size - requiredSequence.size
        }
        if (recipeScoreBoard.endsWith(requiredSequence, shiftLeft = 1)) {
            return recipeScoreBoard.recipes.size - requiredSequence.size - 1
        }
    }
}

class RecipeScoreBoard {
    val recipes = mutableListOf(3, 7)
    var elfIndices = listOf(0, 1)

    fun createRecipe() {
        val sum = elfIndices.sumOf { recipes[it] }
        if (sum >= 10) recipes += listOf(sum / 10, sum % 10)
        else recipes += sum
        elfIndices = elfIndices.map { (it + 1 + recipes[it]) % recipes.size }
    }

    fun endsWith(sequence: List<Int>, shiftLeft: Int = 0): Boolean {
        return recipes.endsWith(sequence, shiftLeft)
    }
}

private fun List<Int>.endsWith(sequence: List<Int>, shiftLeft: Int = 0): Boolean {
    if (sequence.size + shiftLeft > size) return false
    val offset = size - sequence.size - shiftLeft
    for (i in sequence.indices) {
        if (sequence[i] != this[offset + i]) return false
    }
    return true
}

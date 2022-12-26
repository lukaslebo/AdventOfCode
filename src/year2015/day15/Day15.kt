package year2015.day15

import readInput
import kotlin.math.max

fun main() {
    val input = readInput("2015", "Day15")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = getBestScore(input.map { Ingredient.of(it) }.toSet())

private fun part2(input: List<String>) = getBestScore(
    input.map { Ingredient.of(it) }.toSet(),
    caloriesTarget = 500,
)

private data class Ingredient(
    val name: String,
    val capacity: Int,
    val durability: Int,
    val flavor: Int,
    val texture: Int,
    val calories: Int,
) {
    companion object {
        private val pattern =
            "(\\w+): capacity (-?\\d+), durability (-?\\d+), flavor (-?\\d+), texture (-?\\d+), calories (-?\\d+)".toRegex()

        fun of(string: String): Ingredient {
            val groups = pattern.matchEntire(string)?.groupValues ?: error("Not matching pattern: $string")
            return Ingredient(
                name = groups[1],
                capacity = groups[2].toInt(),
                durability = groups[3].toInt(),
                flavor = groups[4].toInt(),
                texture = groups[5].toInt(),
                calories = groups[6].toInt(),
            )
        }
    }
}

private fun getBestScore(
    ingredients: Set<Ingredient>,
    remainingTeaSpoons: Int = 100,
    capacityScore: Int = 0,
    durabilityScore: Int = 0,
    flavorScore: Int = 0,
    textureScore: Int = 0,
    calories: Int = 0,
    caloriesTarget: Int? = null,
): Int {
    if (remainingTeaSpoons == 0 && (caloriesTarget == null || calories == caloriesTarget))
        return listOf(capacityScore, durabilityScore, flavorScore, textureScore)
            .map { max(0, it) }
            .reduce(Int::times)
    else if (remainingTeaSpoons == 0) return 0

    val ingredient = ingredients.first()
    val remainingIngredients = ingredients - ingredient
    return if (remainingIngredients.isEmpty()) getBestScore(
        ingredients = remainingIngredients,
        remainingTeaSpoons = 0,
        capacityScore = capacityScore + ingredient.capacity * remainingTeaSpoons,
        durabilityScore = durabilityScore + ingredient.durability * remainingTeaSpoons,
        flavorScore = flavorScore + ingredient.flavor * remainingTeaSpoons,
        textureScore = textureScore + ingredient.texture * remainingTeaSpoons,
        calories = calories + ingredient.calories * remainingTeaSpoons,
        caloriesTarget = caloriesTarget,
    )
    else (0..remainingTeaSpoons).maxOf {
        getBestScore(
            ingredients = remainingIngredients,
            remainingTeaSpoons = remainingTeaSpoons - it,
            capacityScore = capacityScore + ingredient.capacity * it,
            durabilityScore = durabilityScore + ingredient.durability * it,
            flavorScore = flavorScore + ingredient.flavor * it,
            textureScore = textureScore + ingredient.texture * it,
            calories = calories + ingredient.calories * it,
            caloriesTarget = caloriesTarget,
        )
    }
}
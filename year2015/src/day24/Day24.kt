package day24

import check
import readInput

fun main() {
    val testInput = readInput("2015", "Day24_test")
    check(part1(testInput), 99)
    check(part2(testInput), 44)

    val input = readInput("2015", "Day24")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Long {
    val presents = input.map { it.toLong() }
    val targetWeight = presents.sum() / 3
    val combinations = getTargetWeightCombinations(presents, targetWeight, 6)
    val validCombinations = combinations.filter {
        val remainingWeights = presents - it
        hasTargetWeightCombinations(remainingWeights, targetWeight, remainingWeights.size / 2)
    }
    val minSize = validCombinations.minOf { it.size }
    val minQE = validCombinations.filter { it.size == minSize }.minBy { it.reduce(Long::times) }
    return minQE.reduce(Long::times)
}

private fun part2(input: List<String>): Long {
    val presents = input.map { it.toLong() }
    val targetWeight = presents.sum() / 4
    val combinations = getTargetWeightCombinations(presents, targetWeight, 5)
    val validCombinations = combinations.filter {
        val remainingWeights = presents - it
        hasTargetWeightCombinations(remainingWeights, targetWeight, remainingWeights.size / 2)
    }
    val minSize = validCombinations.minOf { it.size }
    val minQE = validCombinations.filter { it.size == minSize }.minBy { it.reduce(Long::times) }
    return minQE.reduce(Long::times)
}

private fun getTargetWeightCombinations(
    remainingWeights: List<Long>,
    targetWeight: Long,
    maxSize: Int,
    weight: Long = 0,
    combination: Set<Long> = emptySet(),
    cache: MutableMap<Set<Long>, Set<Set<Long>>> = hashMapOf(),
): Set<Set<Long>> {
    if (weight > targetWeight || combination.size > maxSize) return emptySet()
    if (weight == targetWeight) return setOf(combination)
    return cache.getOrPut(combination) {
        remainingWeights.flatMapTo(hashSetOf()) {
            getTargetWeightCombinations(
                remainingWeights = remainingWeights.filter { w -> w > it },
                targetWeight = targetWeight,
                maxSize = maxSize,
                weight = weight + it,
                combination = combination + it,
                cache = cache,
            )
        }
    }
}

private fun hasTargetWeightCombinations(
    remainingWeights: List<Long>,
    targetWeight: Long,
    maxSize: Int,
    weight: Long = 0,
    combination: Set<Long> = emptySet(),
    cache: MutableMap<Set<Long>, Boolean> = hashMapOf(),
): Boolean {
    if (weight > targetWeight || combination.size > maxSize) return false
    if (weight == targetWeight) return true
    return cache.getOrPut(combination) {
        remainingWeights.any {
            hasTargetWeightCombinations(
                remainingWeights = remainingWeights.filter { w -> w > it },
                targetWeight = targetWeight,
                maxSize = maxSize,
                weight = weight + it,
                combination = combination + it,
                cache = cache,
            )
        }
    }
}

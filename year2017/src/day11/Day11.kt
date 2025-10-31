package day11

import check
import readInput
import kotlin.math.min

fun main() {
    check(part1(listOf("ne,ne,ne")), 3)
    check(part1(listOf("ne,ne,sw,sw")), 0)
    check(part1(listOf("ne,ne,s,s")), 2)
    check(part1(listOf("se,sw,se,sw,sw")), 3)

    val input = readInput("2017", "Day11")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.first().split(",").countDirectPathSteps()

private fun part2(input: List<String>): Int {
    val path = input.first().split(",")
    return path.indices.maxOf { path.take(it).countDirectPathSteps() }
}

private fun List<String>.countDirectPathSteps() = groupBy { it }
    .mapValues { it.value.size }
    .toMutableMap()
    .normalize("sw" to "se", "s")
    .normalize("nw" to "ne", "n")
    .normalize("s" to "ne", "se")
    .normalize("s" to "nw", "sw")
    .normalize("n" to "se", "ne")
    .normalize("n" to "sw", "nw")
    .normalize("nw" to "se")
    .normalize("ne" to "sw")
    .normalize("n" to "s")
    .values.sum()

private fun MutableMap<String, Int>.normalize(
    dirsToNormalize: Pair<String, String>,
    normalizedDir: String? = null,
): MutableMap<String, Int> {
    val (dirA, dirB) = dirsToNormalize
    val countA = getOrDefault(dirA, 0)
    val countB = getOrDefault(dirB, 0)
    val overlap = min(countA, countB)
    val countNewDir = getOrDefault(normalizedDir, 0)

    put(dirA, countA - overlap)
    put(dirB, countB - overlap)
    if (normalizedDir != null) {
        put(normalizedDir, countNewDir + overlap)
    }
    return this
}

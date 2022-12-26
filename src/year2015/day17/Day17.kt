package year2015.day17

import readInput

fun main() {
    val input = readInput("2015", "Day17")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = getCombinations(input.toContainers(), 150).size

private fun part2(input: List<String>) = getCombinations(input.toContainers(), 150)
    .groupBy { it.size }
    .minBy { it.key }.value.size

private data class Container(val capacity: Int, val index: Int)

private fun List<String>.toContainers() = mapIndexed { index, num -> Container(num.toInt(), index) }.toSet()

private fun getCombinations(
    remainingContainers: Set<Container>,
    remainingEggnog: Int,
    containers: Set<Container> = emptySet(),
    cache: MutableMap<Pair<Set<Container>, Int>, Set<Set<Container>>> = hashMapOf(),
): Set<Set<Container>> {
    if (remainingEggnog < 0) return emptySet()
    if (remainingEggnog == 0) return setOf(containers)

    val key = Pair(remainingContainers, remainingEggnog)
    return cache.getOrPut(key) {
        val result = mutableSetOf<Set<Container>>()
        for (container in remainingContainers) {
            result += getCombinations(
                remainingContainers = remainingContainers - container,
                remainingEggnog = remainingEggnog - container.capacity,
                containers = containers + container,
                cache = cache,
            )
        }
        return@getOrPut result
    }
}
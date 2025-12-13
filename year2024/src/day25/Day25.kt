package day25

import check
import readInput
import util.splitByEmptyLines

fun main() {
    val testInput = readInput("2024", "Day25_test")
    check(part1(testInput), 3)

    val input = readInput("2024", "Day25")
    println(part1(input))
}

private fun part1(input: List<String>): Int {
    val (locks, keys) = input.parseLocksAndKeys()
    var matches = 0
    for (lock in locks) {
        for (key in keys) {
            if (lock matches key) matches++
        }
    }
    return matches
}

private infix fun Lock.matches(key: Key) =
    heights.zip(key.heights).map { it.first + it.second }.none { it > 5 }

private fun List<String>.parseLocksAndKeys(): Pair<List<Lock>, List<Key>> {
    val tumblerLineGroups = splitByEmptyLines()
    val tumblers = tumblerLineGroups.map { tumblerLineGroup ->
        val heights = mutableListOf<Int>()
        for (i in tumblerLineGroup.first().indices) {
            heights += tumblerLineGroup.count { it[i] == '#' } - 1
        }
        val isLock = tumblerLineGroup.first().all { it == '#' }
        val isKey = tumblerLineGroup.last().all { it == '#' }
        when {
            isLock -> Lock(heights)
            isKey -> Key(heights)
            else -> error("cant determine type")
        }
    }
    val locks = tumblers.filterIsInstance<Lock>()
    val keys = tumblers.filterIsInstance<Key>()
    return locks to keys
}

private sealed interface Tumbler {
    val heights: List<Int>
}

private data class Lock(override val heights: List<Int>) : Tumbler

private data class Key(override val heights: List<Int>) : Tumbler

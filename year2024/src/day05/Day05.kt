package day05

import check
import readInput
import splitByEmptyLines

fun main() {
    val testInput = readInput("2024", "Day05_test")
    check(part1(testInput), 143)
    check(part2(testInput), 123)

    val input = readInput("2024", "Day05")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val (rules, updates) = input.parseRulesAndUpdates()
    return updates.filter { !it.violatesRules(rules) }.sumOf { it.middle() }
}

private fun part2(input: List<String>): Int {
    val (rules, updates) = input.parseRulesAndUpdates()
    return updates.filter { it.violatesRules(rules) }.sumOf { it.fixOrder(rules).middle() }
}

private data class Rule(
    val before: Int,
    val after: Int,
)

private fun List<String>.parseRulesAndUpdates(): Pair<List<Rule>, List<List<Int>>> {
    val (rulesText, updatesText) = splitByEmptyLines()
    val rules = rulesText.map {
        val (a, b) = it.split("|")
        Rule(a.toInt(), b.toInt())
    }
    val updates = updatesText.map { line ->
        line.split(",").map { it.toInt() }
    }
    return rules to updates
}

private fun List<Int>.violatesRules(rules: List<Rule>): Boolean {
    val afterNumbersByBefore = rules.groupBy { it.before }.mapValues { (_, rules) -> rules.map { it.after }.toSet() }
    val passedNumbers = mutableSetOf<Int>()
    for (num in this) {
        val afterNumbers = afterNumbersByBefore[num] ?: emptySet()
        if (passedNumbers.intersect(afterNumbers).isNotEmpty()) return true
        passedNumbers += num
    }
    return false
}

private fun List<Int>.fixOrder(rules: List<Rule>) = sortedWith { a, b ->
    when {
        rules.any { it.after == a && it.before == b } -> -1
        rules.any { it.before == a && it.after == b } -> 1
        else -> 0
    }
}

private fun List<Int>.middle(): Int = get(size / 2)

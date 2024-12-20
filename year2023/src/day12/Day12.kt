package day12

import check
import readInput

fun main() {
    val testInput = readInput("2023", "Day12_test")
    check(part1(testInput), 21)
    check(part2(testInput), 525152L)

    val input = readInput("2023", "Day12")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.toConditionRecords().sumOf { it.combinations() }

private fun part2(input: List<String>): Long {
    return input.toConditionRecords().sumOf { it.unfold(5).combinations() }
}

private data class ConditionRecord(
    val record: String,
    val groups: List<Int>,
)

private fun List<String>.toConditionRecords() = map {
    ConditionRecord(
        record = it.split(' ').first(),
        groups = it.split(' ').last().split(',').map(String::toInt),
    )
}

private fun ConditionRecord.unfold(factor: Int) = ConditionRecord(
    record = (1..factor).joinToString("?") { record },
    groups = (1..factor).flatMap { groups },
)

private fun ConditionRecord.combinations() = combinations(record = record, groups = groups)

private fun combinations(
    record: String,
    groups: List<Int>,
    cache: MutableMap<Pair<String, List<Int>>, Long> = HashMap(),
): Long {
    if (groups.isEmpty()) return if ("#" in record) 0 else 1
    if (record.isEmpty()) return 0

    return cache.getOrPut(record to groups) {
        var result = 0L
        if (record.first() in ".?") {
            result += combinations(record = record.drop(1), groups = groups, cache = cache)
        }
        if (record.first() in "#?" && record.length >= groups.first() && "." !in record.take(groups.first()) && record.getOrNull(groups.first()) != '#') {
            result += combinations(record = record.drop(groups.first() + 1), groups = groups.drop(1), cache = cache)
        }
        result
    }
}

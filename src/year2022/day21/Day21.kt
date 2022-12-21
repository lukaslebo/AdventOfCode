package year2022.day21

import check
import readInput

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day21_test")
    check(part1(testInput), 152)
    check(part2(testInput), 301)

    val input = readInput("2022", "Day21")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Long {
    return parseNumberSupplierByMonkeyName(input).getValue("root")()
}

private fun part2(input: List<String>): Long {
    val map = parseNumberSupplierByMonkeyName(input)
    val dependentMonkeys = getMonkeysDependingOnHuman(input)

    val (_, monkeyA, _, monkeyB) = input.first { it.startsWith("root: ") }.split(": ").flatMap { it.split(' ') }

    val isMonkeyADependent = monkeyA in dependentMonkeys

    val dependingMonkey = if (isMonkeyADependent) monkeyA else monkeyB
    val targetNum = if (isMonkeyADependent) map.getValue(monkeyB)() else map.getValue(monkeyA)()

    val solution = binarySearchHumanNumbers(map, dependingMonkey, targetNum)
        ?: binarySearchHumanNumbers(map, dependingMonkey, targetNum, true)
        ?: error("no number found")

    val solutions = mutableSetOf<Long>()
    for (num in solution - 100..solution + 100) {
        map["humn"] = { num }
        if (targetNum == map.getValue(dependingMonkey)()) {
            solutions += num
        }
    }
    return solutions.first()
}

private fun binarySearchHumanNumbers(
    map: MutableMap<String, NumberSupplier>,
    monkey: String,
    target: Long,
    descending: Boolean = false,
    min: Long = Long.MIN_VALUE / 1_000_000,
    max: Long = Long.MAX_VALUE / 1_000_000,
): Long? {
    if (min > max) return null
    val mid = (min + max) / 2
    map["humn"] = { mid }
    val num = map.getValue(monkey)()
    return if (num == target) mid
    else if (descending && num < target) binarySearchHumanNumbers(map, monkey, target, true, mid + 1, max)
    else if (!descending && num > target) binarySearchHumanNumbers(map, monkey, target, false, mid + 1, max)
    else binarySearchHumanNumbers(map, monkey, target, descending, min, mid - 1)
}

private fun getMonkeysDependingOnHuman(input: List<String>): Set<String> {
    val dependentMonkeys = mutableSetOf<String>()
    val queue = ArrayDeque<String>().apply { add("humn") }
    while (queue.isNotEmpty()) {
        val name = queue.removeFirst()
        val dependent = input.filter { name in it && !it.startsWith(name) }.map { it.substringBefore(":") }
        dependentMonkeys += dependent
        queue += dependent
    }
    return dependentMonkeys
}

private typealias NumberSupplier = () -> Long

private fun parseNumberSupplierByMonkeyName(input: List<String>): MutableMap<String, NumberSupplier> {
    val map = mutableMapOf<String, NumberSupplier>()
    for (line in input) {
        val (name, job) = line.split(": ")
        val parts = job.split(' ')
        if (parts.size == 1) {
            val num = parts.first().toLong()
            map[name] = { num }
        } else {
            val (monkeyA, operationString, monkeyB) = parts
            val operation: Long.(Long) -> Long = when (operationString) {
                "+" -> Long::plus
                "-" -> Long::minus
                "*" -> Long::times
                "/" -> Long::div
                else -> error("Unknown operation $operationString")
            }
            map[name] = { map.getValue(monkeyA)().operation(map.getValue(monkeyB)()) }
        }
    }
    return map
}

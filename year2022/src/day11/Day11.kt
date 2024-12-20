package day11

import check
import readInput
import java.util.LinkedList

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day11_test")
    check(part1(testInput), 10605)
    check(part2(testInput), 2713310158L)

    val input = readInput("2022", "Day11")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = playMonkeyInTheMiddle(
    monkeysById = parseMonkeysById(input),
    rounds = 20,
    divideWorryLevel = 3,
)

private fun part2(input: List<String>) = playMonkeyInTheMiddle(
    monkeysById = parseMonkeysById(input),
    rounds = 10000,
)

private fun playMonkeyInTheMiddle(monkeysById: Map<Int, Monkey>, rounds: Int, divideWorryLevel: Int = 1): Long {
    val monkeys = monkeysById.values
    val greatestCommonModFactor = monkeys.map { it.mod }.reduce { acc, n -> acc * n }
    val monkeyBusiness = Array(monkeys.size) { 0L }
    repeat(rounds) {
        for (monkey in monkeys) {
            while (monkey.items.isNotEmpty()) {
                var itemWorryLevel = monkey.items.removeFirst()
                itemWorryLevel = monkey.inspect(itemWorryLevel)
                monkeyBusiness[monkey.id]++

                if (divideWorryLevel > 1) {
                    itemWorryLevel /= divideWorryLevel
                } else {
                    itemWorryLevel %= greatestCommonModFactor
                }

                val isDivisible = itemWorryLevel % monkey.mod == 0L
                val nextMonkeyId = if (isDivisible) monkey.monkeyIdTrue else monkey.monkeyIdFalse
                monkeysById[nextMonkeyId]!!.items.add(itemWorryLevel)
            }
        }
    }
    return monkeyBusiness.sorted().takeLast(2).let { it[0] * it[1] }
}

private fun parseMonkeysById(input: List<String>) = (input + "").chunked(7).map { lines ->
    val id = lines[0][7].digitToInt()
    val items = lines[1].substringAfter(':').split(',').mapTo(LinkedList()) { it.trim().toLong() }
    val inspect: Inspect = lines[2].substringAfter("new = old ").let { line ->
        val (operationString, numString) = line.split(' ')
        val num = if (numString == "old") null else numString.toLong()
        val operation: Long.(Long) -> Long = when (operationString) {
            "*" -> Long::times
            "+" -> Long::plus
            else -> error("Operand $operationString not supported")
        }
        { it.operation(num ?: it) }
    }
    val mod = lines[3].split(' ').last().toInt()
    val monkeyIdTrue = lines[4].split(' ').last().toInt()
    val monkeyIdFalse = lines[5].split(' ').last().toInt()
    Monkey(id, items, inspect, mod, monkeyIdTrue, monkeyIdFalse)
}.associateBy { it.id }


typealias Inspect = (Long) -> Long

class Monkey(
    val id: Int,
    val items: LinkedList<Long>,
    val inspect: Inspect,
    val mod: Int,
    val monkeyIdTrue: Int,
    val monkeyIdFalse: Int,
)

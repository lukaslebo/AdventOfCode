package day05

import check
import readInput
import java.util.Stack

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day05_test")
    check(part1(testInput), "CMZ")
    check(part2(testInput), "MCD")

    val input = readInput("2022", "Day05")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): String {
    val (stacksById, instructions) = parseStacksByIdAndInstructions(input)
    return executeInstructions(
        stacksById = stacksById,
        instructions = instructions
    ).joinTopElementsToString()
}

private fun part2(input: List<String>): String {
    val (stacksById, instructions) = parseStacksByIdAndInstructions(input)
    return executeInstructions(
        stacksById = stacksById,
        instructions = instructions,
        useCrateMover9001 = true,
    ).joinTopElementsToString()
}

private val instructionPattern = "move (?<amount>\\d+) from (?<from>\\d+) to (?<to>\\d+)".toRegex()

private data class Instruction(
    val amount: Int,
    val from: Int,
    val to: Int,
)

private fun parseStacksByIdAndInstructions(input: List<String>): Pair<Map<Int, Stack<Char>>, List<Instruction>> {
    val separator = input.indexOfFirst { it.isEmpty() }
    val stacksById = parseStacksById(input.subList(0, separator))
    val instructions = parseInstructions(input.subList(separator + 1, input.size))
    return Pair(stacksById, instructions)
}

private fun parseStacksById(input: List<String>): Map<Int, Stack<Char>> {
    val reversed = input.reversed()
    val stackIds = reversed.first().trim().split("   ").map { it.toInt() }
    val stacksById = stackIds.associateWithTo(LinkedHashMap(stackIds.size)) { Stack<Char>() }
    for (line in reversed.subList(1, reversed.size)) {
        for (i in stackIds.indices) {
            val charIndex = 1 + i * 4
            if (line.lastIndex < charIndex) break
            val stackId = stackIds[i]
            val stack = stacksById[stackId]!!
            val char = line[charIndex]
            if (char != ' ') stack += char
        }
    }
    return stacksById
}

private fun parseInstructions(input: List<String>): List<Instruction> {
    return input.map { line ->
        val groups = instructionPattern.find(line)!!.groups
        val amount = groups["amount"]!!.value.toInt()
        val from = groups["from"]!!.value.toInt()
        val to = groups["to"]!!.value.toInt()
        Instruction(
            amount = amount,
            from = from,
            to = to,
        )
    }
}

private fun executeInstructions(
    stacksById: Map<Int, Stack<Char>>,
    instructions: List<Instruction>,
    useCrateMover9001: Boolean = false,
): Map<Int, Stack<Char>> {
    for (instruction in instructions) {
        val from = stacksById[instruction.from]!!
        val to = stacksById[instruction.to]!!

        if (useCrateMover9001) {
            val temp = Stack<Char>()
            repeat(instruction.amount) { temp += from.pop() }
            repeat(instruction.amount) { to += temp.pop() }
        } else {
            repeat(instruction.amount) { to += from.pop() }
        }
    }
    return stacksById
}

private fun Map<Int, Stack<Char>>.joinTopElementsToString() = values.map { it.peek() }.joinToString("")

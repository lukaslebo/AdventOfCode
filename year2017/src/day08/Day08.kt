package day08

import check
import readInput

fun main() {
    val testInput = readInput("2017", "Day08_test")
    check(part1(testInput), 1)
    check(part2(testInput), 10)

    val input = readInput("2017", "Day08")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val registerValues = mutableMapOf<String, Int>()
    input.map { it.parseInstruction() }.forEach { it.execute(registerValues) }
    return registerValues.values.max()
}

private fun part2(input: List<String>): Int {
    val registerValues = mutableMapOf<String, Int>()
    val instructions = input.map { it.parseInstruction() }
    var maxValue = 0
    for (instruction in instructions) {
        instruction.execute(registerValues)
        maxValue = (registerValues.values.maxOrNull() ?: 0).coerceAtLeast(maxValue)
    }
    return maxValue
}

private fun interface InstructionCondition {
    fun evaluate(registerValues: Map<String, Int>): Boolean
}

private class Instruction(
    val register: String,
    val add: Int,
    val condition: InstructionCondition,
) {
    fun execute(registerValues: MutableMap<String, Int>) {
        if (condition.evaluate(registerValues)) {
            val updatedValue = registerValues.getOrDefault(register, 0) + add
            registerValues += register to updatedValue
        }
    }
}

private fun String.parseInstruction(): Instruction {
    val parts = split(" ")
    val register = parts.first()
    val sign = if (parts[1] == "inc") 1 else -1
    val amount = parts[2].toInt()
    val conditionRegister = parts[4]
    val conditionOperation = parts[5]
    val conditionAmount = parts[6].toInt()
    val condition: InstructionCondition = when (conditionOperation) {
        "==" -> InstructionCondition { it.getOrDefault(conditionRegister, 0) == conditionAmount }
        "!=" -> InstructionCondition { it.getOrDefault(conditionRegister, 0) != conditionAmount }
        "<=" -> InstructionCondition { it.getOrDefault(conditionRegister, 0) <= conditionAmount }
        ">=" -> InstructionCondition { it.getOrDefault(conditionRegister, 0) >= conditionAmount }
        "<" -> InstructionCondition { it.getOrDefault(conditionRegister, 0) < conditionAmount }
        ">" -> InstructionCondition { it.getOrDefault(conditionRegister, 0) > conditionAmount }
        else -> error("condition operation $conditionOperation is not supported")
    }
    return Instruction(
        register = register,
        add = sign * amount,
        condition = condition,
    )
}

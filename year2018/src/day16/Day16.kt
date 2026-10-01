package day16

import kotlin.reflect.KClass
import check
import readInput
import util.splitByEmptyLines

fun main() {
    val testInput = readInput("2018", "Day16_test")
    check(part1(testInput), 1)

    val input = readInput("2018", "Day16")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val (scenarios, _) = input.parseScenariosAndTest()
    return scenarios.count { it.possibleOperations.size >= 3 }
}

private fun part2(input: List<String>): Int {
    val (scenarios, test) = input.parseScenariosAndTest()
    val operationByOpcode = determineOperationByOpcode(scenarios)

    val registers = mutableListOf(0, 0, 0, 0)
    for (operationValues in test) {
        val operation = operationByOpcode.getValue(operationValues.first()).create(operationValues)
        operation.execute(registers)
    }
    return registers.first()
}

private fun determineOperationByOpcode(scenarios: List<Scenario>): Map<Int, KClass<out Operation>> {
    val candidatesByOpcode = (0..15).associateWith { Operation.operations.toMutableSet() }
    for (scenario in scenarios) {
        candidatesByOpcode.getValue(scenario.opcode).removeIf { it !in scenario.possibleOperations }
    }
    while (candidatesByOpcode.values.any { it.size > 1 }) {
        val singles = candidatesByOpcode.values.mapNotNull { it.singleOrNull() }.toSet()
        candidatesByOpcode.values.filter { it.size > 1 }.forEach { it.removeAll(singles) }
    }
    val operationByOpcode = candidatesByOpcode.map { it.key to it.value.single() }.toMap()
    return operationByOpcode
}

private data class Scenario(
    val registersBefore: List<Int>,
    val registersAfter: List<Int>,
    val operationValues: List<Int>,
) {
    val opcode = operationValues.first()
    val possibleOperations = Operation.operations.filter {
        val operation = it.create(operationValues)
        val copy = registersBefore.toMutableList()
        operation.execute(copy)
        registersAfter == copy
    }
}

private fun interface Operation {
    fun execute(registers: MutableList<Int>)

    companion object {
        val operations = listOf(
            AddRegister::class,
            AddImmediate::class,
            MultiplyRegister::class,
            MultiplyImmediate::class,
            BitwiseAndRegister::class,
            BitwiseAndImmediate::class,
            BitwiseOrRegister::class,
            BitwiseOrImmediate::class,
            AssignRegister::class,
            AssignImmediate::class,
            GreaterThanImmediateRegister::class,
            GreaterThanRegisterImmediate::class,
            GreaterThanRegisterRegister::class,
            EqualsImmediateRegister::class,
            EqualsRegisterImmediate::class,
            EqualsRegisterRegister::class,
        )
    }
}

private fun KClass<out Operation>.create(values: List<Int>): Operation = when (this) {
    AddRegister::class -> AddRegister(values)
    AddImmediate::class -> AddImmediate(values)
    MultiplyRegister::class -> MultiplyRegister(values)
    MultiplyImmediate::class -> MultiplyImmediate(values)
    BitwiseAndRegister::class -> BitwiseAndRegister(values)
    BitwiseAndImmediate::class -> BitwiseAndImmediate(values)
    BitwiseOrRegister::class -> BitwiseOrRegister(values)
    BitwiseOrImmediate::class -> BitwiseOrImmediate(values)
    AssignRegister::class -> AssignRegister(values)
    AssignImmediate::class -> AssignImmediate(values)
    GreaterThanImmediateRegister::class -> GreaterThanImmediateRegister(values)
    GreaterThanRegisterImmediate::class -> GreaterThanRegisterImmediate(values)
    GreaterThanRegisterRegister::class -> GreaterThanRegisterRegister(values)
    EqualsImmediateRegister::class -> EqualsImmediateRegister(values)
    EqualsRegisterImmediate::class -> EqualsRegisterImmediate(values)
    EqualsRegisterRegister::class -> EqualsRegisterRegister(values)
    else -> error("unsupported operation $this")
}

private class AddRegister(val values: List<Int>) : Operation {
    override fun execute(registers: MutableList<Int>) {
        registers[values[3]] = registers[values[1]] + registers[values[2]]
    }
}

private class AddImmediate(val values: List<Int>) : Operation {
    override fun execute(registers: MutableList<Int>) {
        registers[values[3]] = registers[values[1]] + values[2]
    }
}

private class MultiplyRegister(val values: List<Int>) : Operation {
    override fun execute(registers: MutableList<Int>) {
        registers[values[3]] = registers[values[1]] * registers[values[2]]
    }
}

private class MultiplyImmediate(val values: List<Int>) : Operation {
    override fun execute(registers: MutableList<Int>) {
        registers[values[3]] = registers[values[1]] * values[2]
    }
}

private class BitwiseAndRegister(val values: List<Int>) : Operation {
    override fun execute(registers: MutableList<Int>) {
        registers[values[3]] = registers[values[1]] and registers[values[2]]
    }
}

private class BitwiseAndImmediate(val values: List<Int>) : Operation {
    override fun execute(registers: MutableList<Int>) {
        registers[values[3]] = registers[values[1]] and values[2]
    }
}

private class BitwiseOrRegister(val values: List<Int>) : Operation {
    override fun execute(registers: MutableList<Int>) {
        registers[values[3]] = registers[values[1]] or registers[values[2]]
    }
}

private class BitwiseOrImmediate(val values: List<Int>) : Operation {
    override fun execute(registers: MutableList<Int>) {
        registers[values[3]] = registers[values[1]] or values[2]
    }
}

private class AssignRegister(val values: List<Int>) : Operation {
    override fun execute(registers: MutableList<Int>) {
        registers[values[3]] = registers[values[1]]
    }
}

private class AssignImmediate(val values: List<Int>) : Operation {
    override fun execute(registers: MutableList<Int>) {
        registers[values[3]] = values[1]
    }
}

private class GreaterThanImmediateRegister(val values: List<Int>) : Operation {
    override fun execute(registers: MutableList<Int>) {
        registers[values[3]] = if (values[1] > registers[values[2]]) 1 else 0
    }
}

private class GreaterThanRegisterImmediate(val values: List<Int>) : Operation {
    override fun execute(registers: MutableList<Int>) {
        registers[values[3]] = if (registers[values[1]] > values[2]) 1 else 0
    }
}

private class GreaterThanRegisterRegister(val values: List<Int>) : Operation {
    override fun execute(registers: MutableList<Int>) {
        registers[values[3]] = if (registers[values[1]] > registers[values[2]]) 1 else 0
    }
}

private class EqualsImmediateRegister(val values: List<Int>) : Operation {
    override fun execute(registers: MutableList<Int>) {
        registers[values[3]] = if (values[1] == registers[values[2]]) 1 else 0
    }
}

private class EqualsRegisterImmediate(val values: List<Int>) : Operation {
    override fun execute(registers: MutableList<Int>) {
        registers[values[3]] = if (registers[values[1]] == values[2]) 1 else 0
    }
}

private class EqualsRegisterRegister(val values: List<Int>) : Operation {
    override fun execute(registers: MutableList<Int>) {
        registers[values[3]] = if (registers[values[1]] == registers[values[2]]) 1 else 0
    }
}

private fun List<String>.parseScenariosAndTest(): Pair<List<Scenario>, List<List<Int>>> {
    splitByEmptyLines()
    val (scenariosText, testText) = joinToString("\n").split("\n\n\n\n") + ""
    val scenarios = scenariosText.split("\n\n").map { scenarioText ->
        val (line1, line2, line3) = scenarioText.split("\n")
        Scenario(
            registersBefore = line1.substringAfter("[").removeSuffix("]").split(", ").map { it.toInt() },
            registersAfter = line3.substringAfter("[").removeSuffix("]").split(", ").map { it.toInt() },
            operationValues = line2.split(" ").map { it.toInt() },
        )
    }
    val test = testText.split("\n").filter { it.isNotBlank() }.map { line -> line.split(" ").map { it.toInt() } }
    return scenarios to test
}


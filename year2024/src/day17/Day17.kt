package day17

import check
import readInput
import kotlin.math.pow

fun main() {
    val testInput1 = readInput("2024", "Day17_test_part1")
    val testInput2 = readInput("2024", "Day17_test_part2")
    check(part1(testInput1), "4,6,3,5,6,3,5,2,1,0")
    check(part2(testInput2), 117440)

    val input = readInput("2024", "Day17")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): String {
    val (computer, program) = input.parseComputerAndProgramm()
    return computer.execute(program).joinToString(",")
}

private fun part2(input: List<String>): Long {
    val (_, program) = input.parseComputerAndProgramm()

    fun calculateRegisterA(start: Long = 0, depth: Int = 0): Long? {
        return if (depth == program.size) start
        else (0..7).firstNotNullOfOrNull {
            val registerA = 8 * start + it
            val output = Computer(registerA).execute(program)
            if (output[0] == program[program.lastIndex - depth]) calculateRegisterA(registerA, depth + 1)
            else null
        }
    }

    return calculateRegisterA() ?: error("solution not found")
}

private data class Computer(
    var registerA: Long,
    var registerB: Long = 0,
    var registerC: Long = 0,
)

private fun List<String>.parseComputerAndProgramm(): Pair<Computer, List<Int>> {
    return Computer(
        registerA = get(0).substringAfterLast(" ").toLong(),
        registerB = get(1).substringAfterLast(" ").toLong(),
        registerC = get(2).substringAfterLast(" ").toLong(),
    ) to get(4).substringAfterLast(" ").split(",").map { it.toInt() }
}

private fun Computer.execute(program: List<Int>): List<Int> {
    val output = mutableListOf<Int>()
    var pointer = 0
    while (pointer in program.indices) {
        val opcode = program[pointer]
        val literalOperand = program[pointer + 1]

        fun comboOperand(): Long = when (literalOperand) {
            0 -> 0L
            1 -> 1L
            2 -> 2L
            3 -> 3L
            4 -> registerA
            5 -> registerB
            6 -> registerC
            else -> error("invalid literalOperand $literalOperand")
        }

        when (opcode) {
            0 -> registerA = (registerA / 2.toDouble().pow(comboOperand().toDouble())).toLong()
            1 -> registerB = registerB.xor(literalOperand.toLong())
            2 -> registerB = comboOperand() % 8
            3 -> if (registerA != 0L) {
                pointer = literalOperand
                continue
            }

            4 -> registerB = registerB.xor(registerC)
            5 -> output += (comboOperand() % 8).toInt()
            6 -> registerB = (registerA / 2.toDouble().pow(comboOperand().toDouble())).toLong()
            7 -> registerC = (registerA / 2.toDouble().pow(comboOperand().toDouble())).toLong()
            else -> error("invalid opcode $opcode")
        }

        pointer += 2
    }

    return output
}

package day23

import readInput
import util.sieveOfEratosthenes
import kotlin.math.absoluteValue

fun main() {
    val input = readInput("2017", "Day23")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val program = Program(instructions = input.parseInstructions())
    while (!program.isStopped()) program.execute()
    return program.mulCount
}

private fun part2(input: List<String>): Int {
    val program = Program(
        instructions = input.parseInstructions(),
        registers = IntArray(8) { if (it == 0) 1 else 0 },
    )
    repeat(input.size) { program.execute() }
    val delta = program.instructions.filterIsInstance<Sub>()
        .last { it.register == 'b' - 'a' }.valueReader.read(program).absoluteValue
    val b = program.registers['b' - 'a']
    val c = program.registers['c' - 'a']
    val primes = sieveOfEratosthenes(c).toSet()
    val nums = (b..c step delta).toList()
    return nums.filter { it !in primes }.size
}

private class Program(
    val instructions: List<Instruction>,
    var instructionIndex: Int = 0,
    val registers: IntArray = IntArray(8),
) {
    var mulCount = 0
    fun isStopped() = instructionIndex !in instructions.indices
    fun currentInstruction() = instructions[instructionIndex]
}

private fun interface Reader {
    fun read(program: Program): Int
}

private sealed interface Instruction
private class Set(val register: Int, val valueReader: Reader) : Instruction
private class Sub(val register: Int, val valueReader: Reader) : Instruction
private class Mul(val register: Int, val valueReader: Reader) : Instruction
private class JumpNotZero(val reader: Reader, val stepReader: Reader) : Instruction

private fun Program.execute() {
    when (val instruction = currentInstruction()) {
        is Set -> registers[instruction.register] = instruction.valueReader.read(this)
        is Sub -> registers[instruction.register] = registers[instruction.register] - instruction.valueReader.read(this)
        is Mul -> {
            registers[instruction.register] = registers[instruction.register] * instruction.valueReader.read(this)
            mulCount++
        }

        is JumpNotZero -> if (instruction.reader.read(this) != 0) {
            instructionIndex += instruction.stepReader.read(this)
            return
        }
    }
    instructionIndex++
}

private fun List<String>.parseInstructions(): List<Instruction> {
    fun String.toReader(): Reader {
        val register = first() - 'a'
        val staticValue = toIntOrNull()
        return if (staticValue != null) Reader { staticValue }
        else Reader { it.registers[register] }
    }

    return map {
        val parts = it.split(" ")
        val instruction = parts.first()
        val register = parts[1].first() - 'a'
        when (instruction) {
            "set" -> Set(register, parts.last().toReader())
            "sub" -> Sub(register, parts.last().toReader())
            "mul" -> Mul(register, parts.last().toReader())
            "jnz" -> JumpNotZero(parts[1].toReader(), parts.last().toReader())
            else -> error("$instruction is not supported")
        }
    }
}

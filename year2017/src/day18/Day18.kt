package day18

import check
import readInput

fun main() {
    val testInputA = readInput("2017", "Day18_test_a")
    val testInputB = readInput("2017", "Day18_test_b")
    check(part1(testInputA), 4)
    check(part2(testInputB), 3)

    val input = readInput("2017", "Day18")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Long {
    val program = Program(instructions = input.map { it.parseInstruction(makeMusic = true) })
    while (!program.isStopped()) {
        val soundsPlayed = program.queueOut.size
        val instruction = program.currentInstruction()
        program.execute()
        if (instruction is Recover && program.queueOut.size > soundsPlayed) {
            return program.queueOut.last()
        }
    }
    error("no solution found")
}

private fun part2(input: List<String>): Int {
    val instructions = input.map { it.parseInstruction(makeMusic = false) }
    val program0 = Program(instructions = instructions)
    val program1 = Program(
        instructions = instructions,
        queueIn = program0.queueOut,
        queueOut = program0.queueIn,
    )
    program1.registers['p' - 'a'] = 1

    while (program0.isRunning() || program1.isRunning()) {
        program0.execute()
        program1.execute()
    }
    return program1.sendCount
}

private class Program(
    val instructions: List<Instruction>,
    var instructionIndex: Int = 0,
    val registers: LongArray = LongArray(26),
    val queueIn: ArrayDeque<Long> = ArrayDeque(),
    val queueOut: ArrayDeque<Long> = ArrayDeque(),
) {
    var sendCount = 0
    fun isRunning() = !isStopped() && !isWaiting()
    fun isStopped() = instructionIndex !in instructions.indices
    fun isWaiting() = currentInstruction() is Receive && queueIn.isEmpty()
    fun currentInstruction() = instructions[instructionIndex]
}

private fun interface Reader {
    fun read(program: Program): Long
}

private sealed interface Instruction

private class Sound(val valueReader: Reader) : Instruction
private class Send(val valueReader: Reader) : Instruction
private class Set(val register: Int, val valueReader: Reader) : Instruction
private class Add(val register: Int, val valueReader: Reader) : Instruction
private class Mul(val register: Int, val valueReader: Reader) : Instruction
private class Mod(val register: Int, val valueReader: Reader) : Instruction
private class Recover(val register: Int) : Instruction
private class Receive(val register: Int) : Instruction
private class JumpGreaterZero(val reader: Reader, val stepReader: Reader) : Instruction

private fun Program.execute() {
    if (isStopped() || isWaiting()) return
    when (val instruction = currentInstruction()) {
        is Set -> registers[instruction.register] = instruction.valueReader.read(this)
        is Add -> registers[instruction.register] =
            registers[instruction.register] + instruction.valueReader.read(this)

        is Mul -> registers[instruction.register] =
            registers[instruction.register] * instruction.valueReader.read(this)

        is Mod -> registers[instruction.register] =
            registers[instruction.register] % instruction.valueReader.read(this)

        is JumpGreaterZero ->
            if (instruction.reader.read(this) > 0L) {
                instructionIndex += instruction.stepReader.read(this).toInt()
                return
            }

        is Sound -> queueOut += instruction.valueReader.read(this)
        is Recover -> if (registers[instruction.register] != 0L) queueOut += queueOut.last()

        is Send -> {
            queueOut += instruction.valueReader.read(this)
            sendCount++
        }

        is Receive -> if (queueIn.isNotEmpty()) registers[instruction.register] =
            queueIn.removeFirst() else return
    }
    instructionIndex++
}

private fun String.parseInstruction(makeMusic: Boolean): Instruction {
    val parts = split(" ")
    fun String.toReader(): Reader {
        val register = first() - 'a'
        val staticValue = toLongOrNull()
        return if (staticValue != null) Reader { staticValue }
        else Reader { it.registers[register] }
    }

    val instruction = parts.first()
    val register = parts[1].first() - 'a'
    return when (instruction) {
        "set" -> Set(register, parts.last().toReader())
        "add" -> Add(register, parts.last().toReader())
        "mul" -> Mul(register, parts.last().toReader())
        "mod" -> Mod(register, parts.last().toReader())
        "jgz" -> JumpGreaterZero(parts[1].toReader(), parts.last().toReader())
        "snd" -> if (makeMusic) Sound(parts[1].toReader()) else Send(parts[1].toReader())
        "rcv" -> if (makeMusic) Recover(register) else Receive(register)
        else -> error("$instruction is not supported")
    }
}

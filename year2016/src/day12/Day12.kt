package day12

import check
import readInput

fun main() {
    val testInput = readInput("2016", "Day12_test")
    check(part1(testInput), 42)

    val input = readInput("2016", "Day12")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val program = input.map { it.parseInstruction() }
    val computer = Computer()
    computer.execute(program)
    return computer.registerA
}

private fun part2(input: List<String>): Int {
    val program = input.map { it.parseInstruction() }
    val computer = Computer(registerC = 1)
    computer.execute(program)
    return computer.registerA
}

private class Computer(
    var programIndex: Int = 0,
    var registerA: Int = 0,
    var registerB: Int = 0,
    var registerC: Int = 0,
    var registerD: Int = 0,
)

private sealed interface Instruction {
    fun Computer.execute()
}

private typealias RegisterReader = Computer.() -> Int
private typealias RegisterWriter = Computer.(Int) -> Unit

private class Copy(
    val reader: RegisterReader,
    val writer: RegisterWriter,
) : Instruction {
    override fun Computer.execute() {
        val value = reader()
        writer(value)
        programIndex++
    }
}

private class Increment(
    val reader: RegisterReader,
    val writer: RegisterWriter,
) : Instruction {
    override fun Computer.execute() {
        val value = reader()
        writer(value + 1)
        programIndex++
    }
}

private class Decrement(
    val reader: RegisterReader,
    val writer: RegisterWriter,
) : Instruction {
    override fun Computer.execute() {
        val value = reader()
        writer(value - 1)
        programIndex++
    }
}

private class JumpNotZero(
    val reader: RegisterReader,
    val jump: Int,
) : Instruction {
    override fun Computer.execute() {
        val value = reader()
        if (value != 0) {
            programIndex += jump
        } else {
            programIndex++
        }
    }
}

private fun Computer.execute(program: List<Instruction>) {
    while (programIndex in program.indices) {
        val instruction = program[programIndex]
        with(instruction) {
            execute()
        }
    }
}

private fun String.parseInstruction(): Instruction {
    fun String?.asReader(): RegisterReader = when (this) {
        "a" -> {
            Computer::registerA
        }

        "b" -> {
            Computer::registerB
        }

        "c" -> {
            Computer::registerC
        }

        "d" -> {
            Computer::registerD
        }

        null -> error("cant read from null")

        else -> {
            val staticValue = toInt()
            val staticReader: RegisterReader = { staticValue }
            staticReader
        }
    }

    fun String?.asWriter(): RegisterWriter = when (this) {
        "a" -> {
            { registerA = it }
        }

        "b" -> {
            { registerB = it }
        }

        "c" -> {
            { registerC = it }
        }

        "d" -> {
            { registerD = it }
        }

        else -> error("cant write to $this")
    }

    val parts = split(" ")
    val command = parts[0]
    val argument1 = parts[1]
    val argument2 = parts.getOrNull(2)

    return when (command) {
        "cpy" -> Copy(argument1.asReader(), argument2.asWriter())
        "inc" -> Increment(argument1.asReader(), argument1.asWriter())
        "dec" -> Decrement(argument1.asReader(), argument1.asWriter())
        "jnz" -> JumpNotZero(argument1.asReader(), argument2!!.toInt())
        else -> error("command $command not supported")
    }
}

package day25

import readInput

fun main() {
    val input = readInput("2016", "Day25")
    println(part1(input))
}

private fun part1(input: List<String>): Int {
    val instructions = input.map { it.parseInstruction() }
    var registerA = 0
    while (true) {
        val computer = Computer(program = instructions, registerA = registerA)
        if (computer.executeWhilePattern()) {
            return registerA
        }
        registerA++
    }
}

private class Computer(
    val program: List<Instruction>,
    var programIndex: Int = 0,
    var registerA: Int = 0,
    var registerB: Int = 0,
    var registerC: Int = 0,
    var registerD: Int = 0,
) {
    val out = mutableListOf<Int>()
}

private sealed interface Instruction {
    fun Computer.execute()
}

private typealias RegisterReader = Computer.() -> Int
private typealias RegisterWriter = Computer.(Int) -> Unit

private class Copy(val reader: RegisterReader, val writer: RegisterWriter) : Instruction {
    override fun Computer.execute() {
        val value = reader()
        writer(value)
        programIndex++
    }
}

private class Increment(val reader: RegisterReader, val writer: RegisterWriter) : Instruction {
    override fun Computer.execute() {
        val value = reader()
        writer(value + 1)
        programIndex++
    }
}

private class Decrement(val reader: RegisterReader, val writer: RegisterWriter) : Instruction {
    override fun Computer.execute() {
        val value = reader()
        writer(value - 1)
        programIndex++
    }
}

private class JumpNotZero(val reader: RegisterReader, val jump: Int) : Instruction {
    override fun Computer.execute() {
        val value = reader()
        if (value != 0) {
            programIndex += jump
        } else {
            programIndex++
        }
    }
}

private class Output(val reader: RegisterReader) : Instruction {
    override fun Computer.execute() {
        val value = reader()
        out += value
        programIndex++
    }
}

private fun Computer.executeWhilePattern(): Boolean {
    val states = mutableSetOf(listOf(programIndex, registerA, registerB, registerC, registerD))
    var outputSize = out.size
    while (programIndex in program.indices) {
        val instruction = program[programIndex]
        with(instruction) {
            execute()
        }
        if (out.size == outputSize) continue
        outputSize = out.size

        if (out.first() != 0 || out.last() !in 0..1 || (out.size > 1 && out.last() == out[out.size - 2])) {
            return false
        }
        val state = listOf(programIndex, registerA, registerB, registerC, registerD)
        if (state in states) {
            return true
        }
        states += state
    }
    return false
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
        "out" -> Output(argument1.asReader())
        else -> error("command $command not supported")
    }
}

package day23

import check
import readInput

fun main() {
    val testInput = readInput("2016", "Day23_test")
    check(part1(testInput), 3)

    val input = readInput("2016", "Day23")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val computer = Computer(
        program = input.map { it.parseInstruction() }.toMutableList(),
        registerA = 7,
    )
    computer.run()
    return computer.registerA
}

private fun part2(input: List<String>): Int {
    val computer = Computer(
        program = input.map { it.parseInstruction() }.toMutableList(),
        registerA = 12,
    )
    computer.run()
    return computer.registerA
}

private class Computer(
    val program: MutableList<Instruction>,
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

private class Copy(val from: Argument, val to: Argument) : Instruction {
    override fun Computer.execute() {
        val value = from.read(this)
        to.write(this, value)
        programIndex++
    }
}

private class Increment(val arg: Argument) : Instruction {
    override fun Computer.execute() {
        val value = arg.read(this)
        arg.write(this, value + 1)
        programIndex++
    }
}

private class Decrement(val arg: Argument) : Instruction {
    override fun Computer.execute() {
        val value = arg.read(this)
        arg.write(this, value - 1)
        programIndex++
    }
}

private class JumpNotZero(val selector: Argument, val jump: Argument) : Instruction {
    override fun Computer.execute() {
        val value = selector.read(this)
        if (value != 0) {
            programIndex += jump.read(this)
        } else {
            programIndex++
        }
    }
}

private class Toggle(val arg1: Argument) : Instruction {
    override fun Computer.execute() {
        val indexToToggle = programIndex + arg1.read(this)
        if (indexToToggle in program.indices) {
            program[indexToToggle] = program[indexToToggle].toggle()
        }
        programIndex++
    }
}

private fun Instruction.toggle(): Instruction = when (this) {
    is Copy -> JumpNotZero(from, to)
    is Decrement -> Increment(arg)
    is Increment -> Decrement(arg)
    is JumpNotZero -> Copy(selector, jump)
    is Toggle -> Increment(arg1)
}

private fun Computer.run() {
    while (programIndex in program.indices) {
        val instruction = program[programIndex]
        with(instruction) {
            execute()
        }
    }
}

private data class Argument(val read: RegisterReader, val write: RegisterWriter)

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

        else -> {
            { error("cant write to ${this@asWriter}") }
        }
    }

    val parts = split(" ")
    val command = parts[0]
    val argument1 = parts[1]
    val argument2 = parts.getOrNull(2)
    fun String?.asArgument() = Argument(asReader(), asWriter())

    return when (command) {
        "cpy" -> Copy(argument1.asArgument(), argument2.asArgument())
        "inc" -> Increment(argument1.asArgument())
        "dec" -> Decrement(argument1.asArgument())
        "jnz" -> JumpNotZero(argument1.asArgument(), argument2.asArgument())
        "tgl" -> Toggle(argument1.asArgument())
        else -> error("command $command not supported")
    }
}

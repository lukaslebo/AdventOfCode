package year2015.day23

import readInput

fun main() {
    val input = readInput("2015", "Day23")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val computer = Computer(input.toInstructions())
    computer.executeProgram()
    return computer.b
}

private fun part2(input: List<String>): Int {
    val computer = Computer(input.toInstructions())
    computer.a = 1
    computer.executeProgram()
    return computer.b
}

private fun List<String>.toInstructions() = map {
    val (instruction, p1, p2) = it.split(", ").flatMap { p -> p.split(' ') } + ""
    when (instruction) {
        "hlf" -> Hlf(p1.toRegister())
        "tpl" -> Tpl(p1.toRegister())
        "inc" -> Inc(p1.toRegister())
        "jmp" -> Jmp(p1.toInt())
        "jie" -> Jie(p1.toRegister(), p2.toInt())
        "jio" -> Jio(p1.toRegister(), p2.toInt())
        else -> error("invalid instruction: $it")
    }
}

private fun String.toRegister() = when (this) {
    "a" -> Register.A
    "b" -> Register.B
    else -> error("$this is not a register")
}

private class Computer(val program: List<Instruction>) {
    var a: Int = 0
    var b: Int = 0
    var instructionIndex = 0

    fun executeProgram() {
        while (instructionIndex in program.indices) {
            val instruction = program[instructionIndex]
            instruction.execute(this)
        }
    }
}

private enum class Register { A, B }
private sealed interface Instruction {
    fun execute(computer: Computer)
}

private data class Hlf(val register: Register) : Instruction {
    override fun execute(computer: Computer) {
        when (register) {
            Register.A -> computer.a /= 2
            Register.B -> computer.b /= 2
        }
        computer.instructionIndex++
    }
}

private data class Tpl(val register: Register) : Instruction {
    override fun execute(computer: Computer) {
        when (register) {
            Register.A -> computer.a *= 3
            Register.B -> computer.b *= 3
        }
        computer.instructionIndex++
    }
}

private data class Inc(val register: Register) : Instruction {
    override fun execute(computer: Computer) {
        when (register) {
            Register.A -> computer.a++
            Register.B -> computer.b++
        }
        computer.instructionIndex++
    }
}

private data class Jmp(val offset: Int) : Instruction {
    override fun execute(computer: Computer) {
        computer.instructionIndex += offset
    }
}

private data class Jie(val register: Register, val offset: Int) : Instruction {
    override fun execute(computer: Computer) {
        val v = when (register) {
            Register.A -> computer.a
            Register.B -> computer.b
        }
        if (v % 2 == 0) {
            computer.instructionIndex += offset
        } else {
            computer.instructionIndex++
        }
    }
}

private data class Jio(val register: Register, val offset: Int) : Instruction {
    override fun execute(computer: Computer) {
        val v = when (register) {
            Register.A -> computer.a
            Register.B -> computer.b
        }
        if (v == 1) {
            computer.instructionIndex += offset
        } else {
            computer.instructionIndex++
        }
    }
}
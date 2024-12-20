package day03

import check
import readInput

fun main() {
    val testInput1 = readInput("2024", "Day03_test_part1")
    val testInput2 = readInput("2024", "Day03_test_part2")
    check(part1(testInput1), 161)
    check(part2(testInput2), 48)

    val input = readInput("2024", "Day03")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int = input.getCommands().filterIsInstance<Mul>().sumOf { it.product }
private fun part2(input: List<String>): Int = input.getCommands().getSumOfActiveMuls()

private sealed interface Command
private data object Do : Command
private data object Dont : Command
private data class Mul(
    val a: Int,
    val b: Int,
) : Command {
    val product = a * b
}

private val pattern = "(do\\(\\)|don't\\(\\)|mul\\(\\d+,\\d+\\))".toRegex()

private fun List<String>.getCommands(): List<Command> {
    return pattern.findAll(joinToString("")).map {
        val text = it.value
        if (text.startsWith("mul")) {
            val (a, b) = text.removePrefix("mul(").removeSuffix(")").split(",")
            Mul(a.toInt(), b.toInt())
        } else if (text == "do()") {
            Do
        } else {
            Dont
        }
    }.toList()
}

private fun List<Command>.getSumOfActiveMuls(): Int {
    var active = true
    var sum = 0
    for (control in this) {
        when (control) {
            is Do -> active = true
            is Dont -> active = false
            is Mul -> {
                if (active) sum += control.product
            }
        }
    }
    return sum
}

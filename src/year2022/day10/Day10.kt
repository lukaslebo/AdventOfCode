package year2022.day10

import check
import readInput

fun main() {
    // test if implementation meets criteria from the description, like:
    val testImage = """
        ##..##..##..##..##..##..##..##..##..##..
        ###...###...###...###...###...###...###.
        ####....####....####....####....####....
        #####.....#####.....#####.....#####.....
        ######......######......######......####
        #######.......#######.......#######.....
    """.trimIndent()

    val testInput = readInput("2022", "Day10_test")
    check(part1(testInput), 13140)
    check(part2(testInput), testImage)

    val input = readInput("2022", "Day10")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int = input.processSignal(0) {
    if (cycle.isRelevant()) value += (register * cycle)
}

private fun part2(input: List<String>): String {
    val image = input.processSignal(Array(6) { Array(40) { '.' } }) {
        value[cycle.y][cycle.x] = pixelValue(cycle, register - 1)
    }
    return image.joinToString("\n") { it.joinToString("") }
}

private class Step<T>(var value: T, val cycle: Int, val register: Int)

private fun <T> List<String>.processSignal(initial: T, step: Step<T>.() -> Unit): T {
    var value = initial
    var cycle = 1
    var register = 1
    for (line in this) {
        value = Step(value, cycle, register).apply { step() }.value
        cycle++
        if (line != "noop") {
            value = Step(value, cycle, register).apply { step() }.value
            cycle++

            register += line.split(' ').last().toInt()
        }
    }
    return value
}

private val Int.x
    get() = (this - 1) % 40

private val Int.y
    get() = (this - 1) / 40

private fun pixelValue(cycle: Int, spritePos: Int) = if (cycle.x in (spritePos..(spritePos + 2))) '#' else '.'

private fun Int.isRelevant(): Boolean = this % 40 == 20
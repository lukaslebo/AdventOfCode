package day10

import check
import com.microsoft.z3.*
import parallelMap
import readInput

fun main() {
    val testInput = readInput("2025", "Day10_test")
    check(part1(testInput), 7)
    check(part2(testInput), 33)

    val input = readInput("2025", "Day10")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) =
    input.parallelMap { it.parseMachine().fewestButtonPressesToTurnIndicatorsOn() }.sum()

private fun part2(input: List<String>) =
    input.parallelMap { it.parseMachine().fewestButtonPressesToTurnJoltageOn() }.sum()

private data class Machine(
    val startingIndicators: List<Boolean>,
    val buttons: List<List<Int>>,
    val startingJoltage: List<Int>,
)

private fun Machine.fewestButtonPressesToTurnIndicatorsOn(): Int {
    data class State(
        val indicators: List<Boolean>,
        val presses: Int = 0,
    )

    val visited = mutableSetOf<List<Boolean>>()
    val queue = ArrayDeque<State>()
    queue += State(indicators = startingIndicators.map { false })
    while (queue.isNotEmpty()) {
        val (indicators, presses) = queue.removeFirst()
        if (indicators == startingIndicators) return presses
        visited += indicators
        for (button in buttons) {
            val next = indicators press button
            if (next in visited) continue
            queue += State(next, presses + 1)
        }
    }
    error("no solution")
}

/*
    Create equations for joltages as sum of button presses and equal to required startingJoltage of machine.
    Then Optimize for min of total presses.
    Example:
       x0  x1    x2  x3    x4    x5
       (3) (1,3) (2) (2,3) (0,2) (0,1)
    0:                     x4   +x5    = 3
    1:     x1                   +x5    = 5
    2:           x2 +x3   +x4          = 4
    3: x0 +x1       +x3                = 7
*/
private fun Machine.fewestButtonPressesToTurnJoltageOn(): Int = with(Context()) {
    operator fun <R : ArithSort> ArithExpr<R>.plus(other: ArithExpr<R>) = mkAdd(this, other)
    fun Int.int(): ArithExpr<IntSort> = mkInt(this)

    val optimization = mkOptimize()
    val buttonPresses = buttons.indices.map { mkIntConst("x$it") }

    for (presses in buttonPresses) {
        optimization.Add(mkGe(presses, 0.int()))
    }
    val totalPresses = buttonPresses.fold(0.int()) { acc, n -> acc + n }

    val joltages = startingJoltage.map { 0.int() }.toMutableList()
    for ((button, presses) in buttons.zip(buttonPresses)) {
        for (i in button) {
            joltages[i] = joltages[i] + presses
        }
    }
    for ((joltage, targetJoltage) in joltages.zip(startingJoltage)) {
        optimization.Add(mkEq(joltage, targetJoltage.int()))
    }

    optimization.MkMinimize(totalPresses)
    require(optimization.Check() == Status.SATISFIABLE)
    val totalPressesEvaluated = optimization.model.evaluate(totalPresses, false)
    require(totalPressesEvaluated is IntNum)
    return totalPressesEvaluated.int
}

private fun String.parseMachine(): Machine {
    val parts = split(" ")
    val startingIndicators = parts.first().removePrefix("[").removeSuffix("]").map { it == '#' }
    val buttons = parts.subList(1, parts.size - 1).map { p ->
        p.removePrefix("(").removeSuffix(")").split(",").map { it.toInt() }
    }
    val joltage = parts.last().removePrefix("{").removeSuffix("}").split(',').map { it.toInt() }
    return Machine(
        startingIndicators = startingIndicators,
        buttons = buttons,
        startingJoltage = joltage,
    )
}

private infix fun List<Boolean>.press(button: List<Int>): List<Boolean> = mapIndexed { i, light ->
    if (i in button) !light else light
}

package day13

import check
import readInput
import splitByEmptyLines

fun main() {
    val testInput = readInput("2024", "Day13_test")
    check(part1(testInput), 480)

    val input = readInput("2024", "Day13")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.parseArcadeMachines()
    .mapNotNull { it.costToWinPrice() }
    .sum()

private const val conversionError = 10_000_000_000_000L
private fun part2(input: List<String>) = input.parseArcadeMachines(conversionError)
    .mapNotNull { it.costToWinPrice() }
    .sum()


private data class Pos(val x: Long, val y: Long) {
    operator fun plus(other: Pos) = Pos(x + other.x, y + other.y)
    operator fun times(factor: Long) = Pos(x * factor, y * factor)
}

private data class ArcadeMachine(
    val buttonA: Pos,
    val buttonB: Pos,
    val prize: Pos,
)

private fun List<String>.parseArcadeMachines(prizeConversionError: Long = 0): List<ArcadeMachine> {
    val buttonPattern = "Button [AB]: X\\+(?<X>\\d+), Y\\+(?<Y>\\d+)".toRegex()
    val prizePattern = "Prize: X=(?<X>\\d+), Y=(?<Y>\\d+)".toRegex()
    fun MatchResult.toPos() = Pos(groups["X"]!!.value.toLong(), groups["Y"]!!.value.toLong())

    return splitByEmptyLines().map { (lineA, lineB, linePrize) ->
        ArcadeMachine(
            buttonA = buttonPattern.matchEntire(lineA)!!.toPos(),
            buttonB = buttonPattern.matchEntire(lineB)!!.toPos(),
            prize = prizePattern.matchEntire(linePrize)!!.toPos() + Pos(prizeConversionError, prizeConversionError),
        )
    }
}

private fun ArcadeMachine.costToWinPrice(): Long? {
    // Solve Equations:
    // a*buttonA.x + b*buttonB.x = prize.x
    // a*buttonA.y + b*buttonB.y = prize.y
    val a = (prize.y * buttonB.x - prize.x * buttonB.y) / (buttonA.y * buttonB.x - buttonA.x * buttonB.y)
    val b = (prize.x - buttonA.x * a) / buttonB.x
    val start = Pos(0, 0)
    val target = start + (buttonA * a) + (buttonB * b)
    val cost = a * 3 + b
    return cost.takeIf { target == prize }
}

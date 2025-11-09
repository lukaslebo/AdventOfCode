package day25

import check
import readInput
import splitByEmptyLines

fun main() {
    val testInput = readInput("2017", "Day25_test")
    check(part1(testInput), 3)

    val input = readInput("2017", "Day25")
    println(part1(input))
}

private fun part1(input: List<String>): Int {
    val touringMachine = input.parseTouringMachine()
    while (touringMachine.steps < touringMachine.targetSteps) {
        touringMachine.execute()
    }
    return touringMachine.tape.count { it }
}

private class TouringMachine(
    var stateId: String,
    val statesById: Map<String, State>,
    val targetSteps: Int,
    var cursor: Int = 0,
    val tape: ArrayDeque<Boolean> = ArrayDeque(listOf(false)),
) {
    var steps = 0

    fun moveRight() {
        cursor++
        if (cursor == tape.size) {
            tape += false
        }
    }

    fun moveLeft() {
        if (cursor == 0) {
            tape.addFirst(false)
        } else {
            cursor--
        }
    }

    fun execute() {
        val state = statesById.getValue(stateId)
        val action = if (tape[cursor]) state.actionOnTrue else state.actionOnFalse
        tape[cursor] = action.valueToWrite
        when (action.move) {
            Direction.Left -> moveLeft()
            Direction.Right -> moveRight()
        }
        stateId = action.nextStateId
        steps++
    }
}

private data class State(
    val id: String,
    val actionOnTrue: StateAction,
    val actionOnFalse: StateAction,
)

private data class StateAction(
    val valueToWrite: Boolean,
    val move: Direction,
    val nextStateId: String,
)

private enum class Direction {
    Left, Right
}

private fun List<String>.parseTouringMachine(): TouringMachine {
    val parts = splitByEmptyLines()
    val (firstLine, secondLine) = parts.first()
    val stateId = firstLine.removePrefix("Begin in state ").removeSuffix(".")
    val targetSteps = secondLine.removePrefix("Perform a diagnostic checksum after ").removeSuffix(" steps.").toInt()
    val stateActions = parts.drop(1).map { it.parseState() }
    return TouringMachine(
        stateId = stateId,
        statesById = stateActions.associateBy { it.id },
        targetSteps = targetSteps,
    )
}

private fun List<String>.parseState(): State {
    val stateId = first().trim().removePrefix("In state ").removeSuffix(":")
    check(get(1).trim() == "If the current value is 0:")
    val actionOnFalse = drop(2).take(3).parseStateAction()
    val actionOnTrue = takeLast(3).parseStateAction()
    return State(
        id = stateId,
        actionOnTrue = actionOnTrue,
        actionOnFalse = actionOnFalse,
    )
}

private fun List<String>.parseStateAction(): StateAction {
    val valueToWrite = first().trim().removePrefix("- Write the value ").removeSuffix(".").toInt() > 0
    val move = get(1).trim().removePrefix("- Move one slot to the ").removeSuffix(".")
        .replaceFirstChar { it.uppercase() }.let { Direction.valueOf(it) }
    val nextStateId = last().trim().removePrefix("- Continue with state ").removeSuffix(".")
    return StateAction(
        valueToWrite = valueToWrite,
        move = move,
        nextStateId = nextStateId,
    )
}

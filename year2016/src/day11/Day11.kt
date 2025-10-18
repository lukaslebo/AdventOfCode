package day11

import check
import readInput
import java.util.PriorityQueue

fun main() {
    val testInput = readInput("2016", "Day11_test")
    check(part1(testInput), 11)

    val input = readInput("2016", "Day11")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int = timeToAssembleProject(input.parseProject())

private fun part2(input: List<String>): Int {
    val additionalItems = listOf(
        Generator(Element.Elerium),
        Microchip(Element.Elerium),
        Generator(Element.Dilithium),
        Microchip(Element.Dilithium),
    )
    val project = input.parseProject()
    val floorsWithAdditionalItems = project.floors.mapIndexed { index, floor ->
        if (index == 0) floor + additionalItems
        else floor
    }
    val actualProject = ProjectState(floors = floorsWithAdditionalItems)
    return timeToAssembleProject(actualProject)
}

private enum class Element {
    Dilithium, Elerium, Hydrogen, Lithium, Plutonium, Promethium, Ruthenium, Strontium, Thulium
}

private sealed interface Item {
    val element: Element
}

private data class Generator(override val element: Element) : Item
private data class Microchip(override val element: Element) : Item

private class ProjectState(
    val floors: List<List<Item>>,
    val elevatorLevel: Int = 0,
    val time: Int = 0,
) {
    val simplifiedState = SimplifiedState(
        elevatorLevel = elevatorLevel,
        generatorsPerLevel = floors.map { floor -> floor.count { it is Generator } },
        chipsPerLevel = floors.map { floor -> floor.count { it is Microchip } },
    )

    val minDistanceToDone = time + floors.mapIndexed { index, floor ->
        (((floor.size + 1) * (floors.lastIndex - index)) - 1).coerceAtLeast(0)
    }.sum()

    fun isDone() = floors.count { it.isNotEmpty() } == 1 && floors.last().isNotEmpty()

    fun canGoDown() = elevatorLevel > 0
    fun canGoUp() = elevatorLevel < floors.lastIndex
}

private data class SimplifiedState(
    val elevatorLevel: Int,
    val generatorsPerLevel: List<Int>,
    val chipsPerLevel: List<Int>,
)

private fun timeToAssembleProject(initialState: ProjectState): Int {
    val queue = PriorityQueue<ProjectState>(compareBy { it.minDistanceToDone })
    queue += initialState
    val seen = mutableSetOf<SimplifiedState>()
    while (queue.isNotEmpty()) {
        val state = queue.poll()
        val currentFloor = state.floors[state.elevatorLevel]
        for (itemsToMove in getPossibleItemCombinations(currentFloor)) {
            if (state.canGoUp()) {
                val next = state.moveIfPossible(itemsToMove, state.elevatorLevel + 1)
                if (next != null && next.isDone()) {
                    return next.time
                }
                if (next != null && next.simplifiedState !in seen) {
                    seen += next.simplifiedState
                    queue += next
                }
            }
            if (state.canGoDown()) {
                val next = state.moveIfPossible(itemsToMove, state.elevatorLevel - 1)
                if (next != null && next.simplifiedState !in seen) {
                    seen += next.simplifiedState
                    queue += next
                }
            }
        }
    }

    error("no solution found")
}

private fun getPossibleItemCombinations(items: List<Item>): List<List<Item>> {
    val result = mutableListOf<List<Item>>()
    for (i in items.indices) {
        val item1 = items[i]
        result += listOf(item1)
        for (j in items.indices) {
            if (j <= i) continue
            val item2 = items[j]
            result += listOf(item1, item2)
        }
    }
    return result
}

private fun ProjectState.moveIfPossible(itemsToMove: List<Item>, targetLevel: Int): ProjectState? {
    val currentFloor = floors[elevatorLevel]
    val remainingFloor = currentFloor - itemsToMove
    if (remainingFloor.isAnyChipFriedOnFloor()) return null

    val targetFloor = floors[targetLevel] + itemsToMove
    if (targetFloor.isAnyChipFriedOnFloor()) return null

    val updatedFloors = floors.mapIndexed { index, floor ->
        when (index) {
            targetLevel -> targetFloor
            elevatorLevel -> remainingFloor
            else -> floor
        }
    }

    return ProjectState(
        floors = updatedFloors,
        elevatorLevel = targetLevel,
        time = time + 1,
    )
}

private fun List<Item>.isAnyChipFriedOnFloor(): Boolean {
    val generators = filterIsInstance<Generator>()
    val chips = filterIsInstance<Microchip>()
    for (chip in chips) {
        val isShielded = generators.any { it.element == chip.element }
        if (isShielded) continue
        val isFried = generators.any { it.element != chip.element }
        if (isFried) return true
    }
    return false
}

private fun List<String>.parseProject(): ProjectState = ProjectState(floors = map { it.parseFloor() })

private fun String.parseFloor(): List<Item> {
    val generatorPattern = "(?<element>\\w+) generator".toRegex()
    val microchipPattern = "(?<element>\\w+)-compatible microchip".toRegex()

    fun String.toElement() = Element.valueOf(replaceFirstChar { it.uppercase() })

    val generators = generatorPattern.findAll(this).map { Generator(it.groups["element"]!!.value.toElement()) }.toList()
    val chips = microchipPattern.findAll(this).map { Microchip(it.groups["element"]!!.value.toElement()) }.toList()
    return generators + chips
}

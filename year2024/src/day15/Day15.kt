package day15

import check
import readInput
import splitByEmptyLines

fun main() {
    val testInput = readInput("2024", "Day15_test")
    check(part1(testInput), 10092)
    check(part2(testInput), 9021)

    val input = readInput("2024", "Day15")
    println(part1(input))
    println(part2(input))
}

private const val debug = false

private fun part1(input: List<String>): Int {
    var (warehouse, moves) = input.parseWarehouseAndMoves()
    for (move in moves) {
        warehouse = warehouse.moveRobot(move)
        if (debug) warehouse.print()
    }
    return warehouse.gpsScore()
}

private fun part2(input: List<String>): Int {
    var (warehouse, moves) = input.parseWarehouseAndMoves()
    warehouse = warehouse.expandWidth()
    for (move in moves) {
        warehouse = warehouse.moveRobot(move)
        if (debug) warehouse.print()
    }
    return warehouse.gpsScore()
}

private data class Pos(val x: Int, val y: Int) {
    companion object {
        val up = Pos(0, -1)
        val down = Pos(0, 1)
        val left = Pos(-1, 0)
        val right = Pos(1, 0)
    }

    operator fun plus(other: Pos) = Pos(x + other.x, y + other.y)
}

private data class InputData(
    val warehouse: Warehouse,
    val robotMoves: List<Pos>,
)

private data class Warehouse(
    val walls: Set<Pos>,
    val robot: Pos,
    val boxes: Set<Set<Pos>>,
    val xRange: IntRange,
    val yRange: IntRange,
)

private fun List<String>.parseWarehouseAndMoves(): InputData {
    val (map, seqLines) = splitByEmptyLines()

    var robot: Pos? = null
    val walls = mutableSetOf<Pos>()
    val boxes = mutableSetOf<Pos>()

    for ((y, line) in map.withIndex()) {
        for ((x, c) in line.withIndex()) {
            val pos = Pos(x, y)
            if (c == '@') robot = pos
            else if (c == '#') walls += pos
            else if (c == 'O') boxes += pos
        }
    }

    val moves = seqLines.joinToString("").map {
        when (it) {
            '^' -> Pos.up
            '>' -> Pos.right
            'v' -> Pos.down
            '<' -> Pos.left
            else -> error("unknown direction $it")
        }
    }

    return InputData(
        warehouse = Warehouse(
            walls = walls,
            robot = robot ?: error("robot not found"),
            boxes = boxes.map { setOf(it) }.toSet(),
            xRange = 0..map.first().lastIndex,
            yRange = 0..map.lastIndex,
        ),
        robotMoves = moves,
    )
}

private fun Warehouse.moveRobot(move: Pos): Warehouse {
    val newRobot = robot + move
    if (newRobot in walls) return this
    val newBoxes = boxes.toMutableList()
    val boxesToMove = ArrayDeque<Set<Pos>>()
    val firstBoxToMove = boxes.find { newRobot in it }
    if (firstBoxToMove != null) boxesToMove += firstBoxToMove

    while (boxesToMove.isNotEmpty()) {
        val box = boxesToMove.removeFirst()
        newBoxes -= box
        val moved = box.map { it + move }.toSet()
        if (moved.any { it in walls }) return this
        val moreBoxesToMove = newBoxes.filter { b -> b.any { it in moved } && b !in boxesToMove }
        boxesToMove += moreBoxesToMove
        newBoxes += moved
    }

    return copy(
        boxes = newBoxes.toSet(),
        robot = newRobot,
    )
}

private fun Warehouse.gpsScore() = boxes.map { list -> list.minBy { it.x } }.sumOf { (x, y) -> x + y * 100 }

private fun Warehouse.expandWidth(): Warehouse {
    fun Pos.expand() = listOf(Pos(x * 2, y), Pos(x * 2 + 1, y))
    return copy(
        walls = walls.flatMap { it.expand() }.toSet(),
        boxes = boxes.map { list -> list.minBy { it.x }.expand().toSet() }.toSet(),
        robot = robot.expand().first(),
        xRange = 0..xRange.last * 2,
    )
}

private fun Warehouse.print(wide: Boolean = false) {
    val leftBoxes = boxes.map { list -> list.minBy { it.x } }
    val rightBoxes = boxes.map { list -> list.maxBy { it.x } }
    for (y in yRange) {
        for (x in xRange) {
            val pos = Pos(x, y)
            when {
                pos == robot -> print("@")
                pos in walls -> print("#")
                !wide && pos in leftBoxes -> print("O")
                pos in leftBoxes -> print("[")
                pos in rightBoxes -> print("]")
                else -> print(".")
            }
        }
        print("\n")
    }
    println()
}

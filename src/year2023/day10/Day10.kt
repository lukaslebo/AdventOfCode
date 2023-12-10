package year2023.day10

import check
import readInput

fun main() {
    val testInput1 = readInput("2023", "Day10_test_part1")
    val testInput2 = readInput("2023", "Day10_test_part2")
    check(part1(testInput1), 8)
    check(part2(testInput2), 8)

    val input = readInput("2023", "Day10")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.toPipeSystem().loop.size / 2

private fun part2(input: List<String>): Int {
    val pipeSystem = input.toPipeSystem()
    val pipeSystemWithSpacing = pipeSystem.withSpacing()
    val inside =
        pipeSystemWithSpacing.pipes.keys - pipeSystemWithSpacing.outsideLoop() - pipeSystemWithSpacing.loop - pipeSystemWithSpacing.spacing
    return inside.size
}

private data class Pos(val x: Int, val y: Int) {
    fun north() = Pos(x, y - 1)
    fun south() = Pos(x, y + 1)
    fun west() = Pos(x - 1, y)
    fun east() = Pos(x + 1, y)
    fun adjacent() = listOf(north(), south(), west(), east())
}

private data class Pipe(
    val north: Boolean = false,
    val east: Boolean = false,
    val south: Boolean = false,
    val west: Boolean = false,
) {
    companion object {
        val vertical = Pipe(north = true, east = false, south = true, west = false)
        val horizontal = Pipe(north = false, east = true, south = false, west = true)
        val northEast = Pipe(north = true, east = true, south = false, west = false)
        val northWest = Pipe(north = true, east = false, south = false, west = true)
        val southWest = Pipe(north = false, east = false, south = true, west = true)
        val southEast = Pipe(north = false, east = true, south = true, west = false)
        val empty = Pipe(north = false, east = false, south = false, west = false)
        val start = Pipe(north = true, east = true, south = true, west = true)

        fun from(symbol: Char) = when (symbol) {
            '|' -> vertical
            '-' -> horizontal
            'L' -> northEast
            'J' -> northWest
            '7' -> southWest
            'F' -> southEast
            '.' -> empty
            'S' -> start
            else -> error("Symbol $symbol is not supported")
        }
    }
}

private open class PipeSystem(val pipes: Map<Pos, Pipe>, val loop: Set<Pos>) {
    val maxX = pipes.keys.maxOf { it.x }
    val maxY = pipes.keys.maxOf { it.y }

    override fun toString(): String {
        return (0..maxY).joinToString("\n") { y ->
            (0..maxX).joinToString("") { x ->
                when (val pipe = pipes.getValue(Pos(x, y))) {
                    Pipe.empty -> " "
                    Pipe.vertical -> "│"
                    Pipe.horizontal -> "─"
                    Pipe.northEast -> "└"
                    Pipe.northWest -> "┘"
                    Pipe.southWest -> "┐"
                    Pipe.southEast -> "┌"
                    else -> error("Unknown pipe: $pipe")
                }
            }
        }
    }
}

private class PipeSystemWithSpacing(pipes: Map<Pos, Pipe>, loop: Set<Pos>, val spacing: Set<Pos>) :
    PipeSystem(pipes, loop)

private fun List<String>.toPipeSystem(): PipeSystem {
    val pipes = mutableMapOf<Pos, Pipe>()
    for ((y, line) in withIndex()) {
        for ((x, c) in line.withIndex()) {
            pipes += Pos(x, y) to Pipe.from(c)
        }
    }
    val startPos = pipes.entries.single { it.value == Pipe.start }.key
    val north = pipes[startPos.north()]?.south ?: false
    val south = pipes[startPos.south()]?.north ?: false
    val west = pipes[startPos.west()]?.east ?: false
    val east = pipes[startPos.east()]?.west ?: false
    pipes[startPos] = listOf(
        Pipe.vertical,
        Pipe.horizontal,
        Pipe.northEast,
        Pipe.northWest,
        Pipe.southWest,
        Pipe.southEast,
    ).single { it.north == north && it.south == south && it.west == west && it.east == east }

    fun Pos.next(): List<Pos> {
        val pipe = pipes.getValue(this)
        return listOfNotNull(
            north().takeIf { pipe.north },
            south().takeIf { pipe.south },
            west().takeIf { pipe.west },
            east().takeIf { pipe.east },
        )
    }

    val loop = mutableSetOf(startPos)
    var next = startPos.next()
    while (next.isNotEmpty()) {
        loop += next
        next = next.flatMap { it.next() }.filter { it !in loop }
    }

    return PipeSystem(pipes, loop)
}


private fun PipeSystem.withSpacing(): PipeSystemWithSpacing {
    val spacedPipes = mutableMapOf<Pos, Pipe>()
    val spacedLoop = mutableSetOf<Pos>()
    val spacing = mutableSetOf<Pos>()
    for (x in 0 until 1 + maxX * 2) {
        for (y in 0 until 1 + maxY * 2) {
            val spacedPos = Pos(x, y)
            val oldPos = Pos(x / 2, y / 2).takeIf { x % 2 == 0 && y % 2 == 0 }
            val pipe = oldPos?.let { pipes.getValue(it) } ?: Pipe.empty
            spacedPipes += spacedPos to pipe
            if (oldPos == null) {
                spacing += spacedPos
            }
            if (oldPos in loop) {
                spacedLoop += spacedPos
            }
        }
    }

    for (spacePos in spacing) {
        val north = spacePos.north() in spacedLoop && spacedPipes.getValue(spacePos.north()).south
        val east = spacePos.east() in spacedLoop && spacedPipes.getValue(spacePos.east()).west
        val south = spacePos.south() in spacedLoop && spacedPipes.getValue(spacePos.south()).north
        val west = spacePos.west() in spacedLoop && spacedPipes.getValue(spacePos.west()).east
        val pipe = listOf(
            Pipe.vertical,
            Pipe.horizontal,
            Pipe.northEast,
            Pipe.northWest,
            Pipe.southWest,
            Pipe.southEast,
        ).firstOrNull { it.north == north && it.south == south && it.west == west && it.east == east }
        if (pipe != null) {
            spacedPipes += spacePos to pipe
            spacedLoop += spacePos
        }
    }

    return PipeSystemWithSpacing(spacedPipes, spacedLoop, spacing)
}

private fun PipeSystem.outsideLoop(): Set<Pos> {
    val outside = pipes.keys.filter { it.x == 0 || it.x == maxX || it.y == 0 || it.y == maxY }.filter { it !in loop }
        .toMutableSet()
    val stack = ArrayDeque<Pos>()
    stack += outside
    while (stack.isNotEmpty()) {
        val pos = stack.removeFirst()
        val adjacentOutside = pos.adjacent().filter { it in pipes && it !in loop && it !in outside }
        outside += adjacentOutside
        stack += adjacentOutside
    }
    return outside
}

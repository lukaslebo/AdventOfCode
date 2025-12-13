package day16

import check
import readInput
import util.parallelMap

fun main() {
    val testInput = readInput("2023", "Day16_test")
    check(part1(testInput), 46)
    check(part2(testInput), 51)

    val input = readInput("2023", "Day16")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.parseCavern().beamQuanta().energizedTiles()

private fun part2(input: List<String>): Int {
    val cavern = input.parseCavern()
    val startingBeamQuanta = mutableSetOf<BeamQuanta>()
    startingBeamQuanta += cavern.xRange.map { BeamQuanta(Pos(it, 0), Direction.Down) }
    startingBeamQuanta += cavern.xRange.map { BeamQuanta(Pos(it, cavern.yRange.last), Direction.Up) }
    startingBeamQuanta += cavern.yRange.map { BeamQuanta(Pos(0, it), Direction.Right) }
    startingBeamQuanta += cavern.yRange.map { BeamQuanta(Pos(cavern.xRange.last, it), Direction.Left) }
    return startingBeamQuanta.parallelMap { cavern.beamQuanta(it).energizedTiles() }.max()
}

private data class Cavern(val mirrors: Map<Pos, Mirror>, val xRange: IntRange, val yRange: IntRange)
private data class Pos(val x: Int, val y: Int) {
    fun move(direction: Direction): Pos {
        return when (direction) {
            Direction.Up -> Pos(x, y - 1)
            Direction.Down -> Pos(x, y + 1)
            Direction.Left -> Pos(x - 1, y)
            Direction.Right -> Pos(x+ 1, y )
        }
    }
}

private data class BeamQuanta(val pos: Pos, val direction: Direction)

private enum class Direction {
    Up, Down, Left, Right;
}

private enum class Mirror(val symbol: Char) {
    Vertical('|'),
    Horizontal('-'),
    Slash('/'),
    BackSlash('\\');

    fun reflect(direction: Direction): Set<Direction> {
        return reflectionsByIncomingDirectionByMirror.getValue(this).getValue(direction)
    }

    companion object {
        fun fromSymbol(symbol: Char) = entries.firstOrNull { it.symbol == symbol }

        private val reflectionsByIncomingDirectionByMirror = mapOf(
            Vertical to mapOf(
                Direction.Up to setOf(Direction.Up),
                Direction.Down to setOf(Direction.Down),
                Direction.Left to setOf(Direction.Up, Direction.Down),
                Direction.Right to setOf(Direction.Up, Direction.Down)
            ),
            Horizontal to mapOf(
                Direction.Up to setOf(Direction.Left, Direction.Right),
                Direction.Down to setOf(Direction.Left, Direction.Right),
                Direction.Left to setOf(Direction.Left),
                Direction.Right to setOf(Direction.Right)
            ),
            Slash to mapOf(
                Direction.Up to setOf(Direction.Right),
                Direction.Down to setOf(Direction.Left),
                Direction.Left to setOf(Direction.Down),
                Direction.Right to setOf(Direction.Up)
            ),
            BackSlash to mapOf(
                Direction.Up to setOf(Direction.Left),
                Direction.Down to setOf(Direction.Right),
                Direction.Left to setOf(Direction.Up),
                Direction.Right to setOf(Direction.Down)
            ),
        )
    }
}

private fun List<String>.parseCavern(): Cavern {
    val mirrors = mutableMapOf<Pos, Mirror>()
    for ((y, line) in withIndex()) {
        for ((x, c) in line.withIndex()) {
            val mirror = Mirror.fromSymbol(c)
            if (mirror != null) {
                mirrors += Pos(x, y) to mirror
            }
        }
    }
    return Cavern(mirrors, first().indices, indices)
}

private fun Cavern.beamQuanta(startingBeamQuanta: BeamQuanta = BeamQuanta(Pos(0, 0), Direction.Right)): Set<BeamQuanta> {
    val beamQuanta = mutableSetOf<BeamQuanta>()
    val stack = ArrayDeque<BeamQuanta>()
    stack += startingBeamQuanta
    while (stack.isNotEmpty()) {
        val current = stack.removeFirst()
        beamQuanta += current
        val mirror = mirrors[current.pos]
        val nextBeamQuanta = mutableSetOf<BeamQuanta>()
        if (mirror == null) {
            nextBeamQuanta += BeamQuanta(current.pos.move(current.direction), current.direction)
        } else {
            nextBeamQuanta += mirror.reflect(current.direction).map { BeamQuanta(current.pos.move(it), it) }
        }
        stack += nextBeamQuanta.filter { it.pos.x in xRange && it.pos.y in yRange && it !in beamQuanta }

    }
    return beamQuanta
}

private fun Set<BeamQuanta>.energizedTiles() = map { it.pos }.toSet().size

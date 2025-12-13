package day22

import check
import readInput
import util.parallelMap

fun main() {
    val testInput = readInput("2023", "Day22_test")
    check(part1(testInput), 5)
    check(part2(testInput), 7)

    val input = readInput("2023", "Day22")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val bricks = input.parseBricks().settleBricks()
    return bricks.parallelMap { (bricks - it).isStable() }.count { it }
}

private fun part2(input: List<String>): Int {
    val bricks = input.parseBricks().settleBricks()
    return bricks.parallelMap { (bricks - it).countFallingBricks() }.sum()
}

private data class Pos(val x: Int, val y: Int, val z: Int) {
    fun down() = Pos(x, y, z - 1)
}

private data class Brick(val volume: Set<Pos>, val id: Int = nextId()) {
    fun down() = Brick(volume.mapTo(mutableSetOf()) { it.down() }, id)

    companion object {
        private var idSeq = 0
        private fun nextId() = idSeq++
    }
}

private fun List<String>.parseBricks() = map { line ->
    val (start, end) = line.split('~', ',').map { it.toInt() }.chunked(3)
    val (startX, startY, startZ) = start
    val (endX, endY, endZ) = end
    val volume = mutableSetOf<Pos>()
    for (x in startX..endX) {
        for (y in startY..endY) {
            for (z in startZ..endZ) {
                volume += Pos(x, y, z)
            }
        }
    }
    Brick(volume)
}

private fun List<Brick>.settleBricks(): List<Brick> {
    var bricksToSettle = ArrayDeque(this)
    val occupiedVolume = flatMap { it.volume }.toMutableSet()
    do {
        var moved = false
        val newBricksToSettle = ArrayDeque<Brick>()
        while (bricksToSettle.isNotEmpty()) {
            val currentBrick = bricksToSettle.removeFirst()
            occupiedVolume -= currentBrick.volume
            val newBrick = currentBrick.down()
            val canGoDown = newBrick.volume.none { it.z == 0 } && newBrick.volume.none { it in occupiedVolume }

            val brick = if (canGoDown) newBrick else currentBrick
            newBricksToSettle += brick
            occupiedVolume += brick.volume

            if (canGoDown) moved = true
        }
        bricksToSettle = newBricksToSettle
    } while (moved)
    return bricksToSettle
}

private fun List<Brick>.isStable(): Boolean = countFallingBricks() == 0

private fun List<Brick>.countFallingBricks(): Int {
    val originalById = associateBy { it.id }
    val settled = settleBricks()
    return settled.count { originalById.getValue(it.id).volume != it.volume }
}

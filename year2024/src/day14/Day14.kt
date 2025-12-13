package day14

import check
import readInput
import util.lcm

fun main() {
    val testInput = readInput("2024", "Day14_test")
    check(part1(testInput), 12)

    val input = readInput("2024", "Day14")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.getBathroom().moveRobots(100).safetyFactor()

private fun part2(input: List<String>): Int {
    val initialBathroom = input.getBathroom()
    val robotCycles = initialBathroom.getRobotCycles()
    val lcm = robotCycles.lcm().toInt()

    val timeRange = 1..lcm
    val timeOfLargestGroup = timeRange.map { seconds ->
        val nextBathroom = initialBathroom.moveRobots(seconds)
        val currentGroupSize = nextBathroom.largestGroup()
        currentGroupSize to seconds
    }.maxBy { it.first }.second

    initialBathroom.moveRobots(timeOfLargestGroup).print()

    return timeOfLargestGroup
}

private data class Pos(val x: Int, val y: Int) {
    val adjacent
        get() = setOf(
            Pos(x, y + 1),
            Pos(x + 1, y),
            Pos(x, y - 1),
            Pos(x - 1, y),
        )

    operator fun plus(other: Pos) = Pos(x + other.x, y + other.y)
    operator fun times(factor: Int) = Pos(x * factor, y * factor)
}

private data class Robot(val pos: Pos, val velocity: Pos)
private data class Bathroom(
    val robots: List<Robot>,
    val xRange: IntRange,
    val yRange: IntRange,
)

private fun List<String>.getBathroom(): Bathroom {
    val robots = parseRobots()
    val (maxX, maxY) = robots.map { it.pos }.let { list ->
        list.maxOf { it.x } to list.maxOf { it.y }
    }
    val xRange = 0..maxX
    val yRange = 0..maxY
    return Bathroom(
        robots = robots,
        xRange = xRange,
        yRange = yRange,
    )
}

private fun List<String>.parseRobots(): List<Robot> {
    val pattern = "^p=(\\d+),(\\d+) v=(-?\\d+),(-?\\d+)$".toRegex()
    return map { line ->
        val (px, py, vx, vy) = pattern.matchEntire(line)!!.groups.drop(1).map { it!!.value.toInt() }
        Robot(pos = Pos(px, py), velocity = Pos(vx, vy))
    }
}

private fun Bathroom.moveRobots(time: Int): Bathroom {
    val sizeX = xRange.last + 1
    val sizeY = yRange.last + 1
    fun Pos.wrapInRange(): Pos {
        return Pos(
            x = ((x % sizeX) + sizeX) % sizeX,
            y = ((y % sizeY) + sizeY) % sizeY,
        )
    }
    return copy(robots = robots.map { (start, velocity) ->
        val target = start + (velocity * time)
        Robot(target.wrapInRange(), velocity)
    })
}

private fun Bathroom.safetyFactor(): Int {
    val midX = (xRange.first + xRange.last) / 2
    val midY = (yRange.first + yRange.last) / 2

    val robotPositions = robots.map { it.pos }
    val q1 = robotPositions.count { it.x in 0 until midX && it.y in 0 until midY }
    val q2 = robotPositions.count { it.x in midX + 1..xRange.last && it.y in 0 until midY }
    val q3 = robotPositions.count { it.x in 0 until midX && it.y in midY + 1..yRange.last }
    val q4 = robotPositions.count { it.x in midX + 1..xRange.last && it.y in midY + 1..yRange.last }
    return q1 * q2 * q3 * q4
}

private fun Bathroom.getRobotCycles(): List<Long> {
    val sizeX = xRange.last + 1
    val sizeY = yRange.last + 1
    fun Pos.wrapInRange(): Pos {
        return Pos(
            x = ((x % sizeX) + sizeX) % sizeX,
            y = ((y % sizeY) + sizeY) % sizeY,
        )
    }
    return robots.map { robot ->
        val start = robot.pos
        var next = start
        var cycle = 0L
        do {
            cycle++
            next = (next + robot.velocity).wrapInRange()
        } while (next != start)
        cycle
    }
}

private fun Bathroom.largestGroup(): Int {
    val visited = mutableSetOf<Pos>()
    var largestGroup = emptySet<Pos>()
    val robotPositions = robots.map { it.pos }.toSet()
    for (pos in robotPositions) {
        if (pos in visited) continue
        val group = mutableSetOf<Pos>()
        val queue = ArrayDeque<Pos>()
        queue += pos
        while (queue.isNotEmpty()) {
            val n = queue.removeFirst()
            group += n
            visited += n
            queue += n.adjacent.filter { it in robotPositions && it !in group }
        }

        if (group.size > largestGroup.size) {
            largestGroup = group
        }
    }
    return largestGroup.size
}

private fun Bathroom.print() {
    val robotsByPos = robots.groupingBy { it.pos }.eachCount()
    for (y in yRange) {
        for (x in xRange) {
            val robots = robotsByPos[Pos(x, y)]
            if (robots != null) print("#")
            else print(" ")
        }
        print("\n")
    }
}

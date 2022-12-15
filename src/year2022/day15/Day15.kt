package year2022.day15

import check
import readInput
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day15_test")
    check(part1(testInput, 10), 26)
    check(part2(testInput, 20), 56000011)

    val input = readInput("2022", "Day15")
    println(part1(input, 2000000)) // 4919281
    println(part2(input, 4000000)) // 12630143363767
}

private fun part1(input: List<String>, lineY: Int): Int {
    val sensorToBeaconPos = parseSensorToBeaconPos(input)
    val sensorsToDistance = sensorToBeaconPos.map { Pair(it.first, it.first - it.second) }
    val minX = sensorsToDistance.minOf {
        val (sensor, distance) = it
        sensor.x - distance
    }
    val maxX = sensorsToDistance.maxOf {
        val (sensor, distance) = it
        sensor.x + distance
    }
    val line = Array(maxX - minX + 1) { '.' }
    sensorToBeaconPos.forEach { pair ->
        val (sensorPos, beaconPos) = pair
        if (sensorPos.y == lineY) line[sensorPos.x - minX] = 'S'
        if (beaconPos.y == lineY) line[beaconPos.x - minX] = 'B'
        val distance = abs(sensorPos.x - beaconPos.x) + abs(sensorPos.y - beaconPos.y)

        val deltaY = abs(sensorPos.y - lineY)
        val deltaX = distance - deltaY
        if (deltaX > 0) {
            for (dx in -deltaX..deltaX) {
                val x = sensorPos.x - minX + dx
                if (x >= 0 && x <= line.lastIndex && line[x] == '.') line[x] = '#'
            }
        }
    }
    return line.count { it == '#' }
}

private fun part2(input: List<String>, limit: Int): Long {
    val sensorToBeaconPos = parseSensorToBeaconPos(input)
    val blockedBySensorOrBeacon = sensorToBeaconPos.flatMapTo(hashSetOf()) { it.toList() }

    val sensorsWithDistance = sensorToBeaconPos.map {
        val (sensorPos, beaconPos) = it
        val distance = sensorPos - beaconPos
        Pair(sensorPos, distance)
    }
    var x = 0
    var y = 0
    while (y <= limit) {
        while (x <= limit) {
            val pos = Pos(x, y)
            val blockingSensor = sensorsWithDistance.firstOrNull {
                it.first - pos <= it.second
            }
            if (blockingSensor != null) {
                val sensorPos = blockingSensor.first
                x = sensorPos.x + blockingSensor.second - abs(sensorPos.y - y) + 1
                continue
            }
            if (pos !in blockedBySensorOrBeacon) {
                return x.toLong() * 4000000L + y.toLong()
            }
            x++
        }
        x = 0
        y++
    }
    error("!")
}

private data class Pos(val x: Int, val y: Int) {
    operator fun minus(other: Pos): Int {
        return abs(x - other.x) + abs(y - other.y)
    }
}

private fun parseSensorToBeaconPos(input: List<String>): List<Pair<Pos, Pos>> {
    return input.map { line ->
        line.substringAfter("Sensor at ").split(": closest beacon is at ").map { hint ->
            val (x, y) = hint.split(", ").map { coord -> coord.substringAfter("=").toInt() }
            Pos(x, y)
        }
    }.map { pair ->
        Pair(pair[0], pair[1])
    }
}

private fun List<Pair<Pos, Pos>>.minPos(): Pos {
    var minX = Int.MAX_VALUE
    var minY = Int.MAX_VALUE
    forEach {
        minX = min(it.first.x, minX)
        minX = min(it.second.x, minX)
        minY = min(it.first.y, minY)
        minY = min(it.second.y, minY)
    }
    return Pos(minX, minY)
}

private fun List<Pair<Pos, Pos>>.maxPos(): Pos {
    var maxX = Int.MIN_VALUE
    var maxY = Int.MIN_VALUE
    forEach {
        maxX = max(it.first.x, maxX)
        maxX = max(it.second.x, maxX)
        maxY = max(it.first.y, maxY)
        maxY = max(it.second.y, maxY)
    }
    return Pos(maxX, maxY)
}
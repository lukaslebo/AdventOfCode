package day20

import check
import readInput
import kotlin.math.abs

fun main() {
    val testInput = readInput("2017", "Day20_test")
    check(part1(testInput), 0)

    val input = readInput("2017", "Day20")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val particles = input.parseParticles()
    particles.forEach { it.move(time = 10_000) }
    val zero = Pos(0, 0, 0)
    return particles.minBy { it.pos.manhattenDistanceTo(zero) }.index
}

private fun part2(input: List<String>): Int {
    var particles = input.parseParticles()
    repeat(10_000) {
        particles.forEach { it.move() }
        particles = particles.groupBy { it.pos }.filter { it.value.size == 1 }.flatMap { it.value }
    }
    return particles.size
}

private data class Particle(
    val index: Int,
    var pos: Pos,
    var velocity: Pos,
    val acceleration: Pos,
) {
    fun move(time: Int = 1) {
        repeat(time) {
            velocity += acceleration
            pos += velocity
        }
    }
}

private data class Pos(val x: Long, val y: Long, val z: Long) {
    operator fun plus(other: Pos) = Pos(
        x = x + other.x,
        y = y + other.y,
        z = z + other.z,
    )

    fun manhattenDistanceTo(pos: Pos) = abs(x - pos.x) + abs(y - pos.y) + abs(z - pos.z)
}

private fun List<String>.parseParticles(): List<Particle> {
    return mapIndexed { index, line ->
        val (pos, velocity, acceleration) = line.replace("[pav=<> ]".toRegex(), "").split(",").map { it.toLong() }
            .windowed(size = 3, step = 3).map { (x, y, z) -> Pos(x, y, z) }
        Particle(
            index = index,
            pos = pos,
            velocity = velocity,
            acceleration = acceleration,
        )
    }
}

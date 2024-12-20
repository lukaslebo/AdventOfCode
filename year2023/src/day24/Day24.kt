package day24

import check
import com.microsoft.z3.*
import readInput
import java.math.BigDecimal

fun main() {
    val testInput = readInput("2023", "Day24_test")
    check(part1(testInput, testArea = 7L..27L), 2)
    check(part2(testInput), 47)

    val input = readInput("2023", "Day24")
    println(part1(input, testArea = 200000000000000..400000000000000))
    println(part2(input))
}

private fun part1(input: List<String>, testArea: LongRange): Int {
    val particles = input.parseParticles()
    val pairs = mutableSetOf<Set<Particle>>()
    for (p1 in particles) {
        for (p2 in particles) {
            if (p1 != p2) pairs += setOf(p1, p2)
        }
    }
    return pairs.count { hasFutureCrossingInTestArea(it.first(), it.last(), testArea) }
}

private data class Pos(val x: BigDecimal, val y: BigDecimal, val z: BigDecimal)
private data class Line2D(val a: BigDecimal, val b: BigDecimal, val c: BigDecimal)
private data class Particle(val pos: Pos, val velocity: Pos, val line2D: Line2D)

private fun String.parseParticle(): Particle {
    val (pos, velocity) = split(",", "@").map { it.trim().toBigDecimal() }.chunked(3).map { (x, y, z) -> Pos(x, y, z) }

    val line2D = Line2D(velocity.y, -velocity.x, velocity.y * pos.x - velocity.x * pos.y)
    return Particle(pos = pos, velocity = velocity, line2D = line2D)
}

private fun List<String>.parseParticles() = map { it.parseParticle() }

private fun hasFutureCrossingInTestArea(p1: Particle, p2: Particle, testArea: LongRange): Boolean {
    val (a1, b1, c1) = p1.line2D
    val (a2, b2, c2) = p2.line2D
    if (a1 * b2 == b1 * a2) {
        return false
    }
    val x = (c1 * b2 - c2 * b1) / (a1 * b2 - a2 * b1)
    val y = (c2 * a1 - c1 * a2) / (a1 * b2 - a2 * b1)

    fun Particle.isAhead() = (x - pos.x) * velocity.x >= BigDecimal.ZERO && (y - pos.y) * velocity.y >= BigDecimal.ZERO
    return p1.isAhead() && p2.isAhead() && x in testArea && y in testArea
}

private operator fun LongRange.contains(num: BigDecimal) = num >= first.toBigDecimal() && num <= last.toBigDecimal()

private fun part2(input: List<String>): Long = with(Context()) {
    val particles = input.parseParticles()

    val solver = mkSolver()

    val x = mkRealConst("x")
    val y = mkRealConst("y")
    val z = mkRealConst("z")
    val vx = mkRealConst("vx")
    val vy = mkRealConst("vy")
    val vz = mkRealConst("vz")

    val zero = mkReal(0)
    fun BigDecimal.real() = mkReal(toLong())
    operator fun <R : ArithSort> ArithExpr<R>.times(other: ArithExpr<R>) = mkMul(this, other)
    operator fun <R : ArithSort> ArithExpr<R>.minus(other: ArithExpr<R>) = mkSub(this, other)
    infix fun <R : ArithSort> ArithExpr<R>.eq(other: ArithExpr<R>) = mkEq(this, other)
    operator fun Model.get(x: Expr<RealSort>): Long = (getConstInterp(x) as RatNum).let {
        it.bigIntNumerator.toLong() / it.bigIntDenominator.toLong()
    }

    for (particle in particles) {
        val (pos, v) = particle
        solver.add((((x - pos.x.real()) * (v.y.real() - vy)) - ((y - pos.y.real()) * (v.x.real() - vx))) eq zero)
        solver.add((((y - pos.y.real()) * (v.z.real() - vz)) - ((z - pos.z.real()) * (v.y.real() - vy))) eq zero)
    }
    if (solver.check() != Status.SATISFIABLE) error("no solution found")

    return solver.model[x] + solver.model[y] + solver.model[z]
}

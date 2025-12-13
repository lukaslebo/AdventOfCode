package day03

import check
import readInput

fun main() {
    val testInput = readInput("2018", "Day03_test")
    check(part1(testInput), 4)
    check(part2(testInput), 3)

    val input = readInput("2018", "Day03")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) =
    input.parseClaims().flatMap { it.fillFabrice() }.groupBy { it }.values.count { it.size > 1 }

private fun part2(input: List<String>): Int {
    val claims = input.parseClaims()
    val fabriceByClaim = claims.associateWith { it.fillFabrice() }
    val uniqueFabrice = fabriceByClaim.values.flatten().groupBy { it }.values.filter { it.size == 1 }.flatten().toSet()
    return fabriceByClaim.entries.single { (_, fabrice) -> fabrice.all { it in uniqueFabrice } }.key.id
}

private fun Claim.fillFabrice(): Set<Pos> {
    val fabrice = mutableSetOf<Pos>()
    for (x in 0 until size.x) {
        for (y in 0 until size.y) {
            fabrice += Pos(x, y) + offset
        }
    }
    return fabrice
}

private data class Pos(val x: Int, val y: Int) {
    operator fun plus(other: Pos) = Pos(x + other.x, y + other.y)
}

private data class Claim(
    val id: Int,
    val offset: Pos,
    val size: Pos,
)

private fun List<String>.parseClaims() = map { line ->
    val (id, offsetX, offsetY, x, y) = line
        .replace("#", "")
        .replace("@ ", "")
        .replace(",", " ")
        .replace(":", "")
        .replace("x", " ")
        .split(" ")
        .map { it.toInt() }
    Claim(
        id = id,
        offset = Pos(x = offsetX, y = offsetY),
        size = Pos(x = x, y = y),
    )
}

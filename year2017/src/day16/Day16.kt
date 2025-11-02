package day16

import readInput

fun main() {
    val input = readInput("2017", "Day16")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): String {
    val moves = input.first().parseMoves()
    return moves.fold("abcdefghijklmnop") { programs, move -> move.perform(programs) }
}

private fun part2(input: List<String>): String {
    val moves = input.first().parseMoves()
    var programs = "abcdefghijklmnop"
    val cache = mutableMapOf<String, String>()
    repeat(1_000_000_000) {
        programs = cache.getOrPut(programs) {
            moves.fold(programs) { programs, move -> move.perform(programs) }
        }
    }
    return programs
}

private sealed interface Move {
    fun perform(programs: String): String
}

private class Spin(val n: Int) : Move {
    override fun perform(programs: String) = programs.takeLast(n) + programs.take(programs.length - n)
}

private class Exchange(val indexA: Int, val indexB: Int) : Move {
    override fun perform(programs: String) = programs.swap(indexA, indexB)
}

private class Partner(val a: Char, val b: Char) : Move {
    override fun perform(programs: String) = programs.swap(programs.indexOf(a), programs.indexOf(b))
}

private fun String.swap(indexA: Int, indexB: Int): String {
    val arr = toCharArray()
    val tmp = arr[indexA]
    arr[indexA] = arr[indexB]
    arr[indexB] = tmp
    return arr.concatToString()
}

private fun String.parseMoves(): List<Move> {
    return split(",").map {
        val data = it.drop(1)
        val dataParts = data.split("/")
        when (it.first()) {
            's' -> Spin(data.toInt())
            'x' -> Exchange(dataParts.first().toInt(), dataParts.last().toInt())
            'p' -> Partner(dataParts.first().first(), dataParts.last().first())
            else -> error("unsupported move")
        }
    }
}

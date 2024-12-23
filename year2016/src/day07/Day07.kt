package day07

import readInput

fun main() {
    // Part 1 - TLS
    check("abba[mnop]qrst".supportsTLS())
    check(!"abcd[bddb]xyyx".supportsTLS())
    check(!"aaaa[qwer]tyui".supportsTLS())
    check("ioxxoj[asdfgh]zxcvbn".supportsTLS())
    // Part 2 - SSL
    check("aba[bab]xyz".supportsSSL())
    check(!"xyx[xyx]xyx".supportsSSL())
    check("aaa[kek]eke".supportsSSL())
    check("zazbz[bzb]cdb".supportsSSL())

    val input = readInput("2016", "Day07")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.count { it.supportsTLS() }

private fun part2(input: List<String>) = input.count { it.supportsSSL() }

private fun String.supportsTLS(): Boolean {
    fun String.hasAbba() = indices.any { index ->
        index + 3 <= lastIndex &&
                get(index) == get(index + 3) &&
                get(index + 1) == get(index + 2) &&
                get(index) != get(index + 1)
    }

    val (supernetSequences, hypernetSequences) = getSupernetAndHypernetSequences()
    return hypernetSequences.none { it.hasAbba() } && supernetSequences.any { it.hasAbba() }
}

private fun String.supportsSSL(): Boolean {
    fun String.findAllABA(): List<String> = windowed(3).filter { it[0] != it[1] && it[0] == it[2] }
    fun String.convertToBAB() = "${get(1)}${get(0)}${get(1)}"

    val (supernetSequences, hypernetSequences) = getSupernetAndHypernetSequences()
    val abas = supernetSequences.flatMap { it.findAllABA() }
    val babs = abas.map { it.convertToBAB() }
    return hypernetSequences.any { hypernetSequence ->
        babs.any { bab -> bab in hypernetSequence }
    }
}

private fun String.getSupernetAndHypernetSequences(): Pair<List<String>, List<String>> =
    split("[\\[\\]]".toRegex())
        .withIndex()
        .groupBy({ it.index % 2 }, { it.value })
        .let { it[0]!! to it[1]!! }

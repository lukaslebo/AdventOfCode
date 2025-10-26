package day04

import readInput

fun main() {
    val input = readInput("2017", "Day04")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.count {
    val words = it.split(" ")
    val uniqueWords = words.toSet()
    words.size == uniqueWords.size
}

private fun part2(input: List<String>) = input.count {
    val words = it.split(" ")
    val uniqueWordsWithoutAnagrams = words.map { word -> word.toSet() }.toSet()
    words.size == uniqueWordsWithoutAnagrams.size
}

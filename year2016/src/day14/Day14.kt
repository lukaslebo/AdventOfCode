package day14

import check
import md5
import readInput

fun main() {
    val testInput = readInput("2016", "Day14_test")
    check(part1(testInput), 22728)
    check(part2(testInput), 22551)

    val input = readInput("2016", "Day14")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val oneTimePad = OneTimePad(input.first())
    while (oneTimePad.keyIndex.size < 64) {
        oneTimePad.generateNextKey()
    }
    return oneTimePad.keyIndex.last().index
}

private fun part2(input: List<String>): Int {
    val oneTimePad = OneTimePad(input.first(), enableKeyStretching = true)
    while (oneTimePad.keyIndex.size < 64) {
        oneTimePad.generateNextKey()
    }
    return oneTimePad.keyIndex.last().index
}

private fun String.triples(): Set<String> {
    val triples = mutableSetOf<String>()
    for (triple in windowed(3)) {
        if (triple[0] == triple[1] && triple[0] == triple[2]) {
            triples += triple
        }
    }
    return triples
}

private data class OneTimePad(val salt: String, val enableKeyStretching: Boolean = false) {
    data class KeyIndex(val index: Int, val key: String)

    private var index = 0
    private val queue = ArrayDeque<String>()
    val keyIndex = mutableListOf<KeyIndex>()

    init {
        queue += (0..1000).map { (generateHash(it)) }
    }

    private fun generateHash(index: Int): String {
        val hash = (salt + index).md5()
        if (!enableKeyStretching) return hash
        var stretchedHash = hash
        repeat(2016) {
            stretchedHash = stretchedHash.md5()
        }
        return stretchedHash
    }

    private fun shift() {
        index++
        queue.removeFirst()
        queue += generateHash(index + 1000)
    }

    private fun isKey(): Boolean {
        val triples = queue.first().triples()
        val nextThousand = queue.drop(1)
        for (triple in triples.take(1)) {
            val quintuple = triple.substring(0..0).repeat(5)
            val isKey = nextThousand.any { quintuple in it }
            if (isKey) return true
        }
        return false
    }

    fun generateNextKey() {
        while (!isKey()) {
            shift()
        }
        val key = queue.first()
        keyIndex += KeyIndex(index, key)
        shift()
    }
}

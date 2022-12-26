package year2015.day19

import readInput

fun main() {
    val input = readInput("2015", "Day19")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val replacements = input.dropLast(2).map { it.split(" => ") }.map { it[0] to it[1] }
    val molecule = input.last()
    val transformations = hashSetOf<String>()
    for ((replace, with) in replacements) {
        var index = 0
        do {
            val replaceAt = molecule.indexOf(replace, index)
            if (replaceAt != -1) {
                transformations += molecule.replaceAt(replaceAt, replace, with)
            }
            index = replaceAt + 1
        } while (index > 0)
    }
    return transformations.size
}

private fun part2(input: List<String>): Int {
    val replacements = input.dropLast(2)
        .map { it.split(" => ") }
        .map { it[0] to it[1] }
        .sortedByDescending { it.second.length }
    val medicine = input.last()

    val queue = ArrayDeque<Pair<String, Int>>()
    queue += medicine to 0
    while (queue.isNotEmpty()) {
        val (molecule, steps) = queue.removeFirst()
        for ((with, replace) in replacements) {
            val nextMolecule = molecule.replaceFirst(replace, with)
            if (nextMolecule == "e") return steps + 1
            if (nextMolecule != molecule) {
                queue += nextMolecule to steps + 1
                break
            }
        }
    }
    error("Medicine can't be obtained with alchemy")
}

private fun String.replaceAt(index: Int, replace: String, with: String) = substring(0, index) +
        with + substring(index + replace.length)
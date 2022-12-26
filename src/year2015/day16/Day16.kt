package year2015.day16

import readInput

fun main() {
    val input = readInput("2015", "Day16")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val mfcsam = mapOf(
        "children" to 3,
        "cats" to 7,
        "samoyeds" to 2,
        "pomeranians" to 3,
        "akitas" to 0,
        "vizslas" to 0,
        "goldfish" to 5,
        "trees" to 3,
        "cars" to 2,
        "perfumes" to 1,
    )
    val possibleSues = input.associate { thingsBySue(it) }.filter {
        it.value.all { (k, v) ->
            mfcsam[k] == v
        }
    }
    return possibleSues.keys.first()
}

private fun part2(input: List<String>): Int {
    val mfcsam = mapOf(
        "children" to 3..3,
        "cats" to (7 + 1)..Int.MAX_VALUE,
        "samoyeds" to 2..2,
        "pomeranians" to (Int.MIN_VALUE until 3),
        "akitas" to 0..0,
        "vizslas" to 0..0,
        "goldfish" to (Int.MIN_VALUE until 5),
        "trees" to (3 + 1)..Int.MAX_VALUE,
        "cars" to 2..2,
        "perfumes" to 1..1,
    )
    val possibleSues = input.associate { thingsBySue(it) }.filter {
        it.value.all { (k, v) ->
            v in mfcsam[k]!!
        }
    }
    return possibleSues.keys.first()
}


private fun thingsBySue(input: String): Pair<Int, Map<String, Int>> {
    val num = "Sue (\\d+):".toRegex().find(input)!!.groupValues[1].toInt()
    val things =
        "(\\w+): (\\d+)".toRegex().findAll(input).map { it.groupValues[1] to it.groupValues[2].toInt() }.toMap()
    return num to things
}
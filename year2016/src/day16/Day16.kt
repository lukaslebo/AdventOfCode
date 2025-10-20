package day16

import readInput

fun main() {
    val input = readInput(year = "2016", name = "Day16")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) =
    createCheckSum(initialData = input.first(), diskSpace = 272)

private fun part2(input: List<String>) =
    createCheckSum(initialData = input.first(), diskSpace = 35651584)

private fun createCheckSum(initialData: String, diskSpace: Int): String {
    var data = initialData
    while (data.length < diskSpace) {
        data = data.extrapolate()
    }
    return checksum(data.take(diskSpace))
}

private fun String.extrapolate(): String {
    val a = this
    val b = a.reversed()
        .replace(oldValue = "0", newValue = "x")
        .replace(oldValue = "1", newValue = "0")
        .replace(oldValue = "x", newValue = "1")
    return "${a}0$b"
}

private tailrec fun checksum(input: String): String {
    if (input.length % 2 != 0) return input
    val checksum = input.windowed(size = 2, step = 2).joinToString(separator = "") {
        if (it[0] == it[1]) "1" else "0"
    }
    return checksum(checksum)
}

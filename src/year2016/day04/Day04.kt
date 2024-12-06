package year2016.day04

import check
import readInput

fun main() {
    val testInput = readInput("2016", "Day04_test")
    check(part1(testInput), 1514)

    val input = readInput("2016", "Day04")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.map { it.parseRoom() }
    .filter { it.isReal() }
    .sumOf { it.sectorId }

private fun part2(input: List<String>) = input.map { it.parseRoom() }
    .single {
        val decryptedName = it.decryptName()
        "north" in decryptedName && "pole" in decryptedName && "object" in decryptedName
    }
    .sectorId

private data class Room(
    val name: String,
    val sectorId: Int,
    val checksum: String,
) {
    fun isReal() = checksum == checksum(this)
}

private fun String.parseRoom(): Room {
    val (sectorId, checksum) = substringAfterLast("-").split("\\[|\\]".toRegex())
    return Room(
        substringBeforeLast("-"),
        sectorId.toInt(),
        checksum,
    )
}

private fun checksum(room: Room) = room.name.replace("-", "")
    .groupingBy { it }
    .eachCount()
    .entries
    .sortedWith(compareByDescending<Map.Entry<Char, Int>> { it.value }.thenBy { it.key })
    .take(5)
    .map { it.key }
    .joinToString("")

private fun Room.decryptName(): String {
    fun Char.shift() = (((code - 'a'.code + sectorId) % 26) + 'a'.code).toChar()
    return name.map { it.shift() }.joinToString("")
}

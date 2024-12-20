package day20

import check
import readInput

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day20_test")
    check(part1(testInput), 3)
    check(part2(testInput), 1623178306)

    val input = readInput("2022", "Day20")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = decrypt(input)

private fun part2(input: List<String>) = decrypt(
    input = input,
    decryptionKey = 811589153,
    mixingIterations = 10,
)

private fun decrypt(input: List<String>, decryptionKey: Long = 1, mixingIterations: Int = 1): Long {
    val originalList = input.mapIndexed { i, n -> Pair(n.toLong() * decryptionKey, i) }
    val mixedList = originalList.toMutableList()

    repeat(mixingIterations) {
        originalList.forEach {
            mixNumber(mixedList, it)
        }
    }

    val mixedListNumbers = mixedList.map { it.first }
    val indexOfZero = mixedListNumbers.indexOf(0)
    val posX = mixedListNumbers[(indexOfZero + 1000) % mixedList.size]
    val posY = mixedListNumbers[(indexOfZero + 2000) % mixedList.size]
    val posZ = mixedListNumbers[(indexOfZero + 3000) % mixedList.size]
    return posX + posY + posZ

}

private fun mixNumber(list: MutableList<Pair<Long, Int>>, numWithIndex: Pair<Long, Int>) {
    val index = list.indexOf(numWithIndex)
    val num = list[index].first

    val sizeWithoutNum = list.size - 1
    val moves = num % sizeWithoutNum
    val newIndex = (index + moves + sizeWithoutNum) % sizeWithoutNum

    list.remove(numWithIndex)
    list.add(newIndex.toInt(), numWithIndex)
}

package day09

import check
import readInput

fun main() {
    val testInput = readInput("2024", "Day09_test")
    check(part1(testInput), 1928)
    check(part2(testInput), 2858)

    val input = readInput("2024", "Day09")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.first().parseFileSystem().moveBlocksLeft().checksum()

private fun part2(input: List<String>) = input.first().parseFileSystem().moveFilesLeft().checksum()

private data class Block(val fileID: Int)
private typealias FileSystem = List<Block?>

private fun String.parseFileSystem(): FileSystem {
    val blocks = mutableListOf<Block?>()
    for ((fileID, pair) in chunked(2).withIndex()) {
        val numbers = pair.toCharArray().map { it.digitToInt() }
        val filesize = numbers.first()
        val emptysize = numbers.getOrNull(1) ?: 0
        repeat(filesize) {
            blocks += Block(fileID)
        }
        repeat(emptysize) {
            blocks += null
        }
    }
    return blocks
}

private fun FileSystem.moveBlocksLeft(): FileSystem {
    val sorted = toMutableList()
    var nextEmptyIndex = sorted.indexOf(null)
    var lastFileIndex = sorted.indexOfLastFile()

    while (nextEmptyIndex < lastFileIndex) {
        sorted[nextEmptyIndex] = sorted[lastFileIndex]
        sorted[lastFileIndex] = null
        nextEmptyIndex = sorted.indexOf(null)
        lastFileIndex = sorted.indexOfLastFile(end = lastFileIndex)
    }
    return sorted
}

private fun FileSystem.moveFilesLeft(): FileSystem {
    val sorted = toMutableList()
    val maxFileId = maxOf { it?.fileID ?: 0 }
    var firstEmptyBlock = indexOf(null)
    for (fileId in maxFileId downTo 1) {
        val fileStart = sorted.indexOfFirst { it?.fileID == fileId }
        val fileEnd = sorted.indexOfLast { it?.fileID == fileId }
        val size = fileEnd - fileStart + 1
        val nextEmptyBlocksStart = sorted.findEmptyBlocksWithSizeBetween(size, firstEmptyBlock, fileStart)
        if (nextEmptyBlocksStart != null) {
            for (i in 0 until size) {
                sorted[nextEmptyBlocksStart + i] = Block(fileId)
            }
            for (i in fileStart..fileEnd) {
                sorted[i] = null
            }
            if (nextEmptyBlocksStart == firstEmptyBlock) {
                firstEmptyBlock = sorted.indexOf(null)
            }
        }
    }
    return sorted
}

private fun FileSystem.findEmptyBlocksWithSizeBetween(size: Int, start: Int, end: Int): Int? {
    var emptysize = 0
    for (i in start..end) {
        if (get(i) == null) {
            emptysize++
            if (emptysize >= size) return i - emptysize + 1
        } else {
            emptysize = 0
        }
    }
    return null
}

private fun FileSystem.indexOfLastFile(end: Int = lastIndex): Int {
    var i = end
    while (i >= 0) {
        if (get(i) != null) return i
        i--
    }
    error("No file found")
}

private fun FileSystem.checksum() = mapIndexed { i, block -> i * (block?.fileID ?: 0).toLong() }.sum()

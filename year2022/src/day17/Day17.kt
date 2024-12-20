package day17

import check
import readInput
import kotlin.math.max
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.milliseconds

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day17_test")
    check(part1(testInput), 3068)
    check(part2(testInput), 1_514_285_714_288)

    val input = readInput("2022", "Day17")
    measureTimeMillis { print(part1(input)) }.also { println(" (Part 1 took ${it.milliseconds})") }
    measureTimeMillis { print(part2(input)) }.also { println(" (Part 2 took ${it.milliseconds})") }
}

private fun part1(input: List<String>): Long = getTetrisTowerHeight(input, 2022)

private fun part2(input: List<String>): Long = getTetrisTowerHeight(input, 1000000000000)

private fun getTetrisTowerHeight(input: List<String>, rocks: Long): Long {
    lastHighestRock = 0
    RockType.reset()

    val moves = Moves(input.first())
    val hall = Array(7) { Array(5_000) { true } }

    data class PatternKey(
        val moveIndex: Int,
        val rockTypeIndex: Int,
        val topRow: List<Int>,
    )

    data class PatternValue(
        val highestRock: Int,
        val remainingRocks: Long,
    )

    val patterns = hashMapOf<PatternKey, PatternValue>()

    var remainingRocks = rocks
    var extraHeight = 0L
    while (remainingRocks > 0) {
        val rock = Rock(RockType.next(), hall.highestRock())
        while (true) {
            rock.move(moves.next(), hall)
            val settled = rock.moveDown(hall)
            if (settled) break
        }
        remainingRocks--

        val patternKey = PatternKey(moves.i, RockType.i, hall.topRows())
        val patternValue = PatternValue(hall.highestRock(), remainingRocks)
        val pattern = patterns[patternKey]
        if (pattern != null && extraHeight == 0L) {
            val patternHeight = patternValue.highestRock - pattern.highestRock
            val rocksInPattern = pattern.remainingRocks - patternValue.remainingRocks
            val patternRepetitions = remainingRocks / rocksInPattern
            remainingRocks %= rocksInPattern
            extraHeight += patternRepetitions * patternHeight
        } else {
            patterns[patternKey] = patternValue
        }
    }
    return hall.highestRock() + 1 + extraHeight
}

private enum class Dir {
    L, R
}

private class Moves(private val input: String) {
    var i = 0
        private set
        get() = field % input.length

    fun next(): Dir {
        return if (input[i++] == '>') Dir.R else Dir.L
    }
}

private enum class RockType {
    HLine, Plus, InvL, VLine, Block;

    companion object {
        private val types = arrayOf(HLine, Plus, InvL, VLine, Block)
        var i = 0
            private set
            get() = field % types.size

        fun reset() {
            i = 0
        }

        fun next(): RockType {
            return types[i++]
        }
    }
}

// rocks appear so that its left edge is two units away from the left wall
// its bottom edge is three units above the highest rock
private class Rock(val type: RockType, highestRock: Int) {
    var x = 2
    var y = highestRock + 4

    fun move(dir: Dir, hall: Array<Array<Boolean>>) {
        when (dir) {
            Dir.L -> moveLeft(hall)
            Dir.R -> moveRight(hall)
        }
    }

    private fun moveLeft(hall: Array<Array<Boolean>>) {
        val canMove = when (type) {
            RockType.HLine -> x > 0 && hall[x - 1][y]
            RockType.Plus -> x > 0 && hall[x][y] && hall[x - 1][y + 1] && hall[x][y + 2]
            RockType.InvL -> x > 0 && hall[x - 1][y] && hall[x + 1][y + 1] && hall[x + 1][y + 2]
            RockType.VLine -> x > 0 && hall[x - 1][y] && hall[x - 1][y + 1] && hall[x - 1][y + 2] && hall[x - 1][y + 3]
            RockType.Block -> x > 0 && hall[x - 1][y] && hall[x - 1][y + 1]
        }
        if (canMove) x--
    }

    private fun moveRight(hall: Array<Array<Boolean>>) {
        val canMove = when (type) {
            RockType.HLine -> x + 4 <= hall.lastIndex && hall[x + 4][y]
            RockType.Plus -> x + 3 <= hall.lastIndex && hall[x + 2][y] && hall[x + 3][y + 1] && hall[x + 2][y + 2]
            RockType.InvL -> x + 3 <= hall.lastIndex && hall[x + 3][y] && hall[x + 3][y + 1] && hall[x + 3][y + 2]
            RockType.VLine -> x + 1 <= hall.lastIndex && hall[x + 1][y] && hall[x + 1][y + 1] && hall[x + 1][y + 2] && hall[x + 1][y + 3]
            RockType.Block -> x + 2 <= hall.lastIndex && hall[x + 2][y] && hall[x + 2][y + 1]
        }
        if (canMove) x++
    }

    fun moveDown(hall: Array<Array<Boolean>>): Boolean {
        val canMove = when (type) {
            RockType.HLine -> y > 0 && hall[x][y - 1] && hall[x + 1][y - 1] && hall[x + 2][y - 1] && hall[x + 3][y - 1]
            RockType.Plus -> y > 0 && hall[x][y] && hall[x + 1][y - 1] && hall[x + 2][y]
            RockType.InvL -> y > 0 && hall[x][y - 1] && hall[x + 1][y - 1] && hall[x + 2][y - 1]
            RockType.VLine -> y > 0 && hall[x][y - 1]
            RockType.Block -> y > 0 && hall[x][y - 1] && hall[x + 1][y - 1]
        }
        if (canMove) {
            y--
            return false
        }
        settle(hall)
        return true
    }

    private fun settle(hall: Array<Array<Boolean>>) {
        when (type) {
            RockType.HLine -> {
                hall[x][y] = false
                hall[x + 1][y] = false
                hall[x + 2][y] = false
                hall[x + 3][y] = false
            }

            RockType.Plus -> {
                hall[x + 1][y] = false
                hall[x][y + 1] = false
                hall[x + 1][y + 1] = false
                hall[x + 2][y + 1] = false
                hall[x + 1][y + 2] = false
            }

            RockType.InvL -> {
                hall[x][y] = false
                hall[x + 1][y] = false
                hall[x + 2][y] = false
                hall[x + 2][y + 1] = false
                hall[x + 2][y + 2] = false
            }

            RockType.VLine -> {
                hall[x][y] = false
                hall[x][y + 1] = false
                hall[x][y + 2] = false
                hall[x][y + 3] = false
            }

            RockType.Block -> {
                hall[x][y] = false
                hall[x + 1][y] = false
                hall[x][y + 1] = false
                hall[x + 1][y + 1] = false
            }
        }
    }
}

private var lastHighestRock = -1
private fun Array<Array<Boolean>>.highestRock(): Int {
    val from = max(lastHighestRock, 0)
    for (y in from..first().lastIndex) {
        if (all { it[y] }) {
            lastHighestRock = y - 1
            return y - 1
        }
    }
    return -1
}

private fun Array<Array<Boolean>>.highestBlockingRow(): Int {
    val maxY = max(highestRock(), 0)
    val previous = Array(size) { true }
    for (y in maxY downTo 0) {
        if (mapIndexed { x, col -> !col[y] || !previous[x] }.all { it }) {
            return y
        }
        onEachIndexed { x, col -> previous[x] = col[y] }
    }
    return -1
}

private fun Array<Array<Boolean>>.topRows(): List<Int> {
    val maxY = max(highestRock(), 0)
    val minY = highestBlockingRow() + 1
    var num = 0
    var bit = 0
    val nums = arrayListOf<Int>()
    var addNum = true
    for (x in 0..lastIndex) {
        for (y in maxY downTo minY) {
            addNum = true
            if (this[x][y]) {
                num = num or (1 shl bit)
            }
            bit++
            if (bit == 32) {
                addNum = false
                nums += num
                bit = 0
                num = 0
            }
        }
    }
    if (addNum) nums += num
    return nums
}

private fun Array<Array<Boolean>>.print(rock: Rock? = null) {
    val maxY = max(rock?.y ?: 0, highestRock())
    for (y in maxY downTo 0) {
        print("|")
        for (x in 0..lastIndex) {
            if (rock?.x == x && rock.y == y) print('x')
            else if (this[x][y]) print('.')
            else print('#')

        }
        print("|")
        println()
    }
    println("+-------+")
    println()
}

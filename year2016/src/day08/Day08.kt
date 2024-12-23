package day08

import readInput

fun main() {
    val input = readInput("2016", "Day08")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = swipeCard(input).countActivePixels()
private fun part2(input: List<String>) = swipeCard(input).output()

private fun swipeCard(input: List<String>): Display {
    val instructions = input.map { it.toInstruction() }
    val display = createDisplay()
    for (instruction in instructions) {
        display.perform(instruction)
    }
    return display
}

private typealias Display = Array<Array<Boolean>>

private fun createDisplay(rows: Int = 6, cols: Int = 50): Display = Array(rows) { Array(cols) { false } }

private fun Display.countActivePixels() = sumOf { row -> row.count { it } }

private fun Display.perform(instruction: Instruction) = instruction.performOn(this)

private sealed interface Instruction {
    fun performOn(display: Display)
}

private data class Rect(val rows: Int, val cols: Int) : Instruction {
    override fun performOn(display: Display) {
        for (y in 0 until rows.coerceAtMost(display.size)) {
            for (x in 0 until cols.coerceAtMost(display.first().size)) {
                display[y][x] = true
            }
        }
    }
}

private data class RotateRow(val row: Int, val shift: Int) : Instruction {
    override fun performOn(display: Display) {
        if (row !in display.indices) return
        val array = display[row]
        display[row] = array.rotate(shift)
    }
}

private data class RotateCol(val col: Int, val shift: Int) : Instruction {
    override fun performOn(display: Display) {
        if (col !in display.first().indices) return
        val array = display.map { it[col] }.toTypedArray()
        val rotatedArray = array.rotate(shift)
        for (y in rotatedArray.indices) {
            display[y][col] = rotatedArray[y]
        }
    }
}

private fun Array<Boolean>.rotate(shift: Int): Array<Boolean> {
    val rotated = Array(size) { false }
    for (i in indices) {
        rotated[(i + shift) % size] = this[i]
    }
    return rotated
}

private fun String.toInstruction(): Instruction {
    val rectPattern = "rect (?<cols>\\d+)x(?<rows>\\d+)".toRegex()
    val rotateRowPattern = "rotate row y=(?<row>\\d+) by (?<shift>\\d+)".toRegex()
    val rotateColPattern = "rotate column x=(?<col>\\d+) by (?<shift>\\d+)".toRegex()

    var match = rectPattern.matchEntire(this)
    if (match != null) {
        return Rect(
            rows = match.groups["rows"]!!.value.toInt(),
            cols = match.groups["cols"]!!.value.toInt(),
        )
    }
    match = rotateRowPattern.matchEntire(this)
    if (match != null) {
        return RotateRow(
            row = match.groups["row"]!!.value.toInt(),
            shift = match.groups["shift"]!!.value.toInt(),
        )
    }
    match = rotateColPattern.matchEntire(this)
    if (match != null) {
        return RotateCol(
            col = match.groups["col"]!!.value.toInt(),
            shift = match.groups["shift"]!!.value.toInt(),
        )
    }
    error("unknown instruction: $this")
}

private fun Display.output() = buildString {
    val display = this@output
    for (y in display.indices) {
        for (x in display.first().indices) {
            val pixel = if (display[y][x]) '#' else ' '
            append(pixel)
        }
        append('\n')
    }
}

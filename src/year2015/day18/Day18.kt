package year2015.day18

import check
import readInput

fun main() {
    val testInput = readInput("2015", "Day18_test")
    check(part1(testInput, 4), 4)
    check(part2(testInput, 5), 17)

    val input = readInput("2015", "Day18")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>, rounds: Int = 100) = activeLightsAfterAnimation(input, rounds)

private fun part2(input: List<String>, rounds: Int = 100) =
    activeLightsAfterAnimation(input, rounds) { (y, x), grid -> (y to x) isCornerOf grid }

private fun activeLightsAfterAnimation(
    input: List<String>,
    rounds: Int = 100,
    orCondition: (Pair<Int, Int>, Array<Array<Boolean>>) -> Boolean = { _, _ -> false },
): Int {
    var grid = input.toGrid(orCondition)
    var next = Array(grid.size) { Array(grid.first().size) { false } }
    repeat(rounds) {
        for (y in grid.indices) {
            for (x in grid.first().indices) {
                val activeNeighbours = (y to x).neighbours().count { (ny, nx) ->
                    grid.getOrNull(ny)?.getOrNull(nx) == true
                }
                val isActive = grid[y][x]
                next[y][x] = (isActive && activeNeighbours in 2..3) ||
                        (!isActive && activeNeighbours == 3) ||
                        orCondition(y to x, grid)
            }
        }
        val temp = grid
        grid = next
        next = temp
    }
    return grid.sumOf { row -> row.count { it } }
}

private fun List<String>.toGrid(
    orCondition: (Pair<Int, Int>, Array<Array<Boolean>>) -> Boolean
): Array<Array<Boolean>> {
    val grid = Array(size) { Array(first().length) { false } }
    forEachIndexed { y, row ->
        row.forEachIndexed { x, state ->
            grid[y][x] = state == '#' || orCondition(y to x, grid)
        }
    }
    return grid
}

private fun Pair<Int, Int>.neighbours(): List<Pair<Int, Int>> {
    val (y, x) = this
    return listOf(
        y - 1 to x - 1,
        y - 1 to x,
        y - 1 to x + 1,
        y to x + 1,
        y + 1 to x + 1,
        y + 1 to x,
        y + 1 to x - 1,
        y to x - 1,
    )
}

private infix fun Pair<Int, Int>.isCornerOf(grid: Array<Array<Boolean>>): Boolean {
    val (y, x) = this
    return (y == 0 || y == grid.lastIndex) && (x == 0 || x == grid.first().lastIndex)
}
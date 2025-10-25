package day22

import readInput

private val playGame = false

fun main() {
    val input = readInput("2016", "Day22")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val nodes = input.drop(2).map { it.parseNode() }
    val pairs = mutableSetOf<Pair<Node, Node>>()
    for (nodeA in nodes) {
        for (nodeB in nodes) {
            if (nodeA != nodeB && nodeA.used > 0 && nodeA.used <= nodeB.free) {
                pairs += nodeA to nodeB
            }
        }
    }
    return pairs.size
}

private fun part2(input: List<String>): Int {
    val nodesByPos = input.drop(2).map { it.parseNode() }.associateBy { it.pos }.toMutableMap()
    val maxX = nodesByPos.values.maxOf { it.pos.x }
    val maxY = nodesByPos.values.maxOf { it.pos.y }
    val readPos = Pos(0, 0)
    val goal = Pos(maxX, 0)
    nodesByPos[goal] = nodesByPos.getValue(goal).copy(goal = true)

    if (!playGame) {
        val emptyPos = nodesByPos.values.single { it.used == 0 }.pos
        val wallMinX = nodesByPos.values.filter { it.used > 100 }.minBy { it.pos.x }.pos.x
        // Only works for layout with wall to the right
        val steps = (emptyPos.x - wallMinX) + 1 + emptyPos.y + 1 + (maxX - wallMinX) + ((maxX - 1) * 5)
        return steps
    }

    fun printMap() {
        for (y in 0..maxY) {
            for (x in 0..maxX) {
                val pos = Pos(x, y)
                val node = nodesByPos[pos]!!

                val symbol = when {
                    node.used == 0 -> "0"
                    node.goal -> "G"
                    pos == readPos -> "!"
                    node.used > 80 -> "#"
                    else -> "."
                }
                print(symbol)
            }
            println()
        }
    }

    var steps = 0
    while (nodesByPos.values.single { it.goal }.pos != readPos) {
        printMap()
        println("Steps: $steps")
        val arrow = readArrow() ?: continue
        val emptyNode = nodesByPos.values.single { it.used == 0 }
        val swapNode = nodesByPos.getValue(emptyNode.pos + arrow)
        if (swapNode.used > emptyNode.size) continue
        val newEmptyNode = emptyNode.copy(pos = swapNode.pos)
        val newSwapNode = swapNode.copy(pos = emptyNode.pos)
        nodesByPos += newEmptyNode.pos to newEmptyNode
        nodesByPos += newSwapNode.pos to newSwapNode
        steps++
    }
    return steps
}

private data class Pos(val x: Int, val y: Int) {
    companion object {
        val up = Pos(0, -1)
        val down = Pos(0, 1)
        val left = Pos(-1, 0)
        val right = Pos(1, 0)
    }

    operator fun plus(other: Pos) = Pos(x + other.x, y + other.y)
}

private data class Node(val pos: Pos, val size: Int, val used: Int, val free: Int, val goal: Boolean = false)

private val pattern =
    "/dev/grid/node-x(?<x>\\d+)-y(?<y>\\d+) +(?<size>\\d+)T +(?<used>\\d+)T +(?<free>\\d+)T +(\\d+)%".toRegex()

private fun String.parseNode(): Node {
    val groups = pattern.matchEntire(this)!!.groups
    return Node(
        pos = Pos(
            x = groups["x"]!!.value.toInt(),
            y = groups["y"]!!.value.toInt(),
        ),
        size = groups["size"]!!.value.toInt(),
        used = groups["used"]!!.value.toInt(),
        free = groups["free"]!!.value.toInt(),
    )
}

private fun readArrow(): Pos? {
    val line = readln()
    return when (line) {
        "8" -> Pos.up
        "2" -> Pos.down
        "6" -> Pos.right
        "4" -> Pos.left
        else -> null
    }
}

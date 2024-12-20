package day22

import check
import readInput

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day22_test")
    check(part1(testInput), 6032)
    check(part2(testInput), 5031)

    val input = readInput("2022", "Day22")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val board = parseBoard(input.subList(0, input.size - 2))
    val instructions = parseInstructions(input.last())
    val (pos, dir) = followPathOnBoard(board, instructions)
    return (pos.y + 1) * 1000 + (pos.x + 1) * 4 + dir.ordinal
}

private fun part2(input: List<String>): Int {
    val board = parseBoard(input.subList(0, input.size - 2))
    val instructions = parseInstructions(input.last())
    val (pos, dir) = followPathOnCube(board, instructions)
    return (pos.y + 1) * 1000 + (pos.x + 1) * 4 + dir.ordinal
}

private fun followPathOnBoard(board: Array<Array<Char>>, instructions: List<Instruction>): Pair<Pos, Direction> {
    var pos = Pos(board.first().indexOfFirst { it == '.' }, 0)
    var dir = Direction.Right
    for (instruction in instructions) {
        dir = dir.turn(instruction)
        if (instruction is Instruction.Move) {
            var steps = instruction.steps
            while (steps > 0) {
                val nextPos = board.stepOnBoard(pos, dir)
                if (nextPos == pos) break
                pos = nextPos
                steps--
            }
        }
    }
    return pos to dir
}

private fun followPathOnCube(board: Array<Array<Char>>, instructions: List<Instruction>): Pair<Pos, Direction> {
    var pos = Pos(board.first().indexOfFirst { it == '.' }, 0)
    var dir = Direction.Right
    for (instruction in instructions) {
        dir = dir.turn(instruction)
        if (instruction is Instruction.Move) {
            var steps = instruction.steps
            while (steps > 0) {
                val (nextPos, nextDir) = board.stepOnCube(pos, dir)
                if (nextPos == pos) break
                dir = nextDir
                pos = nextPos
                steps--
            }
        }
    }
    return pos to dir
}

private fun Array<Array<Char>>.stepOnCube(pos: Pos, dir: Direction): Pair<Pos, Direction> {
    if (size < 50) return stepOnCubeTestPattern(pos, dir)

    var nextDir = dir
    var nextPos = when (dir) {
        Direction.Right -> Pos(pos.x + 1, pos.y)
        Direction.Down -> Pos(pos.x, pos.y + 1)
        Direction.Left -> Pos(pos.x - 1, pos.y)
        Direction.Up -> Pos(pos.x, pos.y - 1)
    }
    if (nextPos.y < 0 || (dir == Direction.Up && this[nextPos.y][nextPos.x] == ' ')) {
        if (nextPos.x in 0..49) {
            nextPos = Pos(50, 50 + nextPos.x)
            nextDir = Direction.Right
        } else if (nextPos.x in 50..99) {
            nextPos = Pos(0, 100 + nextPos.x)
            nextDir = Direction.Right
        } else {
            nextPos = Pos(nextPos.x - 100, lastIndex)
            nextDir = Direction.Up
        }
    }
    if (nextPos.y > lastIndex || (dir == Direction.Down && this[nextPos.y][nextPos.x] == ' ')) {
        if (nextPos.x in 0..49) {
            nextPos = Pos(nextPos.x + 100, 0)
            nextDir = Direction.Down
        } else if (nextPos.x in 50..99) {
            nextPos = Pos(49, 100 + nextPos.x)
            nextDir = Direction.Left
        } else {
            nextPos = Pos(99, nextPos.x - 50)
            nextDir = Direction.Left
        }
    }
    if (nextPos.x < 0 || (dir == Direction.Left && this[nextPos.y][nextPos.x] == ' ')) {
        if (nextPos.y in 0..49) {
            nextPos = Pos(0, 149 - nextPos.y)
            nextDir = Direction.Right
        } else if (nextPos.y in 50..99) {
            nextPos = Pos(nextPos.y - 50, 100)
            nextDir = Direction.Down
        } else if (nextPos.y in 100..149) {
            nextPos = Pos(50, 49 - (nextPos.y - 100)) // 100 -> 49 ; 149 -> 0
            nextDir = Direction.Right
        } else {
            nextPos = Pos(nextPos.y - 100, 0)
            nextDir = Direction.Down
        }
    }
    if (nextPos.x > this[nextPos.y].lastIndex || (dir == Direction.Right && this[nextPos.y][nextPos.x] == ' ')) {
        if (nextPos.y in 0..49) {
            nextPos = Pos(99, 100 + (49 - nextPos.y))
            nextDir = Direction.Left
        } else if (nextPos.y in 50..99) {
            nextPos = Pos(50 + nextPos.y, 49)
            nextDir = Direction.Up
        } else if (nextPos.y in 100..149) {
            nextPos = Pos(149, 49 - (nextPos.y - 100)) // 100 -> 49 ; 149 -> 0
            nextDir = Direction.Left
        } else {
            nextPos = Pos(nextPos.y - 100, 149)
            nextDir = Direction.Up
        }
    }

    val c = this[nextPos.y][nextPos.x]
    if (c == ' ') error("!")
    if (c == '#') return pos to dir
    return nextPos to nextDir
}

private fun Array<Array<Char>>.stepOnCubeTestPattern(pos: Pos, dir: Direction): Pair<Pos, Direction> {
    var nextDir = dir
    var nextPos = when (dir) {
        Direction.Right -> Pos(pos.x + 1, pos.y)
        Direction.Down -> Pos(pos.x, pos.y + 1)
        Direction.Left -> Pos(pos.x - 1, pos.y)
        Direction.Up -> Pos(pos.x, pos.y - 1)
    }
    if (nextPos.y < 0 || (dir == Direction.Up && this[nextPos.y][nextPos.x] == ' ')) {
        if (nextPos.x in 0..3) {
            nextPos = Pos(11 - nextPos.x, 0)
            nextDir = Direction.Down
        } else if (nextPos.x in 4..7) {
            // This one is required
            nextPos = Pos(8, nextPos.x - 4)
            nextDir = Direction.Right
        } else if (nextPos.x in 8..11) {
            nextPos = Pos(3 - (nextPos.x - 8), 4)
            nextDir = Direction.Down
        } else {
            nextPos = Pos(11, 7 - (nextPos.x - 12))
            nextDir = Direction.Left
        }
    }
    if (nextPos.y > lastIndex || (dir == Direction.Down && this[nextPos.y][nextPos.x] == ' ')) {
        if (nextPos.x in 0..3) {
            nextPos = Pos(11 - nextPos.x, 11)
            nextDir = Direction.Up
        } else if (nextPos.x in 4..7) {
            nextPos = Pos(8, 11 - (nextPos.x - 4))
            nextDir = Direction.Right
        } else if (nextPos.x in 8..11) {
            // This one is required
            nextPos = Pos(3 - (nextPos.x - 8), 7)
            nextDir = Direction.Up
        } else {
            nextPos = Pos(0, 7 - (nextPos.x - 12))
            nextDir = Direction.Right
        }
    }
    if (nextPos.x < 0 || (dir == Direction.Left && this[nextPos.y][nextPos.x] == ' ')) {
        if (nextPos.y in 0..3) {
            nextPos = Pos(4 + nextPos.y, 4)
            nextDir = Direction.Down
        } else if (nextPos.y in 4..7) {
            nextPos = Pos(15 - (nextPos.y - 4), 11)
            nextDir = Direction.Up
        } else {
            nextPos = Pos(7 - (nextPos.y - 8), 7)
            nextDir = Direction.Up
        }
    }
    if (nextPos.x > this[nextPos.y].lastIndex || (dir == Direction.Right && this[nextPos.y][nextPos.x] == ' ')) {
        if (nextPos.y in 0..3) {
            nextPos = Pos(15, 11 - nextPos.y)
            nextDir = Direction.Left
        } else if (nextPos.y in 4..7) {
            // This one is required
            nextPos = Pos(15 - (nextPos.y - 4), 8)
            nextDir = Direction.Down
        } else {
            nextPos = Pos(11, 3 - (nextPos.y - 8))
            nextDir = Direction.Left
        }
    }

    val c = this[nextPos.y][nextPos.x]
    if (c == ' ') error("!")
    if (c == '#') return pos to dir
    return nextPos to nextDir
}

private fun Array<Array<Char>>.stepOnBoard(pos: Pos, dir: Direction): Pos {
    var nextPos = when (dir) {
        Direction.Right -> Pos(pos.x + 1, pos.y)
        Direction.Down -> Pos(pos.x, pos.y + 1)
        Direction.Left -> Pos(pos.x - 1, pos.y)
        Direction.Up -> Pos(pos.x, pos.y - 1)
    }
    if (nextPos.y < 0 || (dir == Direction.Up && this[nextPos.y][nextPos.x] == ' ')) {
        nextPos = Pos(nextPos.x, indexOfLast { it[nextPos.x] != ' ' })
    }
    if (nextPos.y > lastIndex || (dir == Direction.Down && this[nextPos.y][nextPos.x] == ' ')) {
        nextPos = Pos(nextPos.x, indexOfFirst { it[nextPos.x] != ' ' })
    }
    if (nextPos.x < 0 || (dir == Direction.Left && this[nextPos.y][nextPos.x] == ' ')) {
        nextPos = Pos(this[nextPos.y].indexOfLast { it != ' ' }, nextPos.y)
    }
    if (nextPos.x > this[nextPos.y].lastIndex || (dir == Direction.Right && this[nextPos.y][nextPos.x] == ' ')) {
        nextPos = Pos(this[nextPos.y].indexOfFirst { it != ' ' }, nextPos.y)
    }
    val c = this[nextPos.y][nextPos.x]
    if (c == ' ') error("!")
    if (c == '#') return pos
    return nextPos
}

private data class Pos(val x: Int, val y: Int)

private fun parseBoard(input: List<String>): Array<Array<Char>> {
    val sizeX = input.maxOf { it.length }
    val sizeY = input.size

    val board = Array(sizeY) { Array(sizeX) { ' ' } }

    input.forEachIndexed { y, line ->
        line.forEachIndexed { x, c ->
            board[y][x] = c
        }
    }

    return board
}

private enum class Direction {
    Right, Down, Left, Up;

    fun turn(instruction: Instruction): Direction {
        return when (instruction) {
            Instruction.TurnRight -> when (this) {
                Up -> Right
                Right -> Down
                Down -> Left
                Left -> Up
            }

            Instruction.TurnLeft -> when (this) {
                Up -> Left
                Right -> Up
                Down -> Right
                Left -> Down
            }

            else -> this
        }
    }
}

private interface Instruction {
    data class Move(val steps: Int) : Instruction
    object TurnRight : Instruction
    object TurnLeft : Instruction
}

private fun parseInstructions(line: String): List<Instruction> {
    val parts = "(\\d+|R|L)".toRegex().findAll(line).toList().map { it.value }
    return parts.map {
        when (it) {
            "L" -> Instruction.TurnLeft
            "R" -> Instruction.TurnRight
            else -> Instruction.Move(it.toInt())
        }
    }
}

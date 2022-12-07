package year2022.day07

import check
import readInput

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day07_test")
    check(part1(testInput), 95437)
    check(part2(testInput), 24933642)

    val input = readInput("2022", "Day07")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    return parseDirectoriesByPath(input).values.filter { it.size <= 100_000 }.sumOf { it.size }
}

private fun part2(input: List<String>): Int {
    val directoriesByPath = parseDirectoriesByPath(input)
    val usedSpace = directoriesByPath[listOf("/")]!!.size
    val minSizeToDelete = usedSpace - 40_000_000
    val sortedBySize = directoriesByPath.values.sortedBy { it.size }
    val dirToDelete = sortedBySize.first { it.size >= minSizeToDelete }
    return dirToDelete.size
}

private fun parseDirectoriesByPath(input: List<String>): Map<List<String>, File> {
    val path = mutableListOf<String>()
    return buildMap {
        for (line in input) {
            if (line == "$ cd ..") path.removeLast()
            else if (line.startsWith("$ cd")) path += line.split(' ').last()
            else if (line == "$ ls") put(path.toList(), File(path.toList()))
            else {
                val parent = get(path)!!
                val (info, name) = line.split(' ')
                val file = File(path + name, isFile = info != "dir", size = info.toIntOrNull() ?: 0)
                parent.children += file
                if (file.isFile) {
                    val pathCopy = path.toMutableList()
                    while (pathCopy.isNotEmpty()) {
                        get(pathCopy)!!.size += file.size
                        pathCopy.removeLast()
                    }
                } else put(file.path, file)
            }
        }
    }
}

private data class File(
    val path: List<String>,
    val children: MutableList<File> = arrayListOf(),
    val isFile: Boolean = false,
    var size: Int = 0
)
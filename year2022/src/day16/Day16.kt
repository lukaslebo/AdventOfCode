package day16

import check
import readInput
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.milliseconds

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day16_test")
    check(part1(testInput), 1651)
    check(part2(testInput), 1707)

    val input = readInput("2022", "Day16")
    measureTimeMillis { print(part1(input)) }.also { println(" (Part 1 took ${it.milliseconds})") }
    measureTimeMillis { print(part2(input)) }.also { println(" (Part 2 took ${it.milliseconds})") }
}

private fun part1(input: List<String>): Int {
    val valvesById = parseValvesById(input)
    val shortestPathSteps = getStepsByPath(valvesById)
    return findMaxPressureRelease(
        pos = "AA",
        remainingValves = valvesById.values.filter { it.flowRate > 0 }.mapTo(hashSetOf()) { it.id },
        valvesById = valvesById,
        shortestPathSteps = shortestPathSteps,
    )
}

private fun part2(input: List<String>): Int {
    val valvesById = parseValvesById(input)
    val shortestPathSteps = getStepsByPath(valvesById)
    return findMaxPressureReleaseWithElephant(
        posA = "AA",
        posB = "AA",
        remainingValves = valvesById.values.filter { it.flowRate > 0 }.mapTo(hashSetOf()) { it.id },
        valvesById = valvesById,
        shortestPathSteps = shortestPathSteps,
    )
}

private fun getStepsByPath(valvesById: Map<String, Valve>): Map<String, Int> {
    val shortestPathSteps = hashMapOf<String, Int>()
    for (a in valvesById.keys) {
        for (b in valvesById.keys) {
            if (a == b) continue
            val key = "$a>$b"
            shortestPathSteps[key] = findShortestPathSteps(valvesById, a, b)
        }
    }
    return shortestPathSteps
}

private data class CacheKey1(
    val pos: String,
    val timeLeft: Int,
    val remainingValves: Set<String>,
)

private fun findMaxPressureRelease(
    pos: String,
    timeLeft: Int = 30,
    remainingValves: Set<String>,
    valvesById: Map<String, Valve>,
    shortestPathSteps: Map<String, Int>,
    cache: HashMap<CacheKey1, Int> = hashMapOf(),
): Int {
    if (remainingValves.isEmpty()) {
        return 0
    }
    val key = CacheKey1(
        pos = pos,
        timeLeft = timeLeft,
        remainingValves = remainingValves,
    )
    return cache.getOrPut(key) {
        remainingValves.maxOf {
            val steps = shortestPathSteps["$pos>$it"]!!
            val duration = steps + 1
            val pressureRelease = (timeLeft - duration) * valvesById[it]!!.flowRate
            if (timeLeft - duration <= 0) {
                0
            } else pressureRelease + findMaxPressureRelease(
                pos = it,
                timeLeft = timeLeft - duration,
                remainingValves = remainingValves - it,
                valvesById = valvesById,
                shortestPathSteps = shortestPathSteps,
                cache = cache,
            )
        }
    }
}

private data class CacheKey2(
    val posA: String,
    val posB: String,
    val timeLeftA: Int,
    val timeLeftB: Int,
    val remainingValves: Set<String>,
)

private fun findMaxPressureReleaseWithElephant(
    posA: String,
    posB: String,
    timeLeftA: Int = 26,
    timeLeftB: Int = 26,
    remainingValves: Set<String>,
    valvesById: Map<String, Valve>,
    shortestPathSteps: Map<String, Int>,
    cache: MutableMap<CacheKey2, Int> = ConcurrentHashMap(),
): Int {
    if (remainingValves.isEmpty()) {
        return 0
    }

    if (timeLeftA == 0) {
        return findMaxPressureRelease(
            pos = posB,
            timeLeft = timeLeftB,
            remainingValves = remainingValves,
            valvesById = valvesById,
            shortestPathSteps = shortestPathSteps,
        )
    }
    if (timeLeftB == 0) {
        return findMaxPressureRelease(
            pos = posA,
            timeLeft = timeLeftA,
            remainingValves = remainingValves,
            valvesById = valvesById,
            shortestPathSteps = shortestPathSteps,
        )
    }

    if (remainingValves.size == 1) {
        val valve = valvesById[remainingValves.first()]!!
        val stepsA = findShortestPathSteps(valvesById, posA, valve.id)
        val stepsB = findShortestPathSteps(valvesById, posB, valve.id)
        val durationA = stepsA + 1
        val durationB = stepsB + 1

        val finalReleaseA = (timeLeftA - durationA) * valve.flowRate
        val finalReleaseB = (timeLeftB - durationB) * valve.flowRate

        return max(max(finalReleaseA, finalReleaseB), 0)
    }

    val key = CacheKey2(
        posA = posA,
        posB = posB,
        timeLeftA = timeLeftA,
        timeLeftB = timeLeftB,
        remainingValves = remainingValves,
    )
    return cache.getOrPut(key) {
        val isTopOfCallStack = timeLeftA == 26 && timeLeftB == 26
        val combinations = getCombinations(remainingValves).let {
            if (isTopOfCallStack) it.unique() else it
        }
        if (isTopOfCallStack)
            combinations.parallelStream().map { pair ->
                findPressureReleaseForPair(
                    pair = pair,
                    shortestPathSteps = shortestPathSteps,
                    posA = posA,
                    posB = posB,
                    timeLeftA = timeLeftA,
                    valvesById = valvesById,
                    timeLeftB = timeLeftB,
                    remainingValves = remainingValves,
                    cache = cache,
                )
            }.toList().max()
        else combinations.maxOf { pair ->
            findPressureReleaseForPair(
                pair = pair,
                shortestPathSteps = shortestPathSteps,
                posA = posA,
                posB = posB,
                timeLeftA = timeLeftA,
                valvesById = valvesById,
                timeLeftB = timeLeftB,
                remainingValves = remainingValves,
                cache = cache,
            )
        }
    }
}

private fun findPressureReleaseForPair(
    pair: List<String>,
    shortestPathSteps: Map<String, Int>,
    posA: String,
    posB: String,
    timeLeftA: Int,
    valvesById: Map<String, Valve>,
    timeLeftB: Int,
    remainingValves: Set<String>,
    cache: MutableMap<CacheKey2, Int>,
): Int {
    val (a, b) = pair
    val stepsA = shortestPathSteps["$posA>$a"]!!
    val stepsB = shortestPathSteps["$posB>$b"]!!
    val durationA = stepsA + 1
    val durationB = stepsB + 1
    val pressureReleaseA = (timeLeftA - durationA) * valvesById[a]!!.flowRate
    val pressureReleaseB = (timeLeftB - durationB) * valvesById[b]!!.flowRate
    return if (timeLeftA - durationA <= 0 && timeLeftB - durationB <= 0) {
        0
    } else if (timeLeftA - durationA <= 0) {
        pressureReleaseB + findMaxPressureReleaseWithElephant(
            posA = posA,
            posB = b,
            timeLeftA = 0,
            timeLeftB = timeLeftB - durationB,
            remainingValves = remainingValves - b,
            valvesById = valvesById,
            shortestPathSteps = shortestPathSteps,
            cache = cache,
        )
    } else if (timeLeftB - durationB <= 0) {
        pressureReleaseA + findMaxPressureReleaseWithElephant(
            posA = a,
            posB = posB,
            timeLeftA = timeLeftA - durationA,
            timeLeftB = 0,
            remainingValves = remainingValves - a,
            valvesById = valvesById,
            shortestPathSteps = shortestPathSteps,
            cache = cache,
        )
    } else {
        pressureReleaseA + pressureReleaseB + findMaxPressureReleaseWithElephant(
            posA = a,
            posB = b,
            timeLeftA = timeLeftA - durationA,
            timeLeftB = timeLeftB - durationB,
            remainingValves = remainingValves - a - b,
            valvesById = valvesById,
            shortestPathSteps = shortestPathSteps,
            cache = cache,
        )
    }
}

private fun findShortestPathSteps(graph: Map<String, Valve>, start: String, target: String): Int {
    val stack = ArrayDeque<List<String>>()
    val visited = mutableSetOf(start)
    stack.addLast(listOf(start))
    while (stack.isNotEmpty()) {
        val path = stack.removeFirst()
        val current = path.last()
        val ways = graph[current]?.connectedTo ?: emptyList()
        for (next in ways) {
            if (next !in visited) {
                visited += next
                val newPath = path + next
                if (target == next) return newPath.size - 1
                stack.addLast(newPath)
            }
        }
    }
    error("no path found")
}

private fun parseValvesById(input: List<String>): Map<String, Valve> {
    val pattern =
        "Valve (?<id>[A-Z]{2}) has flow rate=(?<flowRate>\\d+); tunnels? leads? to valves? (?<connections>[A-Z, ]+)".toRegex()
    return input.map { line ->
        val groups = pattern.find(line)?.groups ?: error("Error: $line")
        val id = groups["id"]?.value ?: error("!")
        val flowRate = groups["flowRate"]?.value?.toInt() ?: error("!")
        val connections = groups["connections"]?.value?.split(", ") ?: error("!")
        Valve(id, flowRate, connections)
    }.associateBy { it.id }
}

private data class Valve(
    val id: String,
    val flowRate: Int,
    val connectedTo: List<String>,
)

private fun getCombinations(valves: Set<String>, current: List<String> = emptyList()): Set<List<String>> {
    if (current.size == 2) return setOf(current)
    val result = mutableSetOf<List<String>>()
    for (next in valves) {
        result += getCombinations(valves - next, current + next)
    }
    return result
}

private fun Set<List<String>>.unique() = map { it.toSet() }.toSet().map { it.toList() }

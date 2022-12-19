package year2022.day19

import check
import readInput
import kotlin.math.max
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.milliseconds

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day19_test")
    check(part1(testInput), 33)
    check(part2(testInput), 56 * 62)

    val input = readInput("2022", "Day19")
    measureTimeMillis { print(part1(input)) }.also { println(" (Part 1 took ${it.milliseconds})") }
    measureTimeMillis { print(part2(input)) }.also { println(" (Part 2 took ${it.milliseconds})") }
}

private fun part1(input: List<String>) = input.toCosts()
    .mapIndexed { i, cost ->
        val blueprintId = i + 1
        val highestGeodeCount = getHighestGeodeCount(cost, timeLeft = 24)
        blueprintId * highestGeodeCount
    }.sum()

private fun part2(input: List<String>) = input.take(3)
    .toCosts()
    .map { getHighestGeodeCount(it, timeLeft = 32) }
    .reduce(Int::times)

private data class CacheKey(
    val materials: Materials,
    val timeLeft: Int,
    val oreRobots: Int,
    val clayRobots: Int,
    val obsidianRobots: Int,
    val geodeRobots: Int,
)

private fun getHighestGeodeCount(
    cost: Cost,
    timeLeft: Int,
    materials: Materials = Materials(),
    oreRobots: Int = 1,
    clayRobots: Int = 0,
    obsidianRobots: Int = 0,
    geodeRobots: Int = 0,
    cache: MutableMap<CacheKey, Int> = hashMapOf(),
    maxGeodeRobotsByTimeLeft: MutableMap<Int, MaxRobots> = hashMapOf(),
): Int {
    if (timeLeft == 0) return 0

    val (maxOreRobots, maxClayRobots, maxObsidianRobots, maxGeodeRobots) = maxGeodeRobotsByTimeLeft.compute(timeLeft) { _, v ->
        MaxRobots(
            max(oreRobots, v?.maxOreRobots ?: 0),
            max(clayRobots, v?.maxClayRobots ?: 0),
            max(obsidianRobots, v?.maxObsidianRobots ?: 0),
            max(geodeRobots, v?.maxGeodeRobots ?: 0),
        )
    }!!
    if (geodeRobots < maxGeodeRobots || (geodeRobots == 0 && obsidianRobots < maxObsidianRobots - 1) || (obsidianRobots == 0 && clayRobots < maxClayRobots - 1) || (clayRobots == 0 && oreRobots < maxOreRobots - 1)) return 0

    val minTimeToFirstGeodeRobot =
        if (geodeRobots == 0) minTimeRequired(cost.geodeRobotObsidianCost - materials.obsidian, obsidianRobots)
        else 0
    val minTimeToFirstObsidianRobot =
        if (obsidianRobots == 0) minTimeRequired(cost.obsidianRobotClayCost - materials.clay, clayRobots)
        else 0
    val minTimeToFirstClayRobot =
        if (clayRobots == 0) minTimeRequired(cost.clayRobotOreCost - materials.ore, oreRobots)
        else 0
    if (timeLeft <= minTimeToFirstGeodeRobot + minTimeToFirstObsidianRobot + minTimeToFirstClayRobot) return 0

    val key = CacheKey(
        materials = materials,
        timeLeft = timeLeft,
        oreRobots = oreRobots,
        clayRobots = clayRobots,
        obsidianRobots = obsidianRobots,
        geodeRobots = geodeRobots,
    )
    return cache.getOrPut(key) {
        val newMaterials = materials.copy()
        var newTimeLeft = timeLeft
        var buildableRobots = Robot.values().filter { it.canBuild(newMaterials, cost) }
        while (buildableRobots.size == 1) {
            if (--newTimeLeft == 0) return@getOrPut timeLeft * geodeRobots
            newMaterials.addMined(oreRobots, clayRobots, obsidianRobots)
            buildableRobots = Robot.values().filter { it.canBuild(newMaterials, cost) }
        }

        val avoidSkip =
            (clayRobots == 0 && buildableRobots.size == 3) || (obsidianRobots == 0 && buildableRobots.size == 4) || buildableRobots.size == 5
        if (avoidSkip) {
            buildableRobots = buildableRobots - Robot.Skip
        }

        val minedGeodes = geodeRobots * (1 + timeLeft - newTimeLeft)

        val results = arrayListOf<Int>()
        for (newRobot in buildableRobots) {
            results += (minedGeodes + getHighestGeodeCount(
                cost = cost,
                materials = newMaterials.copy().minusBuildCost(newRobot, cost)
                    .addMined(oreRobots, clayRobots, obsidianRobots),
                timeLeft = newTimeLeft - 1,
                oreRobots = oreRobots + if (newRobot == Robot.OreRobot) 1 else 0,
                clayRobots = clayRobots + if (newRobot == Robot.ClayRobot) 1 else 0,
                obsidianRobots = obsidianRobots + if (newRobot == Robot.ObsidianRobot) 1 else 0,
                geodeRobots = geodeRobots + if (newRobot == Robot.GeodeRobot) 1 else 0,
                cache = cache,
                maxGeodeRobotsByTimeLeft = maxGeodeRobotsByTimeLeft,
            ))
        }
        return@getOrPut results.max()
    }
}

private fun List<String>.toCosts() = map { line ->
    val nums = line.split(' ').mapNotNull { it.toIntOrNull() }
    Cost(
        oreRobotOreCost = nums[0],
        clayRobotOreCost = nums[1],
        obsidianRobotOreCost = nums[2],
        obsidianRobotClayCost = nums[3],
        geodeRobotOreCost = nums[4],
        geodeRobotObsidianCost = nums[5],
    )
}

private enum class Robot {
    Skip, OreRobot, ClayRobot, ObsidianRobot, GeodeRobot;

    fun oreCost(cost: Cost): Int = when (this) {
        Skip -> 0
        OreRobot -> cost.oreRobotOreCost
        ClayRobot -> cost.clayRobotOreCost
        ObsidianRobot -> cost.obsidianRobotOreCost
        GeodeRobot -> cost.geodeRobotOreCost
    }

    fun clayCost(cost: Cost): Int = when (this) {
        ObsidianRobot -> cost.obsidianRobotClayCost
        else -> 0
    }

    fun obsidianCost(cost: Cost): Int = when (this) {
        GeodeRobot -> cost.geodeRobotObsidianCost
        else -> 0
    }

    fun canBuild(materials: Materials, cost: Cost): Boolean = when (this) {
        Skip -> true
        OreRobot -> materials.ore >= cost.oreRobotOreCost
        ClayRobot -> materials.ore >= cost.clayRobotOreCost
        ObsidianRobot -> materials.ore >= cost.obsidianRobotOreCost && materials.clay >= cost.obsidianRobotClayCost
        GeodeRobot -> materials.ore >= cost.geodeRobotOreCost && materials.obsidian >= cost.geodeRobotObsidianCost
    }
}

private data class Cost(
    val oreRobotOreCost: Int,
    val clayRobotOreCost: Int,
    val obsidianRobotOreCost: Int,
    val obsidianRobotClayCost: Int,
    val geodeRobotOreCost: Int,
    val geodeRobotObsidianCost: Int,
)

private data class Materials(
    var ore: Int = 0,
    var clay: Int = 0,
    var obsidian: Int = 0,
) {
    fun minusBuildCost(robot: Robot, cost: Cost): Materials {
        ore -= robot.oreCost(cost)
        clay -= robot.clayCost(cost)
        obsidian -= robot.obsidianCost(cost)
        return this
    }

    fun addMined(oreRobots: Int, clayRobots: Int, obsidianRobots: Int): Materials {
        ore += oreRobots
        clay += clayRobots
        obsidian += obsidianRobots
        return this
    }
}

private fun minTimeRequired(units: Int, robots: Int): Int {
    var acc = 0
    var r = robots
    var n = 1
    while (acc < units) {
        acc += r++
        n++
    }
    return n
}

private data class MaxRobots(
    val maxOreRobots: Int,
    val maxClayRobots: Int,
    val maxObsidianRobots: Int,
    val maxGeodeRobots: Int,
)
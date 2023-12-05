package year2023.day05

import check
import readInput
import kotlin.math.max
import kotlin.math.min

fun main() {
    val testInput = readInput("2023", "Day05_test")
    check(part1(testInput), 35)
    check(part2(testInput), 46)

    val input = readInput("2023", "Day05")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val seeds = input.first().substringAfter("seeds: ").split(" ").map { it.toLong() }
    val locationBySeed = seeds.associateWith { it }.toMutableMap()
    val seedRangeMappings = input.toSeedRangeMappings()
    for (seed in seeds) {
        for (seedRangeMapping in seedRangeMappings) {
            val location = locationBySeed.getValue(seed)
            locationBySeed[seed] = updateSeedLocation(seedRangeMapping, location)
        }
    }
    return locationBySeed.values.min().toInt()
}

private fun updateSeedLocation(seedRangeMappings: List<SeedRangeMapping>, location: Long): Long {
    var updatedLocation = location
    for (seedRangeMapping in seedRangeMappings) {
        if (updatedLocation in seedRangeMapping.source) {
            updatedLocation = seedRangeMapping.mapSeed(updatedLocation)
            break
        }
    }
    return updatedLocation
}

private fun part2(input: List<String>): Int {
    val seedRangeMappings = input.toSeedRangeMappings()
    var currentSeedRanges = input.toSeedRanges()
    for (seedRangeMapping in seedRangeMappings) {
        currentSeedRanges = currentSeedRanges.flatMap { updateSeedRange(it, seedRangeMapping) }.toSet()
    }
    return currentSeedRanges.minOf { it.first }.toInt()
}

private fun updateSeedRange(
    seedRange: LongRange,
    seedRangeMappings: List<SeedRangeMapping>,
) = buildSet {
    var remainingSeedRange = seedRange
    for (seedRangeMapping in seedRangeMappings.sortedBy { it.source.first }) {
        if (remainingSeedRange.last < seedRangeMapping.source.first || remainingSeedRange.first > seedRangeMapping.source.last) {
            continue
        }
        if (remainingSeedRange.first < seedRangeMapping.source.first) {
            add(remainingSeedRange.first..<seedRangeMapping.source.first)
        }
        val lowerBound = max(seedRangeMapping.source.first, remainingSeedRange.first)
        val upperBound = min(seedRangeMapping.source.last, remainingSeedRange.last)
        add(seedRangeMapping.mapSeed(lowerBound)..seedRangeMapping.mapSeed(upperBound))
        if (remainingSeedRange.last > seedRangeMapping.source.last) {
            remainingSeedRange = seedRangeMapping.source.last + 1..remainingSeedRange.last
        } else {
            remainingSeedRange = LongRange.EMPTY
            break
        }
    }
    if (remainingSeedRange != LongRange.EMPTY) {
        add(remainingSeedRange)
    }
}

private fun List<String>.toSeedRangeMappings(): List<List<SeedRangeMapping>> =
    drop(2).fold(mutableListOf<MutableList<SeedRangeMapping>>()) { acc, l ->
        if (acc.isEmpty() || l.isBlank()) {
            acc += mutableListOf<SeedRangeMapping>()
        }
        if (l.isNotBlank() && l.first().isDigit()) {
            val (destStart, sourceStart, length) = l.split(" ").map { it.toLong() }
            acc.last() += SeedRangeMapping(
                sourceStart until (sourceStart + length),
                destStart until (destStart + length)
            )
        }
        acc
    }

private fun List<String>.toSeedRanges() = first()
    .substringAfter("seeds: ")
    .split(" ")
    .map { it.toLong() }
    .chunked(2)
    .map {
        val start = it.first()
        val length = it.last()
        start until (start + length)
    }
    .toSet()

private fun SeedRangeMapping.mapSeed(seedLocation: Long): Long {
    return if (seedLocation in source) {
        val offset = seedLocation - source.first
        destination.first + offset
    } else seedLocation
}

private data class SeedRangeMapping(
    val source: LongRange,
    val destination: LongRange,
)

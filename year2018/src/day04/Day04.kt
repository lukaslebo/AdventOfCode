package day04

import check
import readInput
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main() {
    val testInput = readInput("2018", "Day04_test")
    check(part1(testInput), 240)
    check(part2(testInput), 4455)

    val input = readInput("2018", "Day04")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val stats = input.parseEvents().computeStatistics()
    val targetGuardStats = stats.maxBy { it.totalMinutesSlept }
    return targetGuardStats.guard.id * targetGuardStats.minuteMostSlept
}

private fun part2(input: List<String>): Int {
    val stats = input.parseEvents().computeStatistics()
    val targetGuardStats = stats.maxBy { it.minuteMostSleptCount }
    return targetGuardStats.guard.id * targetGuardStats.minuteMostSlept
}

private fun List<Event>.computeStatistics(): List<Stats> {
    val guardsById = mutableMapOf<Int, Guard>()
    var currentGuard: Guard? = null
    var startSleep: LocalDateTime? = null
    for (event in this) {
        when (event) {
            is BeginsShift -> currentGuard = guardsById.getOrPut(event.guardId) { Guard(event.guardId) }
            is FallsAsleep -> startSleep = startSleep ?: event.datetime
            is WakesUp -> {
                for (minute in startSleep!!.minute until event.datetime.minute) {
                    currentGuard!!.minutesSlept.merge(minute, 1, Int::plus)
                }
                startSleep = null
            }
        }

    }
    return guardsById.values.toList().map { it.computeStats() }
}

private fun Guard.computeStats(): Stats {
    val (minuteMostSlept, minuteMostSleptCount) = minutesSlept.entries.maxBy { it.value }
    return Stats(
        guard = this,
        totalMinutesSlept = minutesSlept.values.sum(),
        minuteMostSlept = minuteMostSlept,
        minuteMostSleptCount = minuteMostSleptCount,
    )
}

private data class Guard(
    val id: Int,
    val minutesSlept: MutableMap<Int, Int> = mutableMapOf(0 to 0),
)

private data class Stats(
    val guard: Guard,
    val totalMinutesSlept: Int,
    val minuteMostSlept: Int,
    val minuteMostSleptCount: Int,
)

private sealed interface Event {
    val datetime: LocalDateTime
}

private data class WakesUp(override val datetime: LocalDateTime) : Event
private data class FallsAsleep(override val datetime: LocalDateTime) : Event
private data class BeginsShift(override val datetime: LocalDateTime, val guardId: Int) : Event

private fun List<String>.parseEvents() = map { it.parseEvent() }.sortedBy { it.datetime }
private val format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
private fun String.parseEvent(): Event {
    val dateTime = LocalDateTime.parse(substring(1, 17), format)
    return when (val text = substring(19)) {
        "falls asleep" -> FallsAsleep(dateTime)
        "wakes up" -> WakesUp(dateTime)
        else -> BeginsShift(dateTime, text.substringAfter("#").substringBefore(" ").toInt())
    }
}

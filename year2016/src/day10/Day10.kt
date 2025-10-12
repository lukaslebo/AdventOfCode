package day10

import readInput

fun main() {
    val input = readInput("2016", "Day10")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val (receiver, chipToBotId) = input.parseFactory()
    val botsById = receiver.filterIsInstance<Bot>().associateBy { it.id }
    for ((chip, botId) in chipToBotId) {
        val bot = botsById[botId]!!
        bot.receive(chip)
    }
    val requiredChips = setOf(61, 17)
    val targetBot = botsById.values.first { it.seen == requiredChips }
    return targetBot.id
}

private fun part2(input: List<String>): Int {
    val (receiver, chipToBotId) = input.parseFactory()
    val botsById = receiver.filterIsInstance<Bot>().associateBy { it.id }
    for ((chip, botId) in chipToBotId) {
        val bot = botsById[botId]!!
        bot.receive(chip)
    }
    return receiver.filterIsInstance<Output>()
        .filter { it.id in 0..2 }
        .map { it.chips.first() }
        .fold(1) { acc, n -> acc * n }
}

private fun List<String>.parseFactory(): Pair<List<ChipReceiver>, List<Pair<Int, Int>>> {
    val botsById = mutableMapOf<Int, Bot>()
    val outputsById = mutableMapOf<Int, Output>()
    val chipToBotId = mutableListOf<Pair<Int, Int>>()
    for (line in this) {
        val parts = line.split(" ")
        if (line.startsWith("value")) {
            val chip = parts[1].toInt()
            val botId = parts[5].toInt()
            chipToBotId += chip to botId
        } else {
            val id = parts[1].toInt()
            val isLowOutput = parts[5] == "output"
            val lowId = parts[6].toInt()
            val isHighOutput = parts[10] == "output"
            val highId = parts[11].toInt()
            val bot = botsById.getOrPut(id) { Bot(id) }
            if (isLowOutput) bot.lowReceiver = outputsById.getOrPut(lowId) { Output(lowId) }
            else bot.lowReceiver = botsById.getOrPut(lowId) { Bot(lowId) }
            if (isHighOutput) bot.highReceiver = outputsById.getOrPut(highId) { Output(highId) }
            else bot.highReceiver = botsById.getOrPut(highId) { Bot(highId) }
        }
    }
    val receivers = botsById.values + outputsById.values
    return receivers to chipToBotId
}

private sealed interface ChipReceiver {
    fun receive(chip: Int)
}

private data class Output(val id: Int) : ChipReceiver {
    val chips = mutableListOf<Int>()

    override fun receive(chip: Int) {
        chips += chip
    }
}

private data class Bot(val id: Int) : ChipReceiver {
    private val chips = mutableListOf<Int>()
    lateinit var lowReceiver: ChipReceiver
    lateinit var highReceiver: ChipReceiver
    val seen = mutableSetOf<Int>()


    override fun receive(chip: Int) {
        chips += chip
        seen += chip
        if (chips.size == 2) {
            val (low, high) = chips.sorted()
            lowReceiver.receive(low)
            highReceiver.receive(high)
            chips.clear()
        }
    }
}

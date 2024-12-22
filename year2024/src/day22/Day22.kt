package day22

import check
import readInput

fun main() {
    val testInput1 = readInput("2024", "Day22_test_part1")
    val testInput2 = readInput("2024", "Day22_test_part2")
    check(part1(testInput1), 37327623)
    check(part2(testInput2), 23)

    val input = readInput("2024", "Day22")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>) = input.sumOf {
    var secret = it.toLong()
    repeat(2000) {
        secret = secret.nextSecret()
    }
    secret
}

private fun part2(input: List<String>): Int {
    val priceSequencePerMonkey = input.map {
        val secret = it.toLong()
        val priceSequence = mutableListOf<MonkeyPrice>()
        priceSequence += MonkeyPrice(
            secret = secret,
            bananas = secret.bananas(),
        )
        repeat(2000) {
            priceSequence += priceSequence.last().nextMonkeyPrice()
        }
        priceSequence
    }

    return priceSequencePerMonkey
        .flatMap { priceSequence ->
            priceSequence
                .drop(1)
                .windowed(4) { groupOf4 ->
                    Trade(
                        changeSequence = groupOf4.map { it.change!! },
                        bananas = groupOf4.last().bananas,
                    )
                }
                .distinctBy { it.changeSequence }
        }
        .groupBy { it.changeSequence }
        .mapValues { (_, value) -> value.sumOf { it.bananas } }
        .values
        .max()
}

private fun Long.nextSecret(): Long {
    val num = this
    val step1 = ((num * 64) xor num) % 16777216
    val step2 = ((step1 / 32) xor step1) % 16777216
    val step3 = ((step2 * 2048) xor step2) % 16777216
    return step3
}

private data class MonkeyPrice(
    val secret: Long,
    val bananas: Int,
    val change: Int? = null,
)

private data class Trade(
    val changeSequence: List<Int>,
    val bananas: Int,
)

private fun Long.bananas() = toString().last().digitToInt()

private fun MonkeyPrice.nextMonkeyPrice(): MonkeyPrice {
    val nextSecret = secret.nextSecret()
    val nextBananas = nextSecret.bananas()
    return MonkeyPrice(
        secret = nextSecret,
        bananas = nextBananas,
        change = nextBananas - bananas,
    )
}

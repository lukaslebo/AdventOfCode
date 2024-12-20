package day20

import readInput
import kotlin.math.sqrt

fun main() {
    val input = readInput("2015", "Day20")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val minPresents = input.first().toInt()
    val pf = PrimeFactors(minPresents / 10)
    for (n in 1..minPresents) {
        val factors = pf.getFactors(n)
        val presents = factors.sum() * 10
        if (presents >= minPresents) return n
    }
    error("!")
}

private fun part2(input: List<String>): Int {
    val minPresents = input.first().toInt()
    val pf = PrimeFactors(minPresents / 11)
    for (n in minPresents / 50..minPresents) {
        val factors = pf.getFactors(n).filter { it >= n / 50 }
        val presents = factors.sum() * 11
        if (presents >= minPresents) return n
    }
    error("!")
}


private class PrimeFactors(
    max: Int
) {
    var spf = getSpf(max)

    private fun getSpf(max: Int): Array<Int> {
        val spf = Array(max) { it }
        for (i in 2..sqrt(max.toDouble()).toInt()) {
            if (spf[i] == i) {
                for (j in i * i until max step i) {
                    if (spf[j] == j) {
                        spf[j] = i
                    }
                }
            }
        }
        return spf
    }

    fun getPrimeFactors(n: Int): List<Int> {
        var num = n
        val ret = arrayListOf<Int>()
        while (num != 1) {
            ret.add(spf[num])
            num /= spf[num]
        }
        return ret
    }

    fun getFactors(n: Int): List<Int> {
        val primes = getPrimeFactors(n)
        val result = hashSetOf<Int>()
        val queue = ArrayDeque(primes)
        while (queue.isNotEmpty()) {
            val p = queue.removeFirst()
            result += result.map { p * it }
            result += p
        }
        result += 1
        return result.toList().sorted()
    }
}

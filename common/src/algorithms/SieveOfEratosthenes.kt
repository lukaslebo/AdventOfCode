package algorithms

import kotlin.math.sqrt

fun sieveOfEratosthenes(limit: Int): List<Int> {
    if (limit < 2) return emptyList()

    val isPrime = BooleanArray(limit + 1) { true }
    isPrime[0] = false
    isPrime[1] = false

    val sqrtLimit = sqrt(limit.toDouble()).toInt()
    for (p in 2..sqrtLimit) {
        if (isPrime[p]) {
            for (multiple in p * p..limit step p) {
                isPrime[multiple] = false
            }
        }
    }

    return buildList {
        for (i in 2..limit) {
            if (isPrime[i]) add(i)
        }
    }
}

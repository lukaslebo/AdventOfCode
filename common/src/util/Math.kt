package util

fun Iterable<Long>.lcm(): Long = reduce(::lcm)
fun Iterable<Long>.gcd(): Long = reduce(::gcd)

/** Least common multiple */
fun lcm(a: Long, b: Long): Long {
    return a / gcd(a, b) * b
}

/** Greatest common divisor */
fun gcd(a: Long, b: Long): Long {
    if (b == 0L) return a
    return gcd(b, a % b)
}

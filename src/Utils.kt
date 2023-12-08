import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(year: String, name: String): List<String> = File("input/$year", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

fun <T> check(actual: T, expected: T) {
    kotlin.check(actual == expected) { "Got $actual but expected $expected." }
}

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

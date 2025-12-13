import java.io.File

/**
 * Reads lines from the given input txt file.
 */
fun readInput(year: String, name: String): List<String> = File("year$year/input", "$name.txt")
    .readLines()

fun <T> check(actual: T, expected: T) {
    kotlin.check(actual == expected) { "Got $actual but expected $expected." }
}

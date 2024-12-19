package year2016.day05

import check
import md5
import readInput

fun main() {
    check(part1("abc"), "18f47a30")
    check(part2("abc"), "05ace8e3")

    val input = readInput("2016", "Day05").first()
    println(part1(input))
    println(part2(input))
}

private fun part1(doorId: String): String {
    val fiveZeros = "0".repeat(5)
    var password = ""
    for (i in 0..Int.MAX_VALUE) {
        val md5 = "$doorId$i".md5()
        if (md5.startsWith(fiveZeros)) {
            password += md5[5]

            if (password.length == 8) break
        }
    }
    return password
}

private fun part2(doorId: String): String {
    val fiveZeros = "0".repeat(5)
    val password = arrayOfNulls<Char>(8)
    for (i in 0..Int.MAX_VALUE) {
        val md5 = "$doorId$i".md5()
        if (md5.startsWith(fiveZeros)) {
            val index = md5[5].digitToIntOrNull() ?: continue
            if (index !in password.indices || password[index] != null) continue
            password[index] = md5[6]

            if (password.none { it == null }) break
        }
    }
    return password.joinToString("")
}

package util

import readProgramParams
import requestEndpoint
import java.io.File

private val years = setOf(2015, 2022, 2023)
private val days = 1..25

/**
 * Downloads all the inputs for the specified years and days.
 *
 * Run with args: --session=abc
 * Your AoC Session is required.
 */
fun main(args: Array<String>) {
    val session = args.readProgramParams().getValue("session")

    for (year in years) {
        days@ for (day in days) {
            val paddedDay = day.toString().padStart(2, '0')
            val file = File("input/$year/Day$paddedDay.txt")
            if (file.exists()) continue@days

            val input = requestEndpoint("https://adventofcode.com/$year/day/$day/input", session)
            file.writeText(input)
        }
    }
}

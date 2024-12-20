package download

import readProgramParams
import requestEndpoint
import java.io.File
import java.time.LocalDateTime

private val years = setOf(2015, 2022, 2023, 2024)
private val days = 1..25

/**
 * Downloads all the inputs for the specified years and days.
 *
 * Run with args: --session=abc
 * Your AoC Session is required.
 */
fun main(args: Array<String>) {
    val session = args.readProgramParams().getValue("session")
    val now = LocalDateTime.now()
    for (year in years) {
        days@ for (day in days) {
            val paddedDay = day.toString().padStart(2, '0')
            val file = File("year$year/input/Day$paddedDay.txt")
            val date = LocalDateTime.of(year, 12, day, 6, 0)
            if (file.exists() || now < date) continue@days

            println("Requesting input for $paddedDay.12.$year...")
            val input = requestEndpoint("https://adventofcode.com/$year/day/$day/input", session)
            file.writeText(input)
        }
    }
}

package tools

import tools.core.Client

private val years = setOf(2015, 2016, 2017, 2022, 2023, 2024, 2025)
private val days = 1..25

/**
 * Downloads all the inputs for the specified years and days.
 *
 * Make sure to set your session cookie as AOC_SESSION env variable.
 */
fun main() {
    for (year in years) {
        for (day in days) {
            Client.downloadToInputFile(year, day)
        }
    }
}


import java.io.File
import java.time.LocalDate

private val year = LocalDate.now().year
private val day = LocalDate.now().dayOfMonth
private const val generateAllDays = false

fun main() {
    if (generateAllDays) {
        generateAllDays()
    } else {
        generateDay(if (!existsDay(day)) day else day + 1)
    }
}

private fun generateAllDays() {
    val days = (1..25)
    File("input/$year").mkdirs()
    for (day in days) {
        generateDay(day)
    }
}

private fun existsDay(day: Int): Boolean {
    val paddedDay = day.toString().padStart(2, '0')
    return File("src/year$year/day$paddedDay/Day$paddedDay.kt").exists()
}

private fun generateDay(day: Int) {
    val paddedDay = day.toString().padStart(2, '0')
    File("input/$year").mkdirs()
    File("input/$year/Day${paddedDay}_test.txt").writeText("")
    File("input/$year/Day$paddedDay.txt").writeText("")
    File("src/year$year/day$paddedDay").mkdirs()
    val content = kotlinContent.replace("{year}", year.toString()).replace("{day}", paddedDay)
    File("src/year$year/day$paddedDay/Day$paddedDay.kt").writeText(content)
}

private val kotlinContent = """
    package year{year}.day{day}

    import check
    import readInput

    fun main() {
        val testInput = readInput("{year}", "Day{day}_test")
        check(part1(testInput), 0)
        //check(part2(testInput), 0)

        val input = readInput("{year}", "Day{day}")
        println(part1(input))
        println(part2(input))
    }

    private fun part1(input: List<String>): Int {
        return input.size
    }

    private fun part2(input: List<String>): Int {
        return input.size
    }
""".trimIndent()

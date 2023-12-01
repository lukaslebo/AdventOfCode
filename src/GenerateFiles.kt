import java.io.File
import java.time.LocalDate

private val year = LocalDate.now().year
private const val generateAllDays = false

fun main() {
    if (generateAllDays) {
        generateAllDays()
    } else {
        val day = LocalDate.now().dayOfMonth
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
    File("input/$year/Day${paddedDay}_test_part1.txt").writeText("")
    File("input/$year/Day${paddedDay}_test_part2.txt").writeText("")
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
        val testInput1 = readInput("{year}", "Day{day}_test_part1")
        val testInput2 = readInput("{year}", "Day{day}_test_part2")
        check(part1(testInput1), 0)
        check(part2(testInput2), 0)

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

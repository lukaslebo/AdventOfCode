import java.io.File
import java.time.LocalDate

private val year = LocalDate.now().year
private val day = nextMissingDay()

fun main() {
    if (day != null) generateDay(day)
}

private fun nextMissingDay(): Int? {
    val maxDays = if (year >= 2025) 12 else 25
    for (day in 1..maxDays) {
        if (existsDay(day)) continue
        return day
    }
    return null
}

private fun existsDay(day: Int): Boolean {
    val paddedDay = day.toString().padStart(2, '0')
    return File("year$year/src/day$paddedDay/Day$paddedDay.kt").exists()
}

private fun generateDay(day: Int) {
    val paddedDay = day.toString().padStart(2, '0')
    val kotlinDir = File("year$year/src/day$paddedDay")
    val kotlinFile = File("year$year/src/day$paddedDay/Day$paddedDay.kt")
    val inputDir = File("year$year/input")
    val input = File("year$year/input/Day${paddedDay}.txt")
    val testInput = File("year$year/input/Day${paddedDay}_test.txt")

    val content = kotlinContent.replace("{year}", year.toString()).replace("{day}", paddedDay)
    inputDir.mkdirs()
    kotlinDir.mkdirs()
    kotlinFile.writeTextIfNotExists(content)
    input.writeTextIfNotExists("")
    testInput.writeTextIfNotExists("")
}

private fun File.writeTextIfNotExists(text: String) {
    if (!exists()) writeText(text)
}

private val kotlinContent = """
    package day{day}

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

import tools.core.Client
import java.io.File

private val year = 2018//LocalDate.now().year
private val day = nextMissingDay()

private const val includeAutoSubmit = false


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

    val content =
        (if (includeAutoSubmit) kotlinContent.insertAfter(autoSubmit, "println(part2(input))") else kotlinContent)
            .replace("{year}", year.toString())
            .replace("{dayPadded}", paddedDay)
            .replace("{day}", day.toString())

    inputDir.mkdirs()
    kotlinDir.mkdirs()
    kotlinFile.writeTextIfNotExists(content)
    if (Client.hasSession()) Client.downloadToInputFile(year, day)
    input.writeTextIfNotExists("")
    testInput.writeTextIfNotExists("")
}

private fun File.writeTextIfNotExists(text: String) {
    if (!exists()) writeText(text)
}

private val kotlinContent = """
    package day{dayPadded}

    import check
    import readInput

    fun main() {
        val testInput = readInput("{year}", "Day{dayPadded}_test")
        check(part1(testInput), 0)
        //check(part2(testInput), 0)

        val input = readInput("{year}", "Day{dayPadded}")
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

private val autoSubmit = """|

    tools.core.Client.submitAnswer(year = {year}, day = {day}, part = 1, answer = part1(input))
    // tools.core.Client.submitAnswer(year = {year}, day = {day}, part = 2, answer = part2(input))
""".trimMargin()

private fun String.insertAfter(insert: String, after: String): String {
    val at = indexOf(after) + after.length
    return take(at) + insert + substring(at)
}

package tools.core

import java.io.File
import java.net.*
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

object Client {
    private val session = System.getenv()["AOC_SESSION"]

    private val proxyHost = System.getenv()["PROXY_HOST"]
    private val proxyPort = System.getenv()["PROXY_PORT"]?.toIntOrNull()
    private val proxyUser = System.getenv()["PROXY_USER"]
    private val proxyPassword = System.getenv()["PROXY_PASSWORD"]?.toCharArray()

    private val proxy = createProxyOrNull()

    fun getRequest(url: String): String {
        validateSession()

        val requestUrl = URI(url).toURL()
        val connection = requestUrl.connect()
        with(connection as HttpURLConnection) {
            requestMethod = "GET"
            addRequestProperty("Cookie", "session=$session")
            val responseCode = responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                error("HTTP GET request failed with response code: $responseCode")
            }
            return inputStream.bufferedReader().use { it.readText() }
        }
    }

    fun downloadToInputFile(year: Int, day: Int) {
        validateSession()

        if (year >= 2025 && day > 12) return

        val release = LocalDate.of(year, 12, day)
            .atStartOfDay(ZoneId.of("America/New_York"))
        val now = ZonedDateTime.now(ZoneId.of("America/New_York"))
        if (now.isBefore(release)) return

        val paddedDay = day.toString().padStart(2, '0')
        val file = File("year$year/input/Day$paddedDay.txt")
        if (file.exists() && file.readText().isNotBlank()) return

        println("Requesting input for $paddedDay.12.$year...")
        val input = getRequest("https://adventofcode.com/$year/day/$day/input")
        file.writeText(input)
    }

    fun <T> submitAnswer(year: Int, day: Int, part: Int, answer: T) {
        validateSession()

        val answerText = answer.toString()
        val url = URI("https://adventofcode.com/$year/day/$day/answer").toURL()
        val connection = url.connect()
        val formData = "level=$part&answer=$answerText"

        val result = with(connection as HttpURLConnection) {
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            setRequestProperty("Content-Length", formData.toByteArray().size.toString())
            setRequestProperty("Cookie", "session=$session")

            outputStream.use { os ->
                os.write(formData.toByteArray(StandardCharsets.UTF_8))
            }

            if (responseCode != HttpURLConnection.HTTP_OK) {
                error("HTTP POST request failed with response code: $responseCode")
            }
            inputStream.bufferedReader().use { it.readText() }
        }
        val message = result
            .substringAfter("<main>")
            .substringBefore("</main>")
            .removePrefix("\n<article><p>")
            .removeSuffix("</p></article>\n")
            .takeIf { it.isNotBlank() } ?: result
        println(message)
    }

    fun hasSession() = session != null

    private fun validateSession() {
        if (!hasSession()) error("AOC_SESSION env variable is not defined")
    }

    private fun URL.connect(): URLConnection = if (proxy != null) openConnection(proxy) else openConnection()

    private fun createProxyOrNull(): Proxy? {
        val proxy = if (proxyHost != null && proxyPort != null) Proxy(
            Proxy.Type.HTTP,
            InetSocketAddress(proxyHost, proxyPort)
        ) else null
        if (proxyUser != null && proxyPassword != null) {
            Authenticator.setDefault(object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(proxyUser, proxyPassword)
                }
            })
        }
        return proxy
    }
}

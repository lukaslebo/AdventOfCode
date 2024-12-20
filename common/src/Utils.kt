import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.io.File
import java.math.BigInteger
import java.net.*
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(year: String, name: String): List<String> = File("year$year/input", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

fun <T> check(actual: T, expected: T) {
    kotlin.check(actual == expected) { "Got $actual but expected $expected." }
}

fun Iterable<Long>.lcm(): Long = reduce(::lcm)
fun Iterable<Long>.gcd(): Long = reduce(::gcd)

/** Least common multiple */
fun lcm(a: Long, b: Long): Long {
    return a / gcd(a, b) * b
}

/** Greatest common divisor */
fun gcd(a: Long, b: Long): Long {
    if (b == 0L) return a
    return gcd(b, a % b)
}

fun List<String>.splitByEmptyLines(): List<List<String>> =
    joinToString("\n")
        .split("\n\n")
        .map { it.split("\n") }

fun <T, R> Iterable<T>.parallelMap(block: (T) -> R): List<R> {
    return runBlocking(Dispatchers.Default) {
        map { async { block(it) } }.awaitAll()
    }
}

fun requestEndpoint(url: String, session: String): String {
    val requestUrl = URI(url).toURL()

    val proxyHost = System.getenv()["PROXY_HOST"]
    val proxyPort = System.getenv()["PROXY_PORT"]?.toIntOrNull()
    val proxyUser = System.getenv()["PROXY_USER"]
    val proxyPassword = System.getenv()["PROXY_PASSWORD"]?.toCharArray()

    val proxy = if (proxyHost != null && proxyPort != null) Proxy(Proxy.Type.HTTP, InetSocketAddress(proxyHost, proxyPort)) else null
    if (proxyUser != null && proxyPassword != null) {
        Authenticator.setDefault(object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(proxyUser, proxyPassword)
            }
        })
    }

    val connection = if (proxy != null) requestUrl.openConnection(proxy) else requestUrl.openConnection()
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

fun Array<String>.readProgramParams() = associate {
    val parameter = it.removePrefix("--")
    val name = parameter.substringBefore("=")
    val value = parameter.substringAfter("=")
    name to value
}

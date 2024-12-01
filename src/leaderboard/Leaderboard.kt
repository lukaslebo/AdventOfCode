package leaderboard

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URI
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val showDuration = true

/**
 * Run with args: --session=abc --leaderboardUrl=https://adventofcode.com/2023/leaderboard/private/view/123.json
 * Your AoC Session and the leaderboard URL are required.
 */
fun main(args: Array<String>) {
    val (session, leaderboardUrl) = args.readSessionAndUrl()
    val leaderboardJson = requestLeaderBoard(leaderboardUrl, session)
    val leaderboard = Json.decodeFromString<Leaderboard>(leaderboardJson)
    leaderboard.members.values.filter { it.localScore > 0 }.sortedByDescending { it.localScore }.forEach { member ->
        val year = leaderboard.event.toInt()
        print("\n${member.name ?: "Anonymous ${member.id}"}")
        member.starsByDay.entries.sortedBy { it.key }.forEach { (day, stars) ->
            val (star1, star2) = stars
            fun Star.time() = if (showDuration) tms.duration(year, day) else tms.formatted()
            print("\nDay $day: Star 1 ${star1.time()}")
            if (star2 != null) {
                print(" | Star 2 ${star2.time()}")
            }
        }
    }

    val owner = leaderboard.members[leaderboard.ownerId]
    println("\n\nFor Leaderboard ${owner?.name} with ${leaderboard.members.size} members")
}

@Serializable
private data class Leaderboard(
    val event: String,
    @SerialName("owner_id")
    val ownerId: Long,
    val members: Map<Long, Member>,
)

@Serializable
private data class Member(
    val id: Long,
    val name: String?,
    val stars: Int,
    @SerialName("local_score")
    val localScore: Int,
    @SerialName("global_score")
    val globalScore: Int,
    @SerialName("last_star_ts")
    @Serializable(with = LocalDateTimeSerializer::class)
    val lastStarTs: LocalDateTime,
    @SerialName("completion_day_level")
    val starsByDay: Map<Int, Stars>,
)

@Serializable
private data class Stars(
    @SerialName("1")
    val star1: Star,
    @SerialName("2")
    val star2: Star? = null,
)

@Serializable
private data class Star(
    @SerialName("star_index")
    val index: Int,
    @SerialName("get_star_ts")
    @Serializable(with = LocalDateTimeSerializer::class)
    val tms: LocalDateTime,
)

private object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeLong(value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val timestamp = decoder.decodeLong() * 1000
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
    }
}

private val format = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
private fun LocalDateTime.formatted() = format(format)
private fun LocalDateTime.duration(year: Int, day: Int): String {
    val start = LocalDateTime.of(year, 12, day, 6, 0, 0)
    val duration = Duration.between(start, this)
    return buildString {
        val hours = duration.toHours().toString().padStart(2, '0')
        val minutes = duration.toMinutesPart().toString().padStart(2, '0')
        val seconds = duration.toSecondsPart().toString().padStart(2, '0')
        append("$hours:$minutes:$seconds")
    }
}

private fun requestLeaderBoard(url: String, session: String): String {
    val requestUrl = URI(url).toURL()
    with(requestUrl.openConnection() as HttpURLConnection) {
        requestMethod = "GET"

        // Adding a custom header
        addRequestProperty("Cookie", "session=$session")
        // Replace "Authorization" with your desired header name and provide the appropriate value

        val responseCode = responseCode
        if (responseCode != HttpURLConnection.HTTP_OK) {
            error("HTTP GET request failed with response code: $responseCode")
        }

        return inputStream.bufferedReader().use { it.readText() }
    }
}

private fun Array<String>.readSessionAndUrl(): Pair<String, String> {
    val paramsByName = associate {
        val parameter = it.removePrefix("--")
        val name = parameter.substringBefore("=")
        val value = parameter.substringAfter("=").takeIf { "=" in parameter }
        name to value
    }
    return Pair(
        paramsByName["session"] ?: error("session is missing"),
        paramsByName["leaderboardUrl"] ?: error("leaderboardUrl is missing"),
    )
}

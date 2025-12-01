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
import readProgramParams
import requestEndpoint
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

private const val printStarsByUser = true
private const val printRanksPerStar = true

/**
 * Run with args: --session=abc --leaderboardUrl=https://adventofcode.com/2023/leaderboard/private/view/123.json
 * Your AoC Session and the leaderboard URL are required.
 */
fun main(args: Array<String>) {
    val (session, leaderboardUrl) = args.readProgramParams().let {
        it.getValue("session") to it.getValue("leaderboardUrl")
    }

    val leaderboardJson = requestEndpoint(leaderboardUrl, session)
    val leaderboard = Json.decodeFromString<Leaderboard>(leaderboardJson)
    if (printStarsByUser) leaderboard.printStarDurationsPerMember()
    if (printRanksPerStar) leaderboard.printRanksPerStar()

    val owner = leaderboard.members[leaderboard.ownerId]
    println("\n\nFor Leaderboard ${owner?.name} with ${leaderboard.members.size} members")
}

private fun Leaderboard.printStarDurationsPerMember() {
    members.values.filter { it.localScore > 0 }.sortedByDescending { it.localScore }.forEach { member ->
        val year = event.toInt()
        print("\n${member.nameOrId}")
        member.starsByDay.entries.sortedBy { it.key }.forEach { (day, stars) ->
            val (star1, star2) = stars
            print("\nDay $day: Star 1 ${star1.tms.duration(year, day)}")
            if (star2 != null) {
                print(" | Star 2 ${star2.tms.duration(year, day)}")
            }
        }
    }
}

private fun Leaderboard.printRanksPerStar() {
    val starDetailsList = members.values.flatMap { member ->
        member.starsByDay.entries.flatMap { (day, star) ->
            val start = LocalDateTime.of(event.toInt(), 12, day, 6, 0, 0)
            listOfNotNull(
                StarDetails(
                    name = member.nameOrId,
                    day = day,
                    star = 1,
                    duration = Duration.between(start, star.star1.tms),
                ),
                if (star.star2 != null) StarDetails(
                    name = member.nameOrId,
                    day = day,
                    star = 2,
                    duration = Duration.between(start, star.star2.tms),
                ) else null,
            )
        }
    }.sortedBy { it.duration }

    println("\n\n")
    val sortedGroups =
        starDetailsList.groupBy { it.day to it.star }.entries.sortedBy { it.key.first * 10 + it.key.second }
    val maxNameLength = starDetailsList.maxOf { it.name.length }
    for ((key, list) in sortedGroups) {
        val (day, star) = key
        println("-".repeat(50))
        println("Day $day Star $star")
        list.take(10).forEachIndexed { i, it ->
            val rank = "${i + 1}".padEnd(list.size.toString().length, ' ')
            val paddedName = it.name.padEnd(maxNameLength, ' ')
            println("$rank $paddedName ${it.durationText}")
        }
    }
    println("-".repeat(50))
}

private data class StarDetails(
    val name: String,
    val day: Int,
    val star: Int,
    val duration: Duration,
) {
    val durationText: String
        get() = duration.format()
}

@Serializable
private data class Leaderboard(
    val event: String,
    @SerialName("owner_id")
    val ownerId: Long,
    @SerialName("num_days")
    val numberOfDays: Int,
    val members: Map<Long, Member>,
    @SerialName("day1_ts")
    @Serializable(with = LocalDateTimeSerializer::class)
    val day1Ts: LocalDateTime,
)

@Serializable
private data class Member(
    val id: Long,
    val name: String?,
    val stars: Int,
    @SerialName("local_score")
    val localScore: Int,
    @SerialName("global_score")
    val globalScore: Int? = null,
    @SerialName("last_star_ts")
    @Serializable(with = LocalDateTimeSerializer::class)
    val lastStarTs: LocalDateTime,
    @SerialName("completion_day_level")
    val starsByDay: Map<Int, Stars>,
) {
    val nameOrId: String
        get() = name ?: "Anonymous $id"
}

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

private fun LocalDateTime.duration(year: Int, day: Int): String {
    val start = LocalDateTime.of(year, 12, day, 6, 0, 0)
    return Duration.between(start, this).format()
}

private fun Duration.format() = buildString {
    val hours = toHours().toString().padStart(2, '0')
    val minutes = toMinutesPart().toString().padStart(2, '0')
    val seconds = toSecondsPart().toString().padStart(2, '0')
    append("$hours:$minutes:$seconds")
}

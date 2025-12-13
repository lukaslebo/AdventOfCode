package util

fun List<String>.splitByEmptyLines(): List<List<String>> =
    joinToString("\n")
        .split("\n\n")
        .map { it.split("\n") }

package util

fun <T> List<List<T>>.transpose() = this[0].indices.map { i -> indices.map { j -> this[j][i] } }
fun <T> List<List<T>>.rotate90() = transpose().map { it.reversed() }
fun <T> List<List<T>>.rotate270() = map { it.reversed() }.transpose()
fun <T> List<List<T>>.rotate180() = map { it.reversed() }.reversed()
fun <T> List<List<T>>.mirrorLeftRight() = map { it.reversed() }
fun <T> List<List<T>>.mirrorTopDown() = reversed()

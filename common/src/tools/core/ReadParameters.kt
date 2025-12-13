package tools.core

fun Array<String>.readParameters() = associate {
    val parameter = it.removePrefix("--")
    val name = parameter.substringBefore("=")
    val value = parameter.substringAfter("=")
    name to value
}

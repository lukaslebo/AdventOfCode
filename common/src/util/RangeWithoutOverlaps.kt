package util

fun List<LongRange>.withoutOverlaps(): List<LongRange> {
    if (isEmpty()) return emptyList()

    val sorted = sortedBy { it.first }

    val merged = mutableListOf<LongRange>()
    var current = sorted.first()

    for (next in sorted.drop(1)) {
        if (next.first <= current.last + 1) {
            current = current.first..maxOf(current.last, next.last)
        } else {
            merged += current
            current = next
        }
    }
    merged += current
    return merged
}

fun LongRange.size(): Long = last - first + 1

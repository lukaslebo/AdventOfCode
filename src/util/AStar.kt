package util

import java.util.PriorityQueue

data class Node<T>(
    val parent: Node<T>?,
    val value: T,
    val cost: Int,
    val heuristic: Int,
) {
    fun path(): List<T> = (parent?.path() ?: emptyList()) + value
}

fun <T> aStar(
    from: T,
    goal: (T) -> Boolean,
    neighboursWithCost: T.() -> Set<Pair<T, Int>>,
    heuristic: (T) -> Int = { 0 },
): Node<T>? {
    val visited = mutableSetOf<T>()
    val queue = PriorityQueue(compareBy<Node<T>> { it.cost + it.heuristic })
    queue += Node(null, from, 0, heuristic(from))

    while (queue.isNotEmpty()) {
        val current = queue.poll()

        if (goal(current.value)) return current

        for ((next, cost) in current.value.neighboursWithCost()) {
            if (next in visited) continue
            visited += next

            queue += Node(current, next, current.cost + cost, heuristic(next))
        }
    }
    return null
}

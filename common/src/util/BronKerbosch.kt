package util

/**
 * Bron-Kerbosch is a Algorithm to find all maximal cliques in a graph.
 *
 * BronKerbosch1(R, P, X):
 * if P and X are both empty:
 *     report R as a maximal clique
 * for each vertex v in P:
 *     BronKerbosch1(R ⋃ {v}, P ⋂ N(v), X ⋂ N(v))
 *     P := P \ {v}
 *     X := X ⋃ {v}
 */
fun <T> Map<T, Set<T>>.bronKerbosch(
    potentialVertices: Set<T>,
    excludedVertices: Set<T> = emptySet(),
    currentClique: Set<T> = emptySet(),
): Set<Set<T>> {
    if (potentialVertices.isEmpty() && excludedVertices.isEmpty()) return setOf(currentClique)

    val maximalCliques = mutableSetOf<Set<T>>()

    val pivot = (potentialVertices + excludedVertices).first()
    val pivotNeighbors = get(pivot) ?: emptySet()

    val candidates = potentialVertices.toMutableSet()
    val excluded = excludedVertices.toMutableSet()

    for (vertex in potentialVertices - pivotNeighbors) {
        val neighbors = get(vertex) ?: emptySet()

        maximalCliques += bronKerbosch(
            potentialVertices = candidates.intersect(neighbors),
            excludedVertices = excluded.intersect(neighbors),
            currentClique = currentClique + vertex,
        )

        candidates -= vertex
        excluded += vertex
    }

    return maximalCliques
}

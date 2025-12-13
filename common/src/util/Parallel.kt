package util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

fun <T, R> Iterable<T>.parallelMap(block: (T) -> R): List<R> {
    return runBlocking(Dispatchers.Default) {
        map { async { block(it) } }.awaitAll()
    }
}

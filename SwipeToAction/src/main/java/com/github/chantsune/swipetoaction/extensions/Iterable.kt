package com.github.chantsune.swipetoaction.extensions

fun <T, R, V> Iterable<T>.zipLongest(other: Iterable<R>, transform: (a: T?, b: R?) -> V): List<V> {
    val first = iterator()
    val second = other.iterator()
    val list = mutableListOf<V>()
    while (first.hasNext() || second.hasNext()) {
        list.add(transform(first.nextOrNull(), second.nextOrNull()))
    }
    return list
}

fun <T, R> Iterable<T>.zipLongest(other: Iterable<R>): List<Pair<T?, R?>> {
    return zipLongest(other) { a, b -> a to b }
}

@JvmName("zipLongestTR")
fun <T, R, V> Iterable<Pair<T, R>>.zipLongest(other: Iterable<V>): List<Triple<T?, R?, V?>> {
    return zipLongest(other) { p, v ->
        Triple(p?.first, p?.second, v)
    }
}

fun <T, R, V> Iterable<Pair<T, R>>.zip(other: Iterable<V>): List<Triple<T, R, V>> {
    return zip(other) { p, v ->
        val (t, r) = p
        Triple(t, r, v)
    }
}

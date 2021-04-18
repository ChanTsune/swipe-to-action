package com.github.chantsune.swipetoaction.ktx

internal inline fun <T, R, V> Iterable<T>.zipLongest(other: Iterable<R>, transform: (a: T?, b: R?) -> V): List<V> {
    val first = iterator()
    val second = other.iterator()
    val list = mutableListOf<V>()
    while (first.hasNext() || second.hasNext()) {
        list.add(transform(first.nextOrNull(), second.nextOrNull()))
    }
    return list
}

internal fun <T, R> Iterable<T>.zipLongest(other: Iterable<R>): List<Pair<T?, R?>> {
    return zipLongest(other) { a, b -> a to b }
}

@JvmName("zipLongestTR")
internal fun <T, R, V> Iterable<Pair<T, R>>.zipLongest(other: Iterable<V>): List<Triple<T?, R?, V?>> {
    return zipLongest(other) { p, v ->
        Triple(p?.first, p?.second, v)
    }
}

internal inline fun <T, R, V> Iterable<T>.zipLongest(other: Array<R>, transform: (a: T?, b: R?) -> V): List<V> {
    val first = iterator()
    val second = other.iterator()
    val list = mutableListOf<V>()
    while (first.hasNext() || second.hasNext()) {
        list.add(transform(first.nextOrNull(), second.nextOrNull()))
    }
    return list
}

internal fun <T, R> Iterable<T>.zipLongest(other: Array<R>): List<Pair<T?, R?>> {
    return zipLongest(other) { a, b -> a to b }
}

@JvmName("zipLongestTR")
internal fun <T, R, V> Iterable<Pair<T, R>>.zipLongest(other: Array<V>): List<Triple<T?, R?, V?>> {
    return zipLongest(other) { p, v ->
        Triple(p?.first, p?.second, v)
    }
}

internal inline fun <T, V> Iterable<T>.zipLongest(other: IntArray, transform: (a: T?, b: Int?) -> V): List<V> {
    val first = iterator()
    val second = other.iterator()
    val list = mutableListOf<V>()
    while (first.hasNext() || second.hasNext()) {
        list.add(transform(first.nextOrNull(), second.nextOrNull()))
    }
    return list
}

internal fun <T> Iterable<T>.zipLongest(other: IntArray): List<Pair<T?, Int?>> {
    return zipLongest(other) { a, b -> a to b }
}

@JvmName("zipLongestTR")
internal fun <T, R> Iterable<Pair<T, R>>.zipLongest(other: IntArray): List<Triple<T?, R?, Int?>> {
    return zipLongest(other) { p, v ->
        Triple(p?.first, p?.second, v)
    }
}

package com.github.chantsune.swipetoaction.ktx

internal inline fun <R, V> IntArray.zipLongest(other: Array<R>, transform: (a: Int?, b: R?) -> V): List<V> {
    val first = iterator()
    val second = other.iterator()
    val list = mutableListOf<V>()
    while (first.hasNext() || second.hasNext()) {
        list.add(transform(first.nextOrNull(), second.nextOrNull()))
    }
    return list
}

internal fun <R> IntArray.zipLongest(other: Array<R>): List<Pair<Int?, R?>> {
    return zipLongest(other) { a, b -> a to b }
}

internal inline fun <R, V> IntArray.zipLongest(other: Iterable<R>, transform: (a: Int?, b: R?) -> V): List<V> {
    val first = iterator()
    val second = other.iterator()
    val list = mutableListOf<V>()
    while (first.hasNext() || second.hasNext()) {
        list.add(transform(first.nextOrNull(), second.nextOrNull()))
    }
    return list
}

internal fun <R> IntArray.zipLongest(other: Iterable<R>): List<Pair<Int?, R?>> {
    return zipLongest(other) { a, b -> a to b }
}

internal inline fun <V> IntArray.zipLongest(other: IntArray, transform: (a: Int?, b: Int?) -> V): List<V> {
    val first = iterator()
    val second = other.iterator()
    val list = mutableListOf<V>()
    while (first.hasNext() || second.hasNext()) {
        list.add(transform(first.nextOrNull(), second.nextOrNull()))
    }
    return list
}

internal fun IntArray.zipLongest(other: IntArray): List<Pair<Int?, Int?>> {
    return zipLongest(other) { a, b -> a to b }
}

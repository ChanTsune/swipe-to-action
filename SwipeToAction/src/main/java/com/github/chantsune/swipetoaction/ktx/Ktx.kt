package com.github.chantsune.swipetoaction.ktx

fun <R> runCatchingAsNull(block: () -> R): R? {
    return try {
        block()
    } catch (e: Throwable) {
        null
    }
}

package com.github.chantsune.swipetoaction.ktx

import android.content.res.TypedArray
import androidx.annotation.StyleableRes
import androidx.core.content.res.getResourceIdOrThrow

internal fun TypedArray.getResourceIdOrNull(@StyleableRes index: Int): Int? {
    return runCatchingAsNull {
        getResourceIdOrThrow(index)
    }
}

internal fun TypedArray.getIntArrayOrNull(@StyleableRes index: Int): IntArray? {
    return getResourceIdOrNull(index)?.let {
        resources.getIntArray(it)
    }
}

internal fun TypedArray.getStringArrayOrNull(@StyleableRes index: Int): Array<String>? {
    return getResourceIdOrNull(index)?.let {
        resources.getStringArray(it)
    }
}

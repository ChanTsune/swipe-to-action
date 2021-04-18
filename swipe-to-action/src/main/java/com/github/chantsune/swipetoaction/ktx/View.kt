package com.github.chantsune.swipetoaction.ktx

import android.view.View
import android.widget.LinearLayout

internal var View.viewWeight: Float
    get() = (layoutParams as LinearLayout.LayoutParams).weight
    set(value) {
        layoutParams = (layoutParams as LinearLayout.LayoutParams).apply {
            weight = value
        }
    }

internal var View.viewWidth: Int
    get() = layoutParams.width
    set(value) {
        layoutParams.width = value
        requestLayout()
    }

internal var View.viewHeight: Int
    get() = layoutParams.height
    set(value) {
        layoutParams.height = value
        requestLayout()
    }

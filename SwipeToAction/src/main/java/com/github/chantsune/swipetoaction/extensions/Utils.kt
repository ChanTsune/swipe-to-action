package com.github.chantsune.swipetoaction.extensions

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.LinearLayout
import androidx.core.graphics.drawable.DrawableCompat

var View.viewWeight: Float
    get() = (layoutParams as LinearLayout.LayoutParams).weight
    set(value) {
        layoutParams = (layoutParams as LinearLayout.LayoutParams).apply {
            weight = value
        }
    }

object Utils {
    @JvmStatic
    fun setViewWidth(view: View, width: Int) {
        view.layoutParams.width = width
        view.requestLayout()
    }

    fun setViewHeight(view: View, height: Int) {
        view.layoutParams.height = height
        view.requestLayout()
    }

    @JvmStatic
    fun setTint(drawable: Drawable, color: Int): Drawable {
        var drawable = drawable
        drawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(drawable, color)
        return drawable.mutate()
    }
}

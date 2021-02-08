package com.github.chantsune.swipetoaction.extensions

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.LinearLayout
import androidx.core.graphics.drawable.DrawableCompat

object Utils {
    @JvmStatic
    fun getViewWeight(view: View): Float {
        val lp = view.layoutParams as LinearLayout.LayoutParams
        return lp.weight
    }

    @JvmStatic
    fun setViewWeight(view: View, weight: Float) {
        val lp = view.layoutParams as LinearLayout.LayoutParams
        lp.weight = weight
        view.layoutParams = lp
    }

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

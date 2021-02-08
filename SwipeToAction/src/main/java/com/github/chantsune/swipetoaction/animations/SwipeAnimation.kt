package com.github.chantsune.swipetoaction.animations

import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import androidx.core.view.ViewCompat
import com.github.chantsune.swipetoaction.extensions.Utils

class SwipeAnimation(
    private val resizeView: View,
    private val width: Int,
    private val changeXView: View,
    private val left: Boolean
) : Animation() {
    private var startWidth = -1

    init {
        duration = 300
        interpolator = DecelerateInterpolator()
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        if (startWidth < 0) {
            startWidth = resizeView.width
        }
        Utils.setViewWidth(
            resizeView,
            startWidth + ((width.toFloat() - startWidth.toFloat()) * interpolatedTime).toInt()
        )
        if (left) {
            ViewCompat.setTranslationX(changeXView, resizeView.width.toFloat())
        } else {
            ViewCompat.setTranslationX(changeXView, -resizeView.width.toFloat())
        }
    }

    override fun willChangeBounds(): Boolean = true
}

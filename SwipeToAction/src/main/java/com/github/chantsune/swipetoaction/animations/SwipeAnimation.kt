package com.github.chantsune.swipetoaction.animations

import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import com.github.chantsune.swipetoaction.extensions.viewWidth

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
        resizeView.viewWidth = startWidth + ((width - startWidth) * interpolatedTime).toInt()

        if (left) {
            changeXView.translationX = resizeView.width.toFloat()
        } else {
            changeXView.translationX = -resizeView.width.toFloat()
        }
    }

    override fun willChangeBounds(): Boolean = true
}

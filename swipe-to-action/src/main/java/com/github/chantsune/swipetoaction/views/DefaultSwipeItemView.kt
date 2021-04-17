package com.github.chantsune.swipetoaction.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.use

internal class DefaultSwipeItemView(
    context: Context,
    icon: Int,
    iconColor: Int?,
    backgroundColor: Int,
    text: String?,
    textColor: Int?,
    left: Boolean,
    // internal params
    itemWidth: Int,
    iconSize: Int,
    textSize: Float,
    listener: OnTouchListener
) : FrameLayout(context) {

    init {
        var id = 0
        foreground = rippleDrawable
        val imageView = ImageView(context).also { imageView ->
            imageView.setImageDrawable(
                ContextCompat.getDrawable(context, icon)?.also { drawable ->
                    if (iconColor != null) {
                        drawable.setTint(iconColor)
                    }
                })
            imageView.id = ++id
        }
        val textView = text?.let { text ->
            TextView(context).also { textView ->
                textView.maxLines = 2
                if (textSize > 0) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                }
                if (textColor != null) {
                    textView.setTextColor(textColor)
                }
                textView.text = text
                textView.gravity = Gravity.CENTER
                textView.id = ++id
            }
        }
        addView(
            ConstraintLayout(context).also { constraintLayout ->
                constraintLayout.addView(
                    imageView,
                    ConstraintLayout.LayoutParams(iconSize, iconSize).also { params ->
                        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        if (textView != null) {
                            params.bottomToTop = textView.id
                        } else {
                            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                        }
                    }
                )
                textView?.let { textView ->
                    constraintLayout.addView(
                        textView,
                        ConstraintLayout.LayoutParams(
                            ConstraintLayout.LayoutParams.MATCH_PARENT,
                            ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ).also { params ->
                            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                            params.topToBottom = imageView.id
                            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                        }
                    )
                }
            },
            LayoutParams(
                itemWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL or if (left) Gravity.END else Gravity.START
            )
        )
        if (backgroundColor != SwipeLayout.NO_ID) {
            setBackgroundColor(backgroundColor)
        }
        layoutParams =
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
        setOnTouchListener(listener)
    }

    private val rippleDrawable: Drawable?
        get() {
            val attrs = intArrayOf(android.R.attr.selectableItemBackground)
            return context.obtainStyledAttributes(attrs).use {
                it.getDrawable(0)
            }
        }
}

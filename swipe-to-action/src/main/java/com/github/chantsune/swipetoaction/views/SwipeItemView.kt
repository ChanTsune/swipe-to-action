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

internal class SwipeItemView(
    context: Context,
    swipeItem: SimpleSwipeLayout.SwipeItem,
) : FrameLayout(context) {

    private var imageView: ImageView
    private var textView: TextView?

    init {
        var id = 0
        foreground = rippleDrawable
        imageView = ImageView(context).also { imageView ->
            imageView.setImageDrawable(
                ContextCompat.getDrawable(context, swipeItem.icon)?.also { drawable ->
                    if (swipeItem.iconColor != null) {
                        drawable.setTint(swipeItem.iconColor)
                    }
                })
            imageView.id = ++id
        }
        textView = swipeItem.text?.let { text ->
            TextView(context).also { textView ->
                textView.maxLines = 2
                if (swipeItem.textSize > 0) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, swipeItem.textSize)
                }
                if (swipeItem.textColor != null) {
                    textView.setTextColor(swipeItem.textColor)
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
                    ConstraintLayout.LayoutParams(swipeItem.iconSize, swipeItem.iconSize).also { params ->
                        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        textView?.let { textView ->
                            params.bottomToTop = textView.id
                        } ?: kotlin.run {
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
                swipeItem.itemWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL or if (swipeItem.left) Gravity.END else Gravity.START
            )
        )
        if (swipeItem.backgroundColor != SwipeLayout.NO_ID) {
            setBackgroundColor(swipeItem.backgroundColor)
        }
        layoutParams =
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
    }

    private val rippleDrawable: Drawable?
        get() {
            val attrs = intArrayOf(android.R.attr.selectableItemBackground)
            return context.obtainStyledAttributes(attrs).use {
                it.getDrawable(0)
            }
        }
}

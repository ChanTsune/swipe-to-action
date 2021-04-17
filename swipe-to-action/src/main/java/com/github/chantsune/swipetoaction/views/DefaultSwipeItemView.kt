package com.github.chantsune.swipetoaction.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
    textTopMargin: Int,
    listener: OnTouchListener
) : FrameLayout(context) {

    init {
        var _id = 0
        foreground = rippleDrawable
        addView(
            RelativeLayout(context).also { relativeLayout ->
                relativeLayout.addView(
                    ImageView(context).also { imageView ->
                        imageView.setImageDrawable(
                            ContextCompat.getDrawable(context, icon)?.also { drawable ->
                                if (iconColor != null) {
                                    drawable.setTint(iconColor)
                                }
                            })
                        imageView.id = ++_id
                    },
                    RelativeLayout.LayoutParams(iconSize, iconSize).also { params ->
                        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
                    }
                )
                if (text != null) {
                    relativeLayout.addView(
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
                        },
                        RelativeLayout.LayoutParams(itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
                            .also { params ->
                                params.addRule(RelativeLayout.BELOW, _id)
                                params.topMargin = textTopMargin
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

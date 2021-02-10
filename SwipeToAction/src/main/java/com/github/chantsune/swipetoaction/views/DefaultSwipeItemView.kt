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

class DefaultSwipeItemView(
    context: Context,
    icon: Int,
    iconColor: Int,
    backgroundColor: Int,
    text: String?,
    textColor: Int,
    left: Boolean,
    // internal params
    itemWidth: Int,
    iconSize: Int,
    textSize: Float,
    textTopMargin: Int,
    listener: OnTouchListener
) : FrameLayout(context) {

    private var _id = 0

    init {
        also { frameLayout ->
            frameLayout.addView(View(context).also { view ->
                view.layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                view.background = rippleDrawable
            })
            frameLayout.addView(RelativeLayout(context).also { relativeLayout ->
                val gravity = if (left) {
                    Gravity.CENTER_VERTICAL or Gravity.END
                } else {
                    Gravity.CENTER_VERTICAL or Gravity.START
                }
                relativeLayout.layoutParams =
                    LayoutParams(itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT, gravity)
                relativeLayout.addView(ImageView(context).also { imageView ->
                    imageView.setImageDrawable(
                        ContextCompat.getDrawable(context, icon)?.also { drawable ->
                            if (iconColor != SwipeLayout.NO_ID) {
                                drawable.setTint(iconColor)
                            }
                        })
                    imageView.layoutParams =
                        RelativeLayout.LayoutParams(iconSize, iconSize).also { params ->
                            params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
                        }
                    imageView.id = ++_id
                })
                if (text != null) {
                    relativeLayout.addView(
                        TextView(context).also { textView ->
                            textView.maxLines = 2
                            if (textSize > 0) {
                                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                            }
                            if (textColor != SwipeLayout.NO_ID) {
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
            })
            if (backgroundColor != SwipeLayout.NO_ID) {
                frameLayout.setBackgroundColor(backgroundColor)
            }
            frameLayout.layoutParams =
                LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
            frameLayout.setOnTouchListener(listener)
        }
    }

    private val rippleDrawable: Drawable?
        get() {
            val attrs = intArrayOf(android.R.attr.selectableItemBackground)
            return context.obtainStyledAttributes(attrs).use {
                it.getDrawable(0)
            }
        }
}

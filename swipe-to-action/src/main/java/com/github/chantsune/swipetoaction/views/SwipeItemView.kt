package com.github.chantsune.swipetoaction.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.use

internal class SwipeItemView(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {

    internal var imageView: ImageView = ImageView(context).also { view ->
        view.id = android.R.id.icon1
    }
    internal var textView: TextView = TextView(context).also { view ->
        view.id = android.R.id.text1
    }
    private var containerView: ConstraintLayout = ConstraintLayout(context)

    init {
        foreground = rippleDrawable
    }

    private fun getImageViewInternal(swipeItem: SwipeLayout.SwipeItem): ImageView? =
        swipeItem.icon?.let { icon ->
            imageView.also { imageView ->
                imageView.setImageDrawable(icon)
            }
        }

    private fun getTextViewInternal(swipeItem: SwipeLayout.SwipeItem): TextView? =
        swipeItem.text?.let { text ->
            textView.also { textView ->
                textView.maxLines = 2
                textView.text = text
                textView.gravity = Gravity.CENTER
            }
        }

    fun update(swipeItem: SwipeLayout.SwipeItem) {
        removeAllViews() // NOTE: clear views
        val imageView = getImageViewInternal(swipeItem)
        val textView = getTextViewInternal(swipeItem)
        addView(
            containerView.also { constraintLayout ->
                constraintLayout.removeAllViews() // NOTE: clear views
                imageView?.let { imageView ->
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
                }
                textView?.let { textView ->
                    constraintLayout.addView(
                        textView,
                        ConstraintLayout.LayoutParams(
                            ConstraintLayout.LayoutParams.MATCH_PARENT,
                            ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ).also { params ->
                            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                            imageView?.let { imageView ->
                                params.topToBottom = imageView.id
                            } ?: kotlin.run {
                                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                            }
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
        swipeItem.backgroundColor?.let { backgroundColor ->
            setBackgroundColor(backgroundColor)
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

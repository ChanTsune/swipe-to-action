package com.github.chantsune.swipetoaction.views

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import com.github.chantsune.swipetoaction.R
import com.github.chantsune.swipetoaction.ktx.getIntArrayOrNull
import com.github.chantsune.swipetoaction.ktx.getResourceIdOrNull
import com.github.chantsune.swipetoaction.ktx.getStringArrayOrNull
import com.github.chantsune.swipetoaction.ktx.zipLongest

open class SimpleSwipeLayout(c: Context, attrs: AttributeSet? = null) : SwipeLayout(c, attrs) {
    var leftColors: List<Int> = listOf()
    var leftIcons: List<Int> = listOf()
    var leftIconColors: List<Int> = listOf()
    var leftTextColors: List<Int> = listOf()

    var rightColors: List<Int> = listOf()
    var rightIcons: List<Int> = listOf()
    var rightIconColors: List<Int> = listOf()
    var rightTextColors: List<Int> = listOf()

    var leftTexts: List<String> = listOf()
    var rightTexts: List<String> = listOf()

    private var iconSize = 0
    private var textSize = 0f
    private var textTopMargin = 0


    override fun setUpAttrs(attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.SimpleSwipeLayout).use { array ->

            contentLayoutId =
                array.getResourceId(R.styleable.SimpleSwipeLayout_foregroundLayout, NO_ID)
            itemWidth =
                array.getDimensionPixelSize(
                    R.styleable.SimpleSwipeLayout_layout_swipeAutoHideSwipe,
                    100
                )
            canFullSwipeFromRight =
                array.getBoolean(
                    R.styleable.SimpleSwipeLayout_layout_swipeCanFullSwipeRightToLeft,
                    false
                )
            canFullSwipeFromLeft =
                array.getBoolean(
                    R.styleable.SimpleSwipeLayout_layout_swipeCanFullSwipeLeftToRight,
                    false
                )
            onlyOneSwipe =
                array.getBoolean(R.styleable.SimpleSwipeLayout_layout_swipeOnlyOneSwipe, true)
            autoHideSwipe =
                array.getBoolean(R.styleable.SimpleSwipeLayout_layout_swipeAutoHideSwipe, true)

            iconSize = array.getDimensionPixelSize(
                R.styleable.SimpleSwipeLayout_layout_swipeIconSize,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            textSize =
                array.getDimensionPixelSize(
                    R.styleable.SimpleSwipeLayout_layout_swipeTextSize,
                    NO_ID
                ).toFloat()
            textTopMargin =
                array.getDimensionPixelSize(
                    R.styleable.SimpleSwipeLayout_layout_swipeTextTopMargin,
                    20
                )
            val rightIconsRes =
                array.getResourceIdOrNull(R.styleable.SimpleSwipeLayout_layout_swipeRightItemIcons)
            val leftIconsRes =
                array.getResourceIdOrNull(R.styleable.SimpleSwipeLayout_layout_swipeLeftItemIcons)

            rightColors =
                array.getIntArrayOrNull(R.styleable.SimpleSwipeLayout_layout_swipeRightItemColors)
                    ?.toList()
                    ?: listOf()

            if (rightIconsRes != null && !isInEditMode)
                rightIcons = fillDrawables(resources.obtainTypedArray(rightIconsRes)).toList()

            leftColors =
                array.getIntArrayOrNull(R.styleable.SimpleSwipeLayout_layout_swipeLeftItemColors)
                    ?.toList()
                    ?: listOf()

            if (leftIconsRes != null && !isInEditMode)
                leftIcons = fillDrawables(resources.obtainTypedArray(leftIconsRes)).toList()

            leftTexts =
                array.getStringArrayOrNull(R.styleable.SimpleSwipeLayout_layout_swipeLeftStrings)
                    ?.toList()
                    ?: listOf()

            rightTexts =
                array.getStringArrayOrNull(R.styleable.SimpleSwipeLayout_layout_swipeRightStrings)
                    ?.toList()
                    ?: listOf()

            leftTextColors =
                array.getIntArrayOrNull(R.styleable.SimpleSwipeLayout_layout_swipeLeftTextColors)
                    ?.toList()
                    ?: listOf()

            rightTextColors =
                array.getIntArrayOrNull(R.styleable.SimpleSwipeLayout_layout_swipeRightTextColors)
                    ?.toList()
                    ?: listOf()

            leftIconColors =
                array.getIntArrayOrNull(R.styleable.SimpleSwipeLayout_layout_swipeLeftIconColors)
                    ?.toList()
                    ?: listOf()

            rightIconColors =
                array.getIntArrayOrNull(R.styleable.SimpleSwipeLayout_layout_swipeRightIconColors)
                    ?.toList()
                    ?: listOf()

            validateParams()
        }
    }

    override fun setUpView() {
        createItemLayouts()
        super.setUpView()
    }

    private fun fillDrawables(ta: TypedArray): IntArray {
        return ta.use {
            IntArray(ta.length()) { ta.getResourceId(it, NO_ID) }
        }
    }


    private fun validateParams() {
        compareArrays(leftColors, leftIcons)
        compareArrays(rightColors, rightIcons)
        compareArrays(leftIconColors, leftIcons)
        compareArrays(rightIconColors, rightIcons)
    }

    private fun compareArrays(arr1: List<Int>, arr2: List<Int>) {
        check(arr1.size >= arr2.size) { "Drawable array shouldn't be bigger than color array" }
    }

    private fun createItemLayouts() {
        createRightItemLayout()
        createLeftItemLayout()
    }

    private fun createRightItemLayout() {
        val views = createSwipeItems(
            rightIcons,
            rightIconColors,
            rightColors,
            rightTexts,
            rightTextColors,
            false
        )
        setRightSwipeItems(views)
    }

    private fun createLeftItemLayout() {
        val views = createSwipeItems(
            leftIcons,
            leftIconColors,
            leftColors,
            leftTexts,
            leftTextColors,
            true
        )
        setLeftSwipeItems(views)
    }

    private fun createSwipeItems(
        icons: List<Int>,
        iconColors: List<Int>,
        backgroundColors: List<Int>,
        texts: List<String>,
        textColors: List<Int>,
        left: Boolean
    ): List<View> {
        return icons
            .zipLongest(iconColors)
            .zipLongest(backgroundColors)
            .zipLongest(texts)
            .zipLongest(textColors)
            .map {
                val icon = it.first?.first ?: NO_ID
                val iconColor = it.first?.second
                val bgColor = it.first?.third ?: NO_ID
                val txt = it.second
                val txtColor = it.third
                createSwipeItem(
                    icon,
                    iconColor,
                    bgColor,
                    txt,
                    txtColor,
                    left
                )
            }
    }

    private fun createSwipeItem(
        icon: Int,
        iconColor: Int?,
        backgroundColor: Int,
        text: String?,
        textColor: Int?,
        left: Boolean
    ): ViewGroup {
        return DefaultSwipeItemView(
            context,
            icon,
            iconColor,
            backgroundColor,
            text,
            textColor,
            left,
            itemWidth,
            iconSize,
            textSize,
            textTopMargin,
            this
        )
    }
}

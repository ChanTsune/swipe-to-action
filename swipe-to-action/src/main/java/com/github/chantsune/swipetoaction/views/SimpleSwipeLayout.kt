package com.github.chantsune.swipetoaction.views

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.use
import com.github.chantsune.swipetoaction.R
import com.github.chantsune.swipetoaction.ktx.getIntArrayOrNull
import com.github.chantsune.swipetoaction.ktx.getResourceIdOrNull
import com.github.chantsune.swipetoaction.ktx.getStringArrayOrNull
import com.github.chantsune.swipetoaction.ktx.zipLongest

open class SimpleSwipeLayout(c: Context, attrs: AttributeSet? = null) : SwipeLayout(c, attrs) {
    var leftColors: IntArray = intArrayOf()
    var leftIcons: IntArray = intArrayOf()
    var leftIconColors: IntArray = intArrayOf()
    var leftTextColors: IntArray = intArrayOf()

    var rightColors: IntArray = intArrayOf()
    var rightIcons: IntArray = intArrayOf()
    var rightIconColors: IntArray = intArrayOf()
    var rightTextColors: IntArray = intArrayOf()

    var leftTexts: Array<String> = arrayOf()
    var rightTexts: Array<String> = arrayOf()

    private var iconSize = 0
    private var textSize = 0f


    override fun setUpAttrs(attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.SimpleSwipeLayout).use { array ->

            contentLayoutId =
                array.getResourceId(R.styleable.SimpleSwipeLayout_layout_swipeContentLayout, NO_ID)
            itemWidth =
                array.getDimensionPixelSize(
                    R.styleable.SimpleSwipeLayout_layout_swipeSwipeItemWidth,
                    100
                )
            canFullSwipeRightToLeft =
                array.getBoolean(
                    R.styleable.SimpleSwipeLayout_layout_swipeCanFullSwipeRightToLeft,
                    false
                )
            canFullSwipeLeftToRight =
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
            val rightIconsRes =
                array.getResourceIdOrNull(R.styleable.SimpleSwipeLayout_layout_swipeRightItemIcons)
            val leftIconsRes =
                array.getResourceIdOrNull(R.styleable.SimpleSwipeLayout_layout_swipeLeftItemIcons)

            rightColors =
                array.getIntArrayOrNull(R.styleable.SimpleSwipeLayout_layout_swipeRightItemColors)
                    ?: intArrayOf()

            if (rightIconsRes != null && !isInEditMode)
                rightIcons = fillDrawables(resources.obtainTypedArray(rightIconsRes))

            leftColors =
                array.getIntArrayOrNull(R.styleable.SimpleSwipeLayout_layout_swipeLeftItemColors)
                    ?: intArrayOf()

            if (leftIconsRes != null && !isInEditMode)
                leftIcons = fillDrawables(resources.obtainTypedArray(leftIconsRes))

            leftTexts =
                array.getStringArrayOrNull(R.styleable.SimpleSwipeLayout_layout_swipeLeftStrings)
                    ?: arrayOf()

            rightTexts =
                array.getStringArrayOrNull(R.styleable.SimpleSwipeLayout_layout_swipeRightStrings)
                    ?: arrayOf()

            leftTextColors =
                array.getIntArrayOrNull(R.styleable.SimpleSwipeLayout_layout_swipeLeftTextColors)
                    ?: intArrayOf()

            rightTextColors =
                array.getIntArrayOrNull(R.styleable.SimpleSwipeLayout_layout_swipeRightTextColors)
                    ?: intArrayOf()

            leftIconColors =
                array.getIntArrayOrNull(R.styleable.SimpleSwipeLayout_layout_swipeLeftIconColors)
                    ?: intArrayOf()

            rightIconColors =
                array.getIntArrayOrNull(R.styleable.SimpleSwipeLayout_layout_swipeRightIconColors)
                    ?: intArrayOf()

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

    private fun compareArrays(arr1: IntArray, arr2: IntArray) {
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
        icons: IntArray,
        iconColors: IntArray,
        backgroundColors: IntArray,
        texts: Array<String>,
        textColors: IntArray,
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
            this
        )
    }
}

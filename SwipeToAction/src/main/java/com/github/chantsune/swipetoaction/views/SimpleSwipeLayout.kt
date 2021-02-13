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
                array.getDimensionPixelSize(R.styleable.SimpleSwipeLayout_swipeItemWidth, 100)
            canFullSwipeFromRight =
                array.getBoolean(R.styleable.SimpleSwipeLayout_canFullSwipeFromRight, false)
            canFullSwipeFromLeft =
                array.getBoolean(R.styleable.SimpleSwipeLayout_canFullSwipeFromLeft, false)
            onlyOneSwipe = array.getBoolean(R.styleable.SimpleSwipeLayout_onlyOneSwipe, true)
            autoHideSwipe = array.getBoolean(R.styleable.SimpleSwipeLayout_autoHideSwipe, true)

            iconSize = array.getDimensionPixelSize(
                R.styleable.SimpleSwipeLayout_iconSize,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            textSize =
                array.getDimensionPixelSize(R.styleable.SimpleSwipeLayout_textSize, NO_ID).toFloat()
            textTopMargin =
                array.getDimensionPixelSize(R.styleable.SimpleSwipeLayout_textTopMargin, 20)
            val rightIconsRes =
                array.getResourceIdOrNull(R.styleable.SimpleSwipeLayout_rightItemIcons)
            val leftIconsRes =
                array.getResourceIdOrNull(R.styleable.SimpleSwipeLayout_leftItemIcons)

            rightColors =
                array.getIntArrayOrNull(R.styleable.SimpleSwipeLayout_rightItemColors)?.toList()
                    ?: listOf()

            if (rightIconsRes != null && !isInEditMode)
                rightIcons = fillDrawables(resources.obtainTypedArray(rightIconsRes)).toList()

            leftColors =
                array.getIntArrayOrNull(R.styleable.SimpleSwipeLayout_leftItemColors)?.toList()
                    ?: listOf()

            if (leftIconsRes != null && !isInEditMode)
                leftIcons = fillDrawables(resources.obtainTypedArray(leftIconsRes)).toList()

            leftTexts =
                array.getStringArrayOrNull(R.styleable.SimpleSwipeLayout_leftStrings)?.toList()
                    ?: listOf()

            rightTexts =
                array.getStringArrayOrNull(R.styleable.SimpleSwipeLayout_rightStrings)?.toList()
                    ?: listOf()

            leftTextColors =
                array.getIntArrayOrNull(R.styleable.SimpleSwipeLayout_leftTextColors)?.toList()
                    ?: listOf()

            rightTextColors =
                array.getIntArrayOrNull(R.styleable.SimpleSwipeLayout_rightTextColors)?.toList()
                    ?: listOf()

            leftIconColors =
                array.getIntArrayOrNull(R.styleable.SimpleSwipeLayout_leftIconColors)?.toList()
                    ?: listOf()

            rightIconColors =
                array.getIntArrayOrNull(R.styleable.SimpleSwipeLayout_rightIconColors)?.toList()
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
        val p = icons
            .zipLongest(iconColors)
            .zipLongest(backgroundColors)
            .zipLongest(texts)
            .zipLongest(textColors)
            .map {
                val icon = it.first?.first ?: NO_ID
                val iconColor = it.first?.second ?: NO_ID
                val bgColor = it.first?.third ?: NO_ID
                val txt = it.second
                val txtColor = it.third ?: NO_ID
                SwipeItemParams(icon, iconColor, bgColor, txt, txtColor)
            }
        return p.map { itemParam ->
            createSwipeItem(
                itemParam.icon,
                itemParam.iconColor,
                itemParam.backgroundColor,
                itemParam.txt,
                itemParam.textColor,
                left
            )
        }
    }

    private fun createSwipeItem(
        icon: Int,
        iconColor: Int,
        backgroundColor: Int,
        text: String?,
        textColor: Int,
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

    private data class SwipeItemParams(
        val icon: Int,
        val iconColor: Int,
        val backgroundColor: Int,
        val txt: String?,
        val textColor: Int,
    )
}

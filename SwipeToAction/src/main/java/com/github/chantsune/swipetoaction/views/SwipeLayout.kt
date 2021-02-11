package com.github.chantsune.swipetoaction.views

import android.content.Context
import android.content.res.TypedArray
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.view.animation.Animation
import android.widget.*
import androidx.core.content.res.use
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.github.chantsune.swipetoaction.R
import com.github.chantsune.swipetoaction.animations.SwipeAnimation
import com.github.chantsune.swipetoaction.animations.WeightAnimation
import com.github.chantsune.swipetoaction.extensions.viewWeight
import com.github.chantsune.swipetoaction.extensions.viewWidth
import com.github.chantsune.swipetoaction.extensions.zipLongest
import kotlin.math.abs

open class SwipeLayout(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(
        context, attrs
    ), OnTouchListener {
    private var contentLayoutId = 0

    var leftColors: List<Int> = emptyList()
    var leftIcons: List<Int> = emptyList()
    var leftIconColors: List<Int> = emptyList()
    var leftTextColors: List<Int> = emptyList()

    var rightColors: List<Int> = emptyList()
    var rightIcons: List<Int> = emptyList()
    var rightIconColors: List<Int> = emptyList()
    var rightTextColors: List<Int> = emptyList()

    var leftTexts: List<String> = emptyList()
    var rightTexts: List<String> = emptyList()

    private var itemWidth = 0
    private val rightLayoutMaxWidth: Int get() = itemWidth * rightIcons.size
    private val leftLayoutMaxWidth: Int get() = itemWidth * leftIcons.size
    var contentView: View? = null
        private set
    private var rightLinear: LinearLayout? = null
    private var leftLinear: LinearLayout? = null
    private var rightLinearWithoutLast: LinearLayout? = null
    private var leftLinearWithoutFirst: LinearLayout? = null
    private var iconSize = 0
    private var textSize = 0f
    private var textTopMargin = 0
    private val fullSwipeEdgePadding: Int =
        resources.getDimensionPixelSize(R.dimen.full_swipe_edge_padding)
    var rightViews: MutableList<View> = mutableListOf()
        private set
    var leftViews: MutableList<View> = mutableListOf()
        private set
    private var onSwipeItemClickListener: OnSwipeItemClickListener? = null
    var isSwipeEnabled = true
    var canFullSwipeFromRight = false
    var canFullSwipeFromLeft = false
    private var autoHideSwipe = true
    private var onlyOneSwipe = true
    private var onScrollListener: RecyclerView.OnScrollListener? = null

    init {
        attrs?.let { setUpAttrs(it) }
        setUpView()
    }

    fun setOnSwipeItemClickListener(listener: OnSwipeItemClickListener?) {
        onSwipeItemClickListener = listener
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setAutoHideSwipe(autoHideSwipe)
        setOnlyOneSwipe(onlyOneSwipe)
    }

    override fun onDetachedFromWindow() {
        setItemState(ITEM_STATE_COLLAPSED, false)
        super.onDetachedFromWindow()
    }

    fun setContentView(view: View) {
        contentView?.let { removeView(it) }
        contentView = view
        addView(view)
    }

    private fun setUpView() {
        if (contentLayoutId != -NO_ID) {
            contentView = LayoutInflater.from(context).inflate(contentLayoutId, null)
        }
        if (contentView != null) {
            compareArrays(leftColors, leftIcons)
            compareArrays(rightColors, rightIcons)
            compareArrays(leftIconColors, leftIcons)
            compareArrays(rightIconColors, rightIcons)
            addView(contentView)
            createItemLayouts()
            contentView!!.bringToFront()
            contentView!!.setOnTouchListener(this)
        }
    }

    private fun compareArrays(arr1: List<Int>, arr2: List<Int>) {
        check(arr1.size >= arr2.size) { "Drawable array shouldn't be bigger than color array" }
    }

    fun invalidateSwipeItems() {
        createItemLayouts()
    }

    private fun createItemLayouts() {
        createRightItemLayout()
        createLeftItemLayout()
    }

    private fun createRightItemLayout() {
        rightLinear?.let { removeView(it) }
        rightLinear = createLinearLayout(Gravity.END)
        rightLinearWithoutLast = createLinearLayout(Gravity.END).also { linearLayout ->
            linearLayout.layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                (rightIcons.size - 1).toFloat()
            )
        }
        addView(rightLinear)
        rightLinear!!.addView(rightLinearWithoutLast)
        addSwipeItems(
            rightIcons,
            rightIconColors,
            rightColors,
            rightTexts,
            rightTextColors,
            rightLinear,
            rightLinearWithoutLast,
            rightViews,
            false
        )
    }

    private fun createLeftItemLayout() {
        leftLinear?.let { removeView(it) }
        leftLinear = createLinearLayout(Gravity.START)
        leftLinearWithoutFirst = createLinearLayout(Gravity.START).also { linearLayout ->
            linearLayout.layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                (leftIcons.size - 1).toFloat()
            )
        }
        addView(leftLinear)
        addSwipeItems(
            leftIcons,
            leftIconColors,
            leftColors,
            leftTexts,
            leftTextColors,
            leftLinear,
            leftLinearWithoutFirst,
            leftViews,
            true
        )
        leftLinear!!.addView(leftLinearWithoutFirst)
    }

    data class SwipeItemParams(
        val icon: Int,
        val iconColor: Int,
        val backgroundColor: Int,
        val txt: String?,
        val textColor: Int,
    )

    private fun addSwipeItems(
        icons: List<Int>,
        iconColors: List<Int>,
        backgroundColors: List<Int>,
        texts: List<String>,
        textColors: List<Int>,
        layout: LinearLayout?,
        layoutWithout: LinearLayout?,
        views: MutableList<View>,
        left: Boolean
    ) {
        val p = icons.zipLongest(iconColors).zipLongest(backgroundColors).zipLongest(texts)
            .zipLongest(textColors).map {
            val icon = it.first?.first ?: NO_ID
            val iconColor = it.first?.second ?: NO_ID
            val bgColor = it.first?.third ?: NO_ID
            val txt = it.second
            val txtColor = it.third ?: NO_ID
            SwipeItemParams(icon, iconColor, bgColor, txt, txtColor)
        }
        val views = p.mapIndexed { i, itemParam ->
            createSwipeItem(
                itemParam.icon,
                itemParam.iconColor,
                itemParam.backgroundColor,
                itemParam.txt,
                itemParam.textColor,
                left
            ).also { itemView ->
                itemView.isClickable = true
                itemView.isFocusable = true
                itemView.setOnClickListener {
                    onSwipeItemClickListener?.onSwipeItemClick(left, i)
                }
            }
        }

        if (left) {
            leftViews = views.toMutableList()
        } else {
            rightViews = views.toMutableList()
        }

        for ((i, swipeItem) in views.withIndex()) {
            if (i == icons.size - (if (left) icons.size else 1)) {
                layout!!.addView(swipeItem)
            } else {
                layoutWithout!!.addView(swipeItem)
            }
        }
    }

    fun setAlphaAtIndex(left: Boolean, index: Int, alpha: Float) {
        val views = if (left) leftViews else rightViews
        views.getOrNull(index)?.alpha = alpha
    }

    fun setEnableAtIndex(left: Boolean, index: Int, enabled: Boolean) {
        val views = if (left) leftViews else rightViews
        views.getOrNull(index)?.isEnabled = enabled
    }

    fun getAlphaAtIndex(left: Boolean, index: Int): Float {
        val views = if (left) leftViews else rightViews
        return views.getOrNull(index)?.alpha ?: 1f
    }

    fun isEnabledAtIndex(left: Boolean, index: Int): Boolean {
        val views = if (left) leftViews else rightViews
        return views.getOrNull(index)?.isEnabled ?: true
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        contentView?.setOnClickListener(listener)
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

    private fun createLinearLayout(gravity: Int): LinearLayout {
        return LinearLayout(context).also { linearLayout ->
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.layoutParams =
                LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, gravity)
        }
    }

    private fun setUpAttrs(attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.SwipeLayout).use { array ->
            contentLayoutId = array.getResourceId(R.styleable.SwipeLayout_foregroundLayout, NO_ID)
            itemWidth = array.getDimensionPixelSize(R.styleable.SwipeLayout_swipeItemWidth, 100)
            iconSize = array.getDimensionPixelSize(
                R.styleable.SwipeLayout_iconSize,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            textSize =
                array.getDimensionPixelSize(R.styleable.SwipeLayout_textSize, NO_ID).toFloat()
            textTopMargin = array.getDimensionPixelSize(R.styleable.SwipeLayout_textTopMargin, 20)
            canFullSwipeFromRight =
                array.getBoolean(R.styleable.SwipeLayout_canFullSwipeFromRight, false)
            canFullSwipeFromLeft =
                array.getBoolean(R.styleable.SwipeLayout_canFullSwipeFromLeft, false)
            onlyOneSwipe = array.getBoolean(R.styleable.SwipeLayout_onlyOneSwipe, true)
            autoHideSwipe = array.getBoolean(R.styleable.SwipeLayout_autoHideSwipe, true)
            val rightColorsRes = array.getResourceId(R.styleable.SwipeLayout_rightItemColors, NO_ID)
            val rightIconsRes = array.getResourceId(R.styleable.SwipeLayout_rightItemIcons, NO_ID)
            val leftColorsRes = array.getResourceId(R.styleable.SwipeLayout_leftItemColors, NO_ID)
            val leftIconsRes = array.getResourceId(R.styleable.SwipeLayout_leftItemIcons, NO_ID)
            val leftTextRes = array.getResourceId(R.styleable.SwipeLayout_leftStrings, NO_ID)
            val rightTextRes = array.getResourceId(R.styleable.SwipeLayout_rightStrings, NO_ID)
            val leftTextColorRes =
                array.getResourceId(R.styleable.SwipeLayout_leftTextColors, NO_ID)
            val rightTextColorRes =
                array.getResourceId(R.styleable.SwipeLayout_rightTextColors, NO_ID)
            val leftIconColors = array.getResourceId(R.styleable.SwipeLayout_leftIconColors, NO_ID)
            val rightIconColors =
                array.getResourceId(R.styleable.SwipeLayout_rightIconColors, NO_ID)
            initiateArrays(
                rightColorsRes,
                rightIconsRes,
                leftColorsRes,
                leftIconsRes,
                leftTextRes,
                rightTextRes,
                leftTextColorRes,
                rightTextColorRes,
                leftIconColors,
                rightIconColors
            )
        }
    }

    private fun initiateArrays(
        rightColorsRes: Int, rightIconsRes: Int, leftColorsRes: Int, leftIconsRes: Int,
        leftTextRes: Int, rightTextRes: Int, leftTextColorRes: Int, rightTextColorRes: Int,
        leftIconColorsRes: Int, rightIconColorsRes: Int
    ) {
        if (rightColorsRes != NO_ID)
            rightColors = resources.getIntArray(rightColorsRes).toList()
        if (rightIconsRes != NO_ID && !isInEditMode)
            rightIcons = fillDrawables(resources.obtainTypedArray(rightIconsRes)).toList()
        if (leftColorsRes != NO_ID)
            leftColors = resources.getIntArray(leftColorsRes).toList()
        if (leftIconsRes != NO_ID && !isInEditMode)
            leftIcons = fillDrawables(resources.obtainTypedArray(leftIconsRes)).toList()
        if (leftTextRes != NO_ID)
            leftTexts = resources.getStringArray(leftTextRes).toList()
        if (rightTextRes != NO_ID)
            rightTexts = resources.getStringArray(rightTextRes).toList()
        if (leftTextColorRes != NO_ID)
            leftTextColors = resources.getIntArray(leftTextColorRes).toList()
        if (rightTextColorRes != NO_ID)
            rightTextColors = resources.getIntArray(rightTextColorRes).toList()
        if (leftIconColorsRes != NO_ID)
            leftIconColors = resources.getIntArray(leftIconColorsRes).toList()
        if (rightIconColorsRes != NO_ID)
            rightIconColors = resources.getIntArray(rightIconColorsRes).toList()
    }

    private fun fillDrawables(ta: TypedArray): IntArray {
        return ta.use {
            IntArray(ta.length()) { ta.getResourceId(it, NO_ID) }
        }
    }

    var prevRawX = -1f
    var directionLeft = false
    var movementStarted = false
    var lastTime: Long = 0
    var downTime: Long = 0
    var speed = 0f
    var downRawX = 0f
    var downX = 0f
    var downY = 0f
    private fun clearAnimations() {
        contentView?.clearAnimation()
        rightLinear?.clearAnimation()
        leftLinear?.clearAnimation()
        rightLinearWithoutLast?.clearAnimation()
        leftLinearWithoutFirst?.clearAnimation()
    }

    var shouldPerformLongClick = false
    var longClickPerformed = false
    private val longClickHandler = Handler()
    private val longClickRunnable = Runnable {
        if (shouldPerformLongClick) {
            if (performLongClick()) {
                longClickPerformed = true
                isPressed = false
            }
        }
    }

    override fun setPressed(pressed: Boolean) {
        super.setPressed(pressed)
        drawableHotspotChanged(downX, downY)
    }

    private val collapsibleViews: List<View>
        get() = if (invokedFromLeft) leftViews else rightViews
    private val collapseListener: Animation.AnimationListener =
        object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                clickBySwipe()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        }

    private fun clickBySwipe() {
        onSwipeItemClickListener?.onSwipeItemClick(
            invokedFromLeft,
            if (invokedFromLeft) 0 else rightIcons.size - 1
        )
    }

    //Set LayoutWithout to weight 0
    private var collapseAnim: WeightAnimation? = null

    //Set LayoutWithout to weight rightIcons.length - 1
    private var expandAnim: WeightAnimation? = null

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        if (!isSwipeEnabled) return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                run {
                    lastTime = System.currentTimeMillis()
                    downTime = lastTime
                }
                run {
                    prevRawX = event.rawX
                    downRawX = prevRawX
                }
                if (contentView!!.translationX == 0f) {
                    rightLinearWithoutLast?.viewWeight = (rightViews.size - 1).toFloat()
                    leftLinearWithoutFirst?.viewWeight = (leftViews.size - 1).toFloat()
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (abs(prevRawX - event.rawX) < 20 && !movementStarted) {
                    if (System.currentTimeMillis() - lastTime >= 50 && !isPressed && !isExpanding && !longClickPerformed) {
                        view.isPressed = true
                        if (!shouldPerformLongClick) {
                            shouldPerformLongClick = true
                            longClickHandler.postDelayed(
                                longClickRunnable,
                                ViewConfiguration.getLongPressTimeout().toLong()
                            )
                        }
                    }
                    return false
                }
                if (view.isPressed) view.isPressed = false
                shouldPerformLongClick = false
                movementStarted = true
                collapseOthersIfNeeded()
                clearAnimations()
                directionLeft = prevRawX - event.rawX > 0
                val delta = abs(prevRawX - event.rawX)
                speed = (System.currentTimeMillis() - lastTime) / delta
                if (directionLeft) {
                    var left = contentView!!.translationX - delta
                    if (left < -rightLayoutMaxWidth) {
                        if (!canFullSwipeFromRight) {
                            left = -rightLayoutMaxWidth.toFloat()
                        } else if (left < -width) {
                            left = -width.toFloat()
                        }
                    }
                    if (canFullSwipeFromRight) {
                        if (contentView!!.translationX <= -(width - fullSwipeEdgePadding)) {
                            if (rightLinearWithoutLast!!.viewWeight > 0 &&
                                (collapseAnim == null || collapseAnim!!.hasEnded())
                            ) {
                                view.isPressed = false
                                rightLinearWithoutLast!!.clearAnimation()
                                if (expandAnim != null) expandAnim = null
                                collapseAnim = WeightAnimation(0f, rightLinearWithoutLast!!)
                                Log.d("WeightAnim", "onTouch - Collapse")
                                startAnimation(collapseAnim)
                            }
                        } else {
                            if (rightLinearWithoutLast!!.viewWeight < rightIcons.size - 1f &&
                                (expandAnim == null || expandAnim!!.hasEnded())
                            ) {
                                Log.d("WeightAnim", "onTouch - Expand")
                                view.isPressed = false
                                rightLinearWithoutLast!!.clearAnimation()
                                if (collapseAnim != null) collapseAnim = null
                                expandAnim = WeightAnimation(
                                    (rightIcons.size - 1).toFloat(),
                                    rightLinearWithoutLast!!
                                )
                                startAnimation(expandAnim)
                            }
                        }
                    }
                    contentView!!.translationX = left
                    if (rightLinear != null) {
                        val rightLayoutWidth = abs(left).toInt()
                        rightLinear!!.viewWidth = rightLayoutWidth
                    }
                    if (leftLinear != null && left > 0) {
                        val leftLayoutWidth = abs(contentView!!.translationX).toInt()
                        leftLinear!!.viewWidth = leftLayoutWidth
                    }
                } else {
                    var right = contentView!!.translationX + delta
                    if (right > leftLayoutMaxWidth) {
                        if (!canFullSwipeFromLeft) {
                            right = leftLayoutMaxWidth.toFloat()
                        } else if (right >= width) {
                            right = width.toFloat()
                        }
                    }
                    if (canFullSwipeFromLeft) {
                        if (contentView!!.translationX >= width - fullSwipeEdgePadding) {
                            if (leftLinearWithoutFirst!!.viewWeight > 0 &&
                                (collapseAnim == null || collapseAnim!!.hasEnded())
                            ) {
                                leftLinearWithoutFirst!!.clearAnimation()
                                if (expandAnim != null) expandAnim = null
                                collapseAnim = WeightAnimation(0f, leftLinearWithoutFirst!!)
                                startAnimation(collapseAnim)
                            }
                        } else {
                            if (leftLinearWithoutFirst!!.viewWeight < leftIcons.size - 1f &&
                                (expandAnim == null || expandAnim!!.hasEnded())
                            ) {
                                leftLinearWithoutFirst!!.clearAnimation()
                                if (collapseAnim != null) collapseAnim = null
                                expandAnim = WeightAnimation(
                                    (leftIcons.size - 1).toFloat(),
                                    leftLinearWithoutFirst!!
                                )
                                startAnimation(expandAnim)
                            }
                        }
                    }
                    contentView!!.translationX = right
                    if (leftLinear != null && right > 0) {
                        val leftLayoutWidth = abs(right).toInt()
                        leftLinear!!.viewWidth = leftLayoutWidth
                    }
                    if (rightLinear != null) {
                        val rightLayoutWidth = abs(contentView!!.translationX).toInt()
                        rightLinear!!.viewWidth = rightLayoutWidth
                    }
                }
                if (abs(contentView!!.translationX) > itemWidth / 5) {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                prevRawX = event.rawX
                lastTime = System.currentTimeMillis()
                return true
            }
            MotionEvent.ACTION_UP -> {
                finishMotion(event)
                if (movementStarted) {
                    finishSwipeAnimated()
                } else {
                    view.isPressed = false
                    if (System.currentTimeMillis() - downTime < ViewConfiguration.getTapTimeout()) {
                        view.isPressed = true
                        view.performClick()
                        view.isPressed = false
                    }
                }
                return false
            }
            MotionEvent.ACTION_CANCEL -> {
                finishMotion(event)
                if (movementStarted) finishSwipeAnimated()
                return false
            }
        }
        return false
    }

    private fun collapseOthersIfNeeded() {
        if (!onlyOneSwipe) return
        val parent = parent
        if (parent != null && parent is RecyclerView) {
            for (item in parent.children) {
                if (item !== this && item is SwipeLayout) {
                    if (item.contentView!!.translationX != 0f && !item.inAnimatedState()) {
                        item.setItemState(ITEM_STATE_COLLAPSED, true)
                    }
                }
            }
        }
    }

    private fun finishMotion(event: MotionEvent) {
        directionLeft = event.rawX - downRawX < 0
        longClickHandler.removeCallbacks(longClickRunnable)
        shouldPerformLongClick = false
        longClickPerformed = false
    }

    var invokedFromLeft = false
    private fun finishSwipeAnimated() {
        shouldPerformLongClick = false
        isPressed = false
        parent.requestDisallowInterceptTouchEvent(false)
        movementStarted = false
        var animateView: LinearLayout? = null
        var left = false
        var requiredWidth = 0
        if (contentView!!.translationX > 0) {
            animateView = leftLinear
            left = true
            if (leftLinear != null) {
                val reqWidth =
                    if (directionLeft) leftLayoutMaxWidth - leftLayoutMaxWidth / 3 else leftLayoutMaxWidth / 3
                if (rightLinear != null) {
                    rightLinear!!.viewWidth = 0
                }
                if (leftLinear!!.width >= reqWidth) {
                    requiredWidth = leftLayoutMaxWidth
                }
                if (requiredWidth == leftLayoutMaxWidth && !directionLeft) {
                    if (contentView!!.translationX >= width - fullSwipeEdgePadding) {
                        requiredWidth = width
                        invokedFromLeft = true
                    }
                }
                contentView!!.translationX = leftLinear!!.width.toFloat()
            }
        } else if (contentView!!.translationX < 0) {
            left = false
            animateView = rightLinear
            if (rightLinear != null) {
                if (leftLinear != null) {
                    leftLinear!!.viewWidth = 0
                }
                val reqWidth =
                    if (directionLeft) rightLayoutMaxWidth / 3 else rightLayoutMaxWidth - rightLayoutMaxWidth / 3
                if (rightLinear!!.width >= reqWidth) {
                    requiredWidth = rightLayoutMaxWidth
                }
                if (requiredWidth == rightLayoutMaxWidth && directionLeft) {
                    if (contentView!!.translationX <= -(width - fullSwipeEdgePadding)) {
                        requiredWidth = width
                        invokedFromLeft = false
                    }
                }
                contentView!!.translationX = -rightLinear!!.width.toFloat()
            }
        }
        var duration = (100 * speed).toLong()
        if (animateView != null) {
            val swipeAnim = SwipeAnimation(animateView, requiredWidth, contentView!!, left)
            if (duration < ANIMATION_MIN_DURATION) duration =
                ANIMATION_MIN_DURATION else if (duration > ANIMATION_MAX_DURATION) duration =
                ANIMATION_MAX_DURATION
            swipeAnim.duration = duration
            val layoutWithout =
                if (animateView === leftLinear) leftLinearWithoutFirst else rightLinearWithoutLast
            val views = if (animateView === leftLinear) leftViews else rightViews
            invokedFromLeft = animateView === leftLinear
            if (requiredWidth == width) {
                if (layoutWithout!!.viewWeight == 0f && width.toFloat() != abs(contentView!!.translationX))
                    swipeAnim.setAnimationListener(collapseListener)
                else if (collapseAnim != null && !collapseAnim!!.hasEnded()) {
                    collapseAnim!!.setAnimationListener(collapseListener)
                } else if (layoutWithout.viewWeight == 0f || width.toFloat() == abs(contentView!!.translationX)) {
                    clickBySwipe()
                } else {
                    layoutWithout.clearAnimation()
                    if (collapseAnim != null) collapseAnim!!.cancel()
                    collapseAnim = WeightAnimation(0f, layoutWithout)
                    collapseAnim!!.setAnimationListener(collapseListener)
                    layoutWithout.startAnimation(collapseAnim)
                }
            } else {
                val weightAnimation = WeightAnimation((views.size - 1).toFloat(), layoutWithout!!)
                layoutWithout.startAnimation(weightAnimation)
            }
            animateView.startAnimation(swipeAnim)
        }
    }

    @Deprecated("use setItemState()")
    fun closeItem() {
        collapseItem(true)
    }

    private fun collapseItem(animated: Boolean) {
        if (leftLinear != null && leftLinear!!.width > 0) {
            leftLinearWithoutFirst!!.viewWidth = leftViews.size - 1
            if (animated) {
                val swipeAnim = SwipeAnimation(leftLinear!!, 0, contentView!!, true)
                leftLinear!!.startAnimation(swipeAnim)
            } else {
                contentView!!.translationX = 0f
                leftLinear!!.viewWidth = 0
            }
        } else if (rightLinear != null && rightLinear!!.width > 0) {
            rightLinearWithoutLast!!.viewWidth = rightViews.size - 1
            if (animated) {
                val swipeAnim = SwipeAnimation(rightLinear!!, 0, contentView!!, false)
                rightLinear!!.startAnimation(swipeAnim)
            } else {
                contentView!!.translationX = 0f
                rightLinear!!.viewWidth = 0
            }
        }
    }

    fun setItemState(state: Int, animated: Boolean) {
        when (state) {
            ITEM_STATE_COLLAPSED -> collapseItem(animated)
            ITEM_STATE_LEFT_EXPAND -> {
                val requiredWidthLeft = leftIcons.size * itemWidth
                if (animated) {
                    val swipeAnim =
                        SwipeAnimation(leftLinear!!, requiredWidthLeft, contentView!!, true)
                    leftLinear!!.startAnimation(swipeAnim)
                } else {
                    contentView!!.translationX = requiredWidthLeft.toFloat()
                    leftLinear!!.viewWidth = requiredWidthLeft
                }
            }
            ITEM_STATE_RIGHT_EXPAND -> {
                val requiredWidthRight = rightIcons.size * itemWidth
                if (animated) {
                    val swipeAnim =
                        SwipeAnimation(rightLinear!!, requiredWidthRight, contentView!!, false)
                    rightLinear!!.startAnimation(swipeAnim)
                } else {
                    contentView!!.translationX = -requiredWidthRight.toFloat()
                    rightLinear!!.viewWidth = requiredWidthRight
                }
            }
        }
    }

    fun inAnimatedState(): Boolean {
        if (leftLinear != null) {
            val anim = leftLinear!!.animation
            if (anim != null && !anim.hasEnded()) return true
        }
        if (rightLinear != null) {
            val anim = rightLinear!!.animation
            if (anim != null && !anim.hasEnded()) return true
        }
        return false
    }

    fun setAutoHideSwipe(autoHideSwipe: Boolean) {
        this.autoHideSwipe = autoHideSwipe
        val parent = parent
        if (parent != null && parent is RecyclerView) {
            onScrollListener?.let { parent.removeOnScrollListener(it) }
            if (autoHideSwipe) {
                parent.addOnScrollListener(object :
                    RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (newState == RecyclerView.SCROLL_STATE_DRAGGING && contentView!!.translationX != 0f) {
                            setItemState(ITEM_STATE_COLLAPSED, true)
                        }
                    }
                }.also { onScrollListener = it })
            }
        } else {
            Log.e(TAG, "For autoHideSwipe parent must be a RecyclerView")
        }
    }

    fun setOnlyOneSwipe(onlyOneSwipe: Boolean) {
        this.onlyOneSwipe = onlyOneSwipe
    }

    val isLeftExpanding: Boolean
        get() = contentView!!.translationX > 0
    val isRightExpanding: Boolean
        get() = contentView!!.translationX < 0
    val isExpanding: Boolean
        get() = isRightExpanding || isLeftExpanding
    val isRightExpanded: Boolean
        get() = rightLinear != null && rightLinear!!.width >= rightLayoutMaxWidth
    val isLeftExpanded: Boolean
        get() = leftLinear != null && leftLinear!!.width >= leftLayoutMaxWidth
    val isExpanded: Boolean
        get() = isLeftExpanded || isRightExpanded

    fun collapseAll(animated: Boolean) {
        val parent = parent
        if (parent != null && parent is RecyclerView) {
            for (item in parent.children) {
                if (item is SwipeLayout) {
                    if (item.contentView!!.translationX != 0f) {
                        item.setItemState(ITEM_STATE_COLLAPSED, animated)
                    }
                }
            }
        }
    }

    interface OnSwipeItemClickListener {
        fun onSwipeItemClick(left: Boolean, index: Int)
    }

    companion object {
        val TAG = SwipeLayout::class.simpleName
        const val NO_ID = 0
        const val ITEM_STATE_LEFT_EXPAND = 0
        const val ITEM_STATE_RIGHT_EXPAND = 1
        const val ITEM_STATE_COLLAPSED = 2
        private const val ANIMATION_MIN_DURATION: Long = 100
        private const val ANIMATION_MAX_DURATION: Long = 300
    }
}

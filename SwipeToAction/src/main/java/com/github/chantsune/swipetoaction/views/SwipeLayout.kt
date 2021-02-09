package com.github.chantsune.swipetoaction.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.View.OnTouchListener
import android.view.animation.Animation
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.use
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.github.chantsune.swipetoaction.R
import com.github.chantsune.swipetoaction.animations.SwipeAnimation
import com.github.chantsune.swipetoaction.animations.WeightAnimation
import com.github.chantsune.swipetoaction.extensions.Utils.setTint
import com.github.chantsune.swipetoaction.extensions.Utils.setViewWidth
import com.github.chantsune.swipetoaction.extensions.viewWeight
import kotlin.math.abs

class SwipeLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(
        context, attrs
    ), OnTouchListener, View.OnClickListener {
    private var layoutId = 0
    private var leftColors: IntArray? = null
    var leftIcons: IntArray? = null
    var leftIconColors: IntArray? = null
    var rightColors: IntArray? = null
    var rightIcons: IntArray? = null
    var rightIconColors: IntArray? = null
    var rightTextColors: IntArray? = null

    var leftTextColors: IntArray? = null
    var leftTexts: Array<String>? = null
    var rightTexts: Array<String>? = null
    private var itemWidth = 0
    private var rightLayoutMaxWidth = 0
    private var leftLayoutMaxWidth = 0
    var swipeableView: View? = null
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

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (swipeableView != null) super.addView(child, index, params) else {
            swipeableView = child
            setUpView()
        }
    }

    private fun setUpView() {
        if (layoutId != -NO_ID) {
            swipeableView = LayoutInflater.from(context).inflate(layoutId, null)
        }
        if (swipeableView != null) {
            compareArrays(leftColors, leftIcons)
            compareArrays(rightColors, rightIcons)
            compareArrays(leftIconColors, leftIcons)
            compareArrays(rightIconColors, rightIcons)
            addView(swipeableView)
            createItemLayouts()
            swipeableView!!.bringToFront()
            swipeableView!!.setOnTouchListener(this)
        }
    }

    private fun compareArrays(arr1: IntArray?, arr2: IntArray?) {
        if (arr1 != null && arr2 != null) {
            check(arr1.size >= arr2.size) { "Drawable array shouldn't be bigger than color array" }
        }
    }

    fun invalidateSwipeItems() {
        createItemLayouts()
    }

    private fun createItemLayouts() {
        if (rightIcons != null) {
            rightLayoutMaxWidth = itemWidth * rightIcons!!.size
            if (rightLinear != null) removeView(rightLinear)
            rightLinear = createLinearLayout(Gravity.END)
            rightLinearWithoutLast = createLinearLayout(Gravity.END)
            rightLinearWithoutLast!!.layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                (rightIcons!!.size - 1).toFloat()
            )
            addView(rightLinear)
            rightLinear!!.addView(rightLinearWithoutLast)
            addSwipeItems(
                rightIcons!!,
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
        if (leftIcons != null) {
            leftLayoutMaxWidth = itemWidth * leftIcons!!.size
            if (leftLinear != null) removeView(leftLinear)
            leftLinear = createLinearLayout(Gravity.START)
            leftLinearWithoutFirst = createLinearLayout(Gravity.START)
            leftLinearWithoutFirst!!.layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                (leftIcons!!.size - 1).toFloat()
            )
            addView(leftLinear)
            addSwipeItems(
                leftIcons!!,
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
    }

    private fun addSwipeItems(
        icons: IntArray,
        iconColors: IntArray?,
        backgroundColors: IntArray?,
        texts: Array<String>?,
        textColors: IntArray?,
        layout: LinearLayout?,
        layoutWithout: LinearLayout?,
        views: MutableList<View>,
        left: Boolean
    ) {
        for (i in icons.indices) {
            val backgroundColor = backgroundColors?.getOrNull(i) ?: NO_ID
            val iconColor = iconColors?.getOrNull(i) ?: NO_ID
            val txt: String? = texts?.getOrNull(i)
            val textColor = textColors?.getOrNull(i) ?: NO_ID
            val swipeItem =
                createSwipeItem(icons[i], iconColor, backgroundColor, txt, textColor, left)
            swipeItem.isClickable = true
            swipeItem.isFocusable = true
            swipeItem.setOnClickListener(this)
            views.add(swipeItem)
            if (i == icons.size - (if (!left) 1 else icons.size)) {
                layout!!.addView(swipeItem)
            } else {
                layoutWithout!!.addView(swipeItem)
            }
        }
    }

    fun setAlphaAtIndex(left: Boolean, index: Int, alpha: Float) {
        val views = if (left) leftViews else rightViews
        if (index <= views.size - 1) {
            views[index].alpha = alpha
        }
    }

    fun setEnableAtIndex(left: Boolean, index: Int, enabled: Boolean) {
        val views = if (left) leftViews else rightViews
        if (index <= views.size - 1) {
            views[index].isEnabled = enabled
        }
    }

    fun getAlphaAtIndex(left: Boolean, index: Int): Float {
        val views = if (left) leftViews else rightViews
        return if (index <= views.size - 1) {
            views[index].alpha
        } else 1f
    }

    fun isEnabledAtIndex(left: Boolean, index: Int): Boolean {
        val views = if (left) leftViews else rightViews
        return if (index <= views.size - 1) {
            views[index].isEnabled
        } else true
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        swipeableView?.setOnClickListener(listener)
    }

    private val rippleDrawable: Drawable?
        private get() {
            val attrs = intArrayOf(android.R.attr.selectableItemBackground)
            val ta = context.obtainStyledAttributes(attrs)
            val ripple = ta.getDrawable(0)
            ta.recycle()
            return ripple
        }
    var id_ = 0
    private fun createSwipeItem(
        icon: Int,
        iconColor: Int,
        backgroundColor: Int,
        text: String?,
        textColor: Int,
        left: Boolean
    ): ViewGroup {
        val frameLayout = FrameLayout(context)
        frameLayout.layoutParams =
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
        val view = View(context)
        view.layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        view.background = rippleDrawable
        frameLayout.addView(view)
        if (backgroundColor != NO_ID) {
            frameLayout.setBackgroundColor(backgroundColor)
        }
        val imageView = ImageView(context)
        var drawable = ContextCompat.getDrawable(context, icon)
        if (iconColor != NO_ID) {
            drawable = setTint(drawable!!, iconColor)
        }
        imageView.setImageDrawable(drawable)
        val relativeLayout = RelativeLayout(context)
        var gravity = Gravity.CENTER_VERTICAL
        gravity = if (left) {
            gravity or Gravity.END
        } else {
            gravity or Gravity.START
        }
        relativeLayout.layoutParams =
            LayoutParams(itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT, gravity)
        val imageViewParams = RelativeLayout.LayoutParams(iconSize, iconSize)
        imageViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
        imageView.layoutParams = imageViewParams
        imageView.id = ++id_
        relativeLayout.addView(imageView)
        if (text != null) {
            val textView = TextView(context)
            textView.maxLines = 2
            if (textSize > 0) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            }
            if (textColor != NO_ID) {
                textView.setTextColor(textColor)
            }
            if (typeface != null) textView.typeface = typeface
            textView.text = text
            textView.gravity = Gravity.CENTER
            val textViewParams =
                RelativeLayout.LayoutParams(itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
            textViewParams.addRule(RelativeLayout.BELOW, id_)
            textViewParams.topMargin = textTopMargin
            relativeLayout.addView(textView, textViewParams)
        }
        frameLayout.setOnTouchListener(this)
        frameLayout.addView(relativeLayout)
        return frameLayout
    }

    private fun createLinearLayout(gravity: Int): LinearLayout {
        return LinearLayout(context).also { linearLayout ->
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.layoutParams =
                LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT).also { params ->
                    params.gravity = gravity
                }
        }
    }

    private fun setUpAttrs(attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.SwipeLayout).use { array ->
            layoutId = array.getResourceId(R.styleable.SwipeLayout_foregroundLayout, NO_ID)
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
            val typefaceAssetPath = array.getString(R.styleable.SwipeLayout_customFont)
            if (typefaceAssetPath != null) {
                if (typeface == null) {
                    typeface = Typeface.createFromAsset(context.assets, typefaceAssetPath)
                }
            }
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
        val res = resources
        if (rightColorsRes != NO_ID) rightColors = res.getIntArray(rightColorsRes)
        if (rightIconsRes != NO_ID && !isInEditMode) rightIcons =
            fillDrawables(res.obtainTypedArray(rightIconsRes))
        if (leftColorsRes != NO_ID) leftColors = res.getIntArray(leftColorsRes)
        if (leftIconsRes != NO_ID && !isInEditMode) leftIcons =
            fillDrawables(res.obtainTypedArray(leftIconsRes))
        if (leftTextRes != NO_ID) leftTexts = res.getStringArray(leftTextRes)
        if (rightTextRes != NO_ID) rightTexts = res.getStringArray(rightTextRes)
        if (leftTextColorRes != NO_ID) leftTextColors = res.getIntArray(leftTextColorRes)
        if (rightTextColorRes != NO_ID) rightTextColors = res.getIntArray(rightTextColorRes)
        if (leftIconColorsRes != NO_ID) leftIconColors = res.getIntArray(leftIconColorsRes)
        if (rightIconColorsRes != NO_ID) rightIconColors = res.getIntArray(rightIconColorsRes)
    }

    private fun fillDrawables(ta: TypedArray): IntArray {
        val drawableArr = IntArray(ta.length())
        ta.use {
            for (i in 0 until ta.length()) {
                drawableArr[i] = ta.getResourceId(i, NO_ID)
            }
        }
        return drawableArr
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
        swipeableView?.clearAnimation()
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
            if (invokedFromLeft) 0 else rightIcons!!.size - 1
        )
    }

    //Set LayoutWithout to weight 0
    private var collapseAnim: WeightAnimation? = null

    //Set LayoutWithout to weight rightIcons.length - 1
    private var expandAnim: WeightAnimation? = null
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        if (isSwipeEnabled && (leftIcons != null || rightIcons != null)) {
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
                    if (swipeableView!!.translationX == 0f) {
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
                    var rightLayoutWidth = 0
                    var leftLayoutWidth = 0
                    if (directionLeft) {
                        var left = swipeableView!!.translationX - delta
                        if (left < -rightLayoutMaxWidth) {
                            if (!canFullSwipeFromRight) {
                                left = -rightLayoutMaxWidth.toFloat()
                            } else if (left < -width) {
                                left = -width.toFloat()
                            }
                        }
                        if (canFullSwipeFromRight) {
                            if (swipeableView!!.translationX <= -(width - fullSwipeEdgePadding)) {
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
                                if (rightLinearWithoutLast!!.viewWeight < rightIcons!!.size - 1f &&
                                    (expandAnim == null || expandAnim!!.hasEnded())
                                ) {
                                    Log.d("WeightAnim", "onTouch - Expand")
                                    view.isPressed = false
                                    rightLinearWithoutLast!!.clearAnimation()
                                    if (collapseAnim != null) collapseAnim = null
                                    expandAnim = WeightAnimation(
                                        (rightIcons!!.size - 1).toFloat(),
                                        rightLinearWithoutLast!!
                                    )
                                    startAnimation(expandAnim)
                                }
                            }
                        }
                        swipeableView!!.translationX = left
                        if (rightLinear != null) {
                            rightLayoutWidth = abs(left).toInt()
                            setViewWidth(rightLinear!!, rightLayoutWidth)
                        }
                        if (leftLinear != null && left > 0) {
                            leftLayoutWidth = abs(swipeableView!!.translationX).toInt()
                            setViewWidth(leftLinear!!, leftLayoutWidth)
                        }
                    } else {
                        var right = swipeableView!!.translationX + delta
                        if (right > leftLayoutMaxWidth) {
                            if (!canFullSwipeFromLeft) {
                                right = leftLayoutMaxWidth.toFloat()
                            } else if (right >= width) {
                                right = width.toFloat()
                            }
                        }
                        if (canFullSwipeFromLeft) {
                            if (swipeableView!!.translationX >= width - fullSwipeEdgePadding) {
                                if (leftLinearWithoutFirst!!.viewWeight > 0 &&
                                    (collapseAnim == null || collapseAnim!!.hasEnded())
                                ) {
                                    leftLinearWithoutFirst!!.clearAnimation()
                                    if (expandAnim != null) expandAnim = null
                                    collapseAnim = WeightAnimation(0f, leftLinearWithoutFirst!!)
                                    startAnimation(collapseAnim)
                                }
                            } else {
                                if (leftLinearWithoutFirst!!.viewWeight < leftIcons!!.size - 1f &&
                                    (expandAnim == null || expandAnim!!.hasEnded())
                                ) {
                                    leftLinearWithoutFirst!!.clearAnimation()
                                    if (collapseAnim != null) collapseAnim = null
                                    expandAnim = WeightAnimation(
                                        (leftIcons!!.size - 1).toFloat(),
                                        leftLinearWithoutFirst!!
                                    )
                                    startAnimation(expandAnim)
                                }
                            }
                        }
                        swipeableView!!.translationX = right
                        if (leftLinear != null && right > 0) {
                            leftLayoutWidth = abs(right).toInt()
                            setViewWidth(leftLinear!!, leftLayoutWidth)
                        }
                        if (rightLinear != null) {
                            rightLayoutWidth = abs(swipeableView!!.translationX).toInt()
                            setViewWidth(rightLinear!!, rightLayoutWidth)
                        }
                    }
                    if (abs(swipeableView!!.translationX) > itemWidth / 5) {
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
        }
        return false
    }

    private fun collapseOthersIfNeeded() {
        if (!onlyOneSwipe) return
        val parent = parent
        if (parent != null && parent is RecyclerView) {
            for (item in parent.children) {
                if (item !== this && item is SwipeLayout) {
                    if (item.swipeableView!!.translationX != 0f && !item.inAnimatedState()) {
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
        if (swipeableView!!.translationX > 0) {
            animateView = leftLinear
            left = true
            if (leftLinear != null) {
                val reqWidth =
                    if (directionLeft) leftLayoutMaxWidth - leftLayoutMaxWidth / 3 else leftLayoutMaxWidth / 3
                if (rightLinear != null) setViewWidth(rightLinear!!, 0)
                if (leftLinear!!.width >= reqWidth) {
                    requiredWidth = leftLayoutMaxWidth
                }
                if (requiredWidth == leftLayoutMaxWidth && !directionLeft) {
                    if (swipeableView!!.translationX >= width - fullSwipeEdgePadding) {
                        requiredWidth = width
                        invokedFromLeft = true
                    }
                }
                swipeableView!!.translationX = leftLinear!!.width.toFloat()
            }
        } else if (swipeableView!!.translationX < 0) {
            left = false
            animateView = rightLinear
            if (rightLinear != null) {
                if (leftLinear != null) setViewWidth(leftLinear!!, 0)
                val reqWidth =
                    if (directionLeft) rightLayoutMaxWidth / 3 else rightLayoutMaxWidth - rightLayoutMaxWidth / 3
                if (rightLinear!!.width >= reqWidth) {
                    requiredWidth = rightLayoutMaxWidth
                }
                if (requiredWidth == rightLayoutMaxWidth && directionLeft) {
                    if (swipeableView!!.translationX <= -(width - fullSwipeEdgePadding)) {
                        requiredWidth = width
                        invokedFromLeft = false
                    }
                }
                swipeableView!!.translationX = -rightLinear!!.width.toFloat()
            }
        }
        var duration = (100 * speed).toLong()
        if (animateView != null) {
            val swipeAnim = SwipeAnimation(animateView, requiredWidth, swipeableView!!, left)
            if (duration < ANIMATION_MIN_DURATION) duration =
                ANIMATION_MIN_DURATION else if (duration > ANIMATION_MAX_DURATION) duration =
                ANIMATION_MAX_DURATION
            swipeAnim.duration = duration
            val layoutWithout =
                if (animateView === leftLinear) leftLinearWithoutFirst else rightLinearWithoutLast
            val views = if (animateView === leftLinear) leftViews else rightViews
            invokedFromLeft = animateView === leftLinear
            if (requiredWidth == width) {
                if (layoutWithout!!.viewWeight == 0f && width.toFloat() != abs(swipeableView!!.translationX))
                    swipeAnim.setAnimationListener(collapseListener)
                else if (collapseAnim != null && !collapseAnim!!.hasEnded()) {
                    collapseAnim!!.setAnimationListener(collapseListener)
                } else if (layoutWithout.viewWeight == 0f || width.toFloat() == abs(swipeableView!!.translationX)) {
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
            setViewWidth(leftLinearWithoutFirst!!, leftViews.size - 1)
            if (animated) {
                val swipeAnim = SwipeAnimation(leftLinear!!, 0, swipeableView!!, true)
                leftLinear!!.startAnimation(swipeAnim)
            } else {
                swipeableView!!.translationX = 0f
                setViewWidth(leftLinear!!, 0)
            }
        } else if (rightLinear != null && rightLinear!!.width > 0) {
            setViewWidth(rightLinearWithoutLast!!, rightViews.size - 1)
            if (animated) {
                val swipeAnim = SwipeAnimation(rightLinear!!, 0, swipeableView!!, false)
                rightLinear!!.startAnimation(swipeAnim)
            } else {
                swipeableView!!.translationX = 0f
                setViewWidth(rightLinear!!, 0)
            }
        }
    }

    fun setItemState(state: Int, animated: Boolean) {
        when (state) {
            ITEM_STATE_COLLAPSED -> collapseItem(animated)
            ITEM_STATE_LEFT_EXPAND -> {
                val requiredWidthLeft = leftIcons!!.size * itemWidth
                if (animated) {
                    val swipeAnim =
                        SwipeAnimation(leftLinear!!, requiredWidthLeft, swipeableView!!, true)
                    leftLinear!!.startAnimation(swipeAnim)
                } else {
                    swipeableView!!.translationX = requiredWidthLeft.toFloat()
                    setViewWidth(leftLinear!!, requiredWidthLeft)
                }
            }
            ITEM_STATE_RIGHT_EXPAND -> {
                val requiredWidthRight = rightIcons!!.size * itemWidth
                if (animated) {
                    val swipeAnim =
                        SwipeAnimation(rightLinear!!, requiredWidthRight, swipeableView!!, false)
                    rightLinear!!.startAnimation(swipeAnim)
                } else {
                    swipeableView!!.translationX = -requiredWidthRight.toFloat()
                    setViewWidth(rightLinear!!, requiredWidthRight)
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
            if (onScrollListener != null) parent.removeOnScrollListener(onScrollListener!!)
            if (autoHideSwipe) parent.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING && swipeableView!!.translationX != 0f) {
                        setItemState(ITEM_STATE_COLLAPSED, true)
                    }
                }
            }.also { onScrollListener = it })
        } else {
            Log.e(TAG, "For autoHideSwipe parent must be a RecyclerView")
        }
    }

    fun setOnlyOneSwipe(onlyOneSwipe: Boolean) {
        this.onlyOneSwipe = onlyOneSwipe
    }

    val isLeftExpanding: Boolean
        get() = swipeableView!!.translationX > 0
    val isRightExpanding: Boolean
        get() = swipeableView!!.translationX < 0
    val isExpanding: Boolean
        get() = isRightExpanding || isLeftExpanding
    val isRightExpanded: Boolean
        get() = rightLinear != null && rightLinear!!.width >= rightLayoutMaxWidth
    val isLeftExpanded: Boolean
        get() = leftLinear != null && leftLinear!!.width >= leftLayoutMaxWidth
    val isExpanded: Boolean
        get() = isLeftExpanded || isRightExpanded

    override fun onClick(view: View) {
        if (onSwipeItemClickListener != null) {
            if (leftViews != null) {
                for ((i, v) in leftViews.withIndex()) {
                    if (v === view) {
                        if (leftViews.size == 1 || leftLinearWithoutFirst!!.viewWeight > 0) {
                            onSwipeItemClickListener!!.onSwipeItemClick(true, i)
                        }
                        return
                    }
                }
            }
            if (rightViews != null) {
                for ((i, v) in rightViews.withIndex()) {
                    if (v === view) {
                        if (rightViews.size == 1 || rightLinearWithoutLast!!.viewWeight > 0) {
                            onSwipeItemClickListener!!.onSwipeItemClick(false, i)
                        }
                        break
                    }
                }
            }
        }
    }

    fun collapseAll(animated: Boolean) {
        val parent = parent
        if (parent != null && parent is RecyclerView) {
            for (item in parent.children) {
                if (item is SwipeLayout) {
                    if (item.swipeableView!!.translationX != 0f) {
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
        private const val NO_ID = 0
        private var typeface: Typeface? = null
        const val ITEM_STATE_LEFT_EXPAND = 0
        const val ITEM_STATE_RIGHT_EXPAND = 1
        const val ITEM_STATE_COLLAPSED = 2
        private const val ANIMATION_MIN_DURATION: Long = 100
        private const val ANIMATION_MAX_DURATION: Long = 300
    }

    init {
        attrs?.let { setUpAttrs(it) }
        setUpView()
    }
}

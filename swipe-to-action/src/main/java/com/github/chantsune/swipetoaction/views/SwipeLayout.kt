package com.github.chantsune.swipetoaction.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.widget.*
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.use
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.github.chantsune.swipetoaction.R
import com.github.chantsune.swipetoaction.animations.SwipeAnimation
import com.github.chantsune.swipetoaction.animations.WeightAnimation
import com.github.chantsune.swipetoaction.ktx.viewWeight
import com.github.chantsune.swipetoaction.ktx.viewWidth
import kotlin.math.abs

open class SwipeLayout(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) :
    FrameLayout(
        context, attrs, defStyleAttr, defStyleRes
    ), View.OnTouchListener {
    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        0
    )

    protected var contentLayoutId = 0
    var isSwipeEnabled = true
    var canFullSwipeRightToLeft = false
    var canFullSwipeLeftToRight = false
    var autoHideSwipe = true
        set(value) {
            field = value
            setUpAutoHide()
        }
    var onlyOneSwipe = true
    protected var itemWidth = 0

    private val rightLayoutMaxWidth: Int get() = itemWidth * rightItems.size
    private val leftLayoutMaxWidth: Int get() = itemWidth * leftItems.size
    var contentView: View = View(context).also { addView(it) }
        private set
    private val rightLinear: LinearLayout = createLinearLayout(Gravity.END).also {
        it.id = ID_RIGHT_VIEW
        addView(it)
    }
    private val leftLinear: LinearLayout = createLinearLayout(Gravity.START).also {
        it.id = ID_LEFT_VIEW
        addView(it)
    }
    private val rightLinearWithoutLast: LinearLayout = createLinearLayout(Gravity.END)
    private val leftLinearWithoutFirst: LinearLayout = createLinearLayout(Gravity.START)
    private val fullSwipeEdgePadding: Int =
        resources.getDimensionPixelSize(R.dimen.full_swipe_edge_padding)
    private var rightViews: List<View> = listOf()
    private var leftViews: List<View> = listOf()

    private var onSwipeItemClickListener: OnSwipeItemClickListener? = null
    private var onScrollListener: RecyclerView.OnScrollListener? = null

    init {
        initAttrs(attrs)
        setUpView()
    }

    fun setOnSwipeItemClickListener(listener: OnSwipeItemClickListener?) {
        onSwipeItemClickListener = listener
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setUpAutoHide()
    }

    override fun onDetachedFromWindow() {
        setItemState(ITEM_STATE_COLLAPSED, false)
        super.onDetachedFromWindow()
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (child != null && child.id !in listOf(ID_LEFT_VIEW, ID_RIGHT_VIEW)) {
            setContentView(child)
        }
        super.addView(child, index, params)
    }

    private fun setContentView(view: View) {
        if (contentView in children) {
            removeView(contentView)
        }
        contentView = view
        contentView.bringToFront()
        contentView.setOnTouchListener(this)
    }

    private fun setUpView() {

        invalidateSwipeItems()

        if (contentLayoutId != -NO_ID) {
            addView(LayoutInflater.from(context).inflate(contentLayoutId, null))
        }
    }

    private fun invalidateSwipeItems() {
        placementRightItemViewLayout()
        placementLeftItemViewLayout()
    }

    private fun placementRightItemViewLayout() {
        // clear views
        rightLinear.removeAllViews()
        rightLinearWithoutLast.removeAllViews()

        // placement item views
        rightLinear.also { rightLinear ->
            rightLinear.addView(rightLinearWithoutLast.also { linearLayout ->
                linearLayout.layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (rightViews.size - 1).toFloat()
                )
            })
        }
        for ((i, swipeItem) in rightViews.withIndex()) {
            if (i == rightViews.lastIndex) {
                rightLinear.addView(swipeItem)
            } else {
                rightLinearWithoutLast.addView(swipeItem)
            }
        }
    }

    private fun placementLeftItemViewLayout() {
        // clear views
        leftLinear.removeAllViews()
        leftLinearWithoutFirst.removeAllViews()

        // placement item views
        leftLinear.also { leftLinear ->
            for ((i, swipeItem) in leftViews.withIndex()) {
                if (i == 0) {
                    leftLinear.addView(swipeItem)
                } else {
                    leftLinearWithoutFirst.addView(swipeItem)
                }
            }
            leftLinear.addView(leftLinearWithoutFirst.also { linearLayout ->
                linearLayout.layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (leftViews.size - 1).toFloat()
                )
            })
        }
    }

    private fun setLeftSwipeItems(views: List<View>, bindSwipeItemOnClick: Boolean = true) {
        leftViews = views

        placementLeftItemViewLayout()

        if (bindSwipeItemOnClick) {
            for (item in leftItems) {
                val view = item.customView ?: item.swipeItemView
                view.isClickable = true
                view.isFocusable = true
                view.setOnClickListener {
                    onSwipeItemClickListener?.onSwipeItemClick(item)
                }
            }
        }
    }

    private fun setRightSwipeItems(views: List<View>, bindSwipeItemOnClick: Boolean = true) {
        rightViews = views

        placementRightItemViewLayout()

        if (bindSwipeItemOnClick) {
            for (item in rightItems) {
                val view = item.customView ?: item.swipeItemView
                view.isClickable = true
                view.isFocusable = true
                view.setOnClickListener {
                    onSwipeItemClickListener?.onSwipeItemClick(item)
                }
            }
        }
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        contentView.setOnClickListener(listener)
    }

    private fun createLinearLayout(gravity: Int): LinearLayout {
        return LinearLayout(context).also { linearLayout ->
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.layoutParams =
                LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, gravity)
        }
    }

    private fun initAttrs(attrs: AttributeSet?) {
        attrs ?: return
        context.obtainStyledAttributes(attrs, R.styleable.SwipeLayout).use { array ->
            contentLayoutId =
                array.getResourceId(R.styleable.SwipeLayout_layout_swipeContentLayout, NO_ID)
            itemWidth =
                array.getDimensionPixelSize(R.styleable.SwipeLayout_layout_swipeSwipeItemWidth, 100)
            canFullSwipeRightToLeft =
                array.getBoolean(R.styleable.SwipeLayout_layout_swipeCanFullSwipeRightToLeft, false)
            canFullSwipeLeftToRight =
                array.getBoolean(R.styleable.SwipeLayout_layout_swipeCanFullSwipeLeftToRight, false)
            onlyOneSwipe = array.getBoolean(R.styleable.SwipeLayout_layout_swipeOnlyOneSwipe, true)
            autoHideSwipe =
                array.getBoolean(R.styleable.SwipeLayout_layout_swipeAutoHideSwipe, true)
        }
    }

    private var prevRawX = -1f
    private var directionLeft = false
    private var movementStarted = false
    private var lastTime: Long = 0
    private var downTime: Long = 0
    private var speed = 0f
    private var downRawX = 0f
    private var downX = 0f
    private var downY = 0f

    private fun clearAnimations() {
        contentView.clearAnimation()
        rightLinear.clearAnimation()
        leftLinear.clearAnimation()
        rightLinearWithoutLast.clearAnimation()
        leftLinearWithoutFirst.clearAnimation()
    }

    private var shouldPerformLongClick = false
    private var longClickPerformed = false
    private val longClickHandler = Handler(Looper.getMainLooper())
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

    private val collapseListener: Animation.AnimationListener =
        object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                clickBySwipe()
            }
            override fun onAnimationRepeat(animation: Animation) {}
        }

    private fun clickBySwipe() {
        val position = if (invokedFromLeft) 0 else rightItems.lastIndex
        val swipeItem = if (invokedFromLeft) leftItems[position] else rightItems[position]
        onSwipeItemClickListener?.onSwipeItemClick(
            swipeItem,
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
                if (contentView.translationX == 0f) {
                    rightLinearWithoutLast.viewWeight = (rightViews.size - 1).toFloat()
                    leftLinearWithoutFirst.viewWeight = (leftViews.size - 1).toFloat()
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
                    var left = contentView.translationX - delta
                    if (left < -rightLayoutMaxWidth) {
                        if (!canFullSwipeRightToLeft) {
                            left = -rightLayoutMaxWidth.toFloat()
                        } else if (left < -width) {
                            left = -width.toFloat()
                        }
                    }
                    if (canFullSwipeRightToLeft) {
                        if (contentView.translationX <= -(width - fullSwipeEdgePadding)) {
                            if (rightLinearWithoutLast.viewWeight > 0 &&
                                (collapseAnim == null || collapseAnim!!.hasEnded())
                            ) {
                                view.isPressed = false
                                rightLinearWithoutLast.clearAnimation()
                                if (expandAnim != null) expandAnim = null
                                collapseAnim = WeightAnimation(0f, rightLinearWithoutLast)
                                Log.d("WeightAnim", "onTouch - Collapse")
                                startAnimation(collapseAnim)
                            }
                        } else {
                            if (rightLinearWithoutLast.viewWeight < rightViews.size - 1f &&
                                (expandAnim == null || expandAnim!!.hasEnded())
                            ) {
                                Log.d("WeightAnim", "onTouch - Expand")
                                view.isPressed = false
                                rightLinearWithoutLast.clearAnimation()
                                if (collapseAnim != null) collapseAnim = null
                                expandAnim = WeightAnimation(
                                    (rightViews.size - 1).toFloat(),
                                    rightLinearWithoutLast
                                )
                                startAnimation(expandAnim)
                            }
                        }
                    }
                    contentView.translationX = left
                    if (left > 0) {
                        val leftLayoutWidth = abs(contentView.translationX).toInt()
                        leftLinear.viewWidth = leftLayoutWidth
                    } else {
                        val rightLayoutWidth = abs(left).toInt()
                        rightLinear.viewWidth = rightLayoutWidth
                    }
                } else {
                    var right = contentView.translationX + delta
                    if (right > leftLayoutMaxWidth) {
                        if (!canFullSwipeLeftToRight) {
                            right = leftLayoutMaxWidth.toFloat()
                        } else if (right >= width) {
                            right = width.toFloat()
                        }
                    }
                    if (canFullSwipeLeftToRight) {
                        if (contentView.translationX >= width - fullSwipeEdgePadding) {
                            if (leftLinearWithoutFirst.viewWeight > 0 &&
                                (collapseAnim == null || collapseAnim!!.hasEnded())
                            ) {
                                leftLinearWithoutFirst.clearAnimation()
                                if (expandAnim != null) expandAnim = null
                                collapseAnim = WeightAnimation(0f, leftLinearWithoutFirst)
                                startAnimation(collapseAnim)
                            }
                        } else {
                            if (leftLinearWithoutFirst.viewWeight < leftViews.size - 1f &&
                                (expandAnim == null || expandAnim!!.hasEnded())
                            ) {
                                leftLinearWithoutFirst.clearAnimation()
                                if (collapseAnim != null) collapseAnim = null
                                expandAnim = WeightAnimation(
                                    (leftViews.size - 1).toFloat(),
                                    leftLinearWithoutFirst
                                )
                                startAnimation(expandAnim)
                            }
                        }
                    }
                    contentView.translationX = right
                    if (right > 0) {
                        val leftLayoutWidth = abs(right).toInt()
                        leftLinear.viewWidth = leftLayoutWidth
                    } else {
                        val rightLayoutWidth = abs(contentView.translationX).toInt()
                        rightLinear.viewWidth = rightLayoutWidth
                    }
                }
                if (abs(contentView.translationX) > itemWidth / 5) {
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
                    if (item.contentView.translationX != 0f && !item.inAnimatedState()) {
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

    private var invokedFromLeft = false

    private fun finishSwipeAnimated() {
        shouldPerformLongClick = false
        isPressed = false
        parent.requestDisallowInterceptTouchEvent(false)
        movementStarted = false
        var animateView: LinearLayout? = null
        var left = false
        var requiredWidth = 0
        if (contentView.translationX > 0) {
            animateView = leftLinear
            left = true
            val reqWidth =
                if (directionLeft) leftLayoutMaxWidth - leftLayoutMaxWidth / 3 else leftLayoutMaxWidth / 3
            rightLinear.viewWidth = 0
            if (leftLinear.width >= reqWidth) {
                requiredWidth = leftLayoutMaxWidth
            }
            if (requiredWidth == leftLayoutMaxWidth && !directionLeft) {
                if (contentView.translationX >= width - fullSwipeEdgePadding) {
                    requiredWidth = width
                    invokedFromLeft = true
                }
            }
            contentView.translationX = leftLinear.width.toFloat()
        } else if (contentView.translationX < 0) {
            left = false
            animateView = rightLinear
            leftLinear.viewWidth = 0
            val reqWidth =
                if (directionLeft) rightLayoutMaxWidth / 3 else rightLayoutMaxWidth - rightLayoutMaxWidth / 3
            if (rightLinear.width >= reqWidth) {
                requiredWidth = rightLayoutMaxWidth
            }
            if (requiredWidth == rightLayoutMaxWidth && directionLeft) {
                if (contentView.translationX <= -(width - fullSwipeEdgePadding)) {
                    requiredWidth = width
                    invokedFromLeft = false
                }
            }
            contentView.translationX = -rightLinear.width.toFloat()
        }
        val duration = (100 * speed).toLong()
            .coerceAtLeast(ANIMATION_MIN_DURATION)
            .coerceAtMost(ANIMATION_MAX_DURATION)
        if (animateView != null) {
            val swipeAnim = SwipeAnimation(animateView, requiredWidth, contentView, left)
            swipeAnim.duration = duration
            val layoutWithout =
                if (animateView === leftLinear) leftLinearWithoutFirst else rightLinearWithoutLast
            val views = if (animateView === leftLinear) leftViews else rightViews
            invokedFromLeft = animateView === leftLinear
            if (requiredWidth == width) {
                if (layoutWithout.viewWeight == 0f && width.toFloat() != abs(contentView.translationX))
                    swipeAnim.setAnimationListener(collapseListener)
                else if (collapseAnim != null && !collapseAnim!!.hasEnded()) {
                    collapseAnim!!.setAnimationListener(collapseListener)
                } else if (layoutWithout.viewWeight == 0f || width.toFloat() == abs(contentView.translationX)) {
                    clickBySwipe()
                } else {
                    layoutWithout.clearAnimation()
                    if (collapseAnim != null) collapseAnim!!.cancel()
                    collapseAnim = WeightAnimation(0f, layoutWithout)
                    collapseAnim!!.setAnimationListener(collapseListener)
                    layoutWithout.startAnimation(collapseAnim)
                }
            } else {
                val weightAnimation = WeightAnimation((views.size - 1).toFloat(), layoutWithout)
                layoutWithout.startAnimation(weightAnimation)
            }
            animateView.startAnimation(swipeAnim)
        }
    }

    private fun collapseItem(animated: Boolean, animationListener: Animation.AnimationListener?) {
        if (leftLinear.width > 0) {
            leftLinearWithoutFirst.viewWidth = leftViews.size - 1
            if (animated) {
                val swipeAnim = SwipeAnimation(leftLinear, 0, contentView, true)
                if (animationListener != null) swipeAnim.setAnimationListener(animationListener)
                leftLinear.startAnimation(swipeAnim)
            } else {
                contentView.translationX = 0f
                leftLinear.viewWidth = 0
            }
        } else if (rightLinear.width > 0) {
            rightLinearWithoutLast.viewWidth = rightViews.size - 1
            if (animated) {
                val swipeAnim = SwipeAnimation(rightLinear, 0, contentView, false)
                if (animationListener != null) swipeAnim.setAnimationListener(animationListener)
                rightLinear.startAnimation(swipeAnim)
            } else {
                contentView.translationX = 0f
                rightLinear.viewWidth = 0
            }
        }
    }

    private fun expandRightItem(
        animated: Boolean,
        animationListener: Animation.AnimationListener?
    ) {
        val requiredWidthRight = rightLayoutMaxWidth
        if (animated) {
            val swipeAnim =
                SwipeAnimation(rightLinear, requiredWidthRight, contentView, false)
            if (animationListener != null) swipeAnim.setAnimationListener(animationListener)
            rightLinear.startAnimation(swipeAnim)
        } else {
            contentView.translationX = -requiredWidthRight.toFloat()
            rightLinear.viewWidth = requiredWidthRight
        }
    }

    private fun expandLeftItem(animated: Boolean, animationListener: Animation.AnimationListener?) {
        val requiredWidthLeft = leftLayoutMaxWidth
        if (animated) {
            val swipeAnim =
                SwipeAnimation(leftLinear, requiredWidthLeft, contentView, true)
            if (animationListener != null) swipeAnim.setAnimationListener(animationListener)
            leftLinear.startAnimation(swipeAnim)
        } else {
            contentView.translationX = requiredWidthLeft.toFloat()
            leftLinear.viewWidth = requiredWidthLeft
        }
    }

    fun setItemState(
        state: Int,
        animated: Boolean,
        animationListener: Animation.AnimationListener? = null
    ) {
        when (state) {
            ITEM_STATE_COLLAPSED -> collapseItem(animated, animationListener)
            ITEM_STATE_LEFT_EXPAND -> expandLeftItem(animated, animationListener)
            ITEM_STATE_RIGHT_EXPAND -> expandRightItem(animated, animationListener)
        }
    }

    fun inAnimatedState(): Boolean {
        leftLinear.animation?.also { anim ->
            if (!anim.hasEnded()) return true
        }
        rightLinear.animation?.also { anim ->
            if (!anim.hasEnded()) return true
        }
        return false
    }

    private fun setUpAutoHide() {
        val parent = parent
        if (parent != null && parent is RecyclerView) {
            onScrollListener?.let { parent.removeOnScrollListener(it) }
            if (autoHideSwipe) {
                parent.addOnScrollListener(object :
                    RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (newState == RecyclerView.SCROLL_STATE_DRAGGING && contentView.translationX != 0f) {
                            setItemState(ITEM_STATE_COLLAPSED, true)
                        }
                    }
                }.also { onScrollListener = it })
            }
        } else {
            Log.e(TAG, "For autoHideSwipe parent must be a RecyclerView")
        }
    }


    val isLeftExpanding: Boolean
        get() = contentView.translationX > 0
    val isRightExpanding: Boolean
        get() = contentView.translationX < 0
    val isExpanding: Boolean
        get() = isRightExpanding || isLeftExpanding
    val isRightExpanded: Boolean
        get() = rightLinear.width >= rightLayoutMaxWidth
    val isLeftExpanded: Boolean
        get() = leftLinear.width >= leftLayoutMaxWidth
    val isExpanded: Boolean
        get() = isLeftExpanded || isRightExpanded

    fun collapseAll(animated: Boolean) {
        val parent = parent
        if (parent != null && parent is RecyclerView) {
            for (item in parent.children) {
                if (item is SwipeLayout) {
                    if (item.contentView.translationX != 0f) {
                        item.setItemState(ITEM_STATE_COLLAPSED, animated)
                    }
                }
            }
        }
    }

    fun interface OnSwipeItemClickListener {
        fun onSwipeItemClick(swipeItem: SwipeItem)
    }

    private val rightItems: MutableList<SwipeItem> = mutableListOf()
    private val leftItems: MutableList<SwipeItem> = mutableListOf()

    val rightSwipeItemCount: Int get() = rightItems.size
    val leftSwipeItemCount: Int get() = leftItems.size

    private fun updateItemPosition(items: List<SwipeItem>) {
        for ((i, item) in items.withIndex()) {
            item.position = i
        }
    }

    fun newSwipeItem(left: Boolean): SwipeItem {
        return SwipeItem(context, left = left, itemWidth = itemWidth)
    }

    fun addSwipeItem(swipeItem: SwipeItem) {
        swipeItem.update(this)
        if (swipeItem.left) {
            leftItems.add(swipeItem)
            setLeftSwipeItems(leftItems.map { it.customView ?: it.swipeItemView })
            updateItemPosition(leftItems)
        } else {
            rightItems.add(swipeItem)
            setRightSwipeItems(rightItems.map { it.customView ?: it.swipeItemView })
            updateItemPosition(rightItems)
        }
    }

    fun removeSwipeItem(swipeItem: SwipeItem) {
        if (swipeItem.left) {
            leftItems.remove(swipeItem)
            setLeftSwipeItems(leftItems.map { it.customView ?: it.swipeItemView })
        } else {
            rightItems.remove(swipeItem)
            setRightSwipeItems(rightItems.map { it.customView ?: it.swipeItemView })
        }
    }

    fun getSwipeItemAt(position: Int, left: Boolean): SwipeItem {
        return if (left) leftItems[position] else rightItems[position]
    }

    class SwipeItem(
        private val context: Context,
        val left: Boolean,
        icon: Drawable? = null,
        backgroundColor: Int? = null,
        text: CharSequence? = null,
        // internal params
        val itemWidth: Int,
        iconSize: Int = 100,
    ) {
        var position: Int = INVALID_ITEM_POSITION
        internal set

        var icon: Drawable? = icon
            set(value) {
                field = value
                update()
            }
        fun setIcon(@DrawableRes id: Int) {
            icon = ContextCompat.getDrawable(context, id)
        }
        var backgroundColor: Int? = backgroundColor
            set(value) {
                field = value
                update()
            }
        var text: CharSequence? = text
            set(value) {
                field = value
                update()
            }
        fun setText(@StringRes id: Int) {
            text = context.getText(id)
        }
        fun setTextColor(@ColorInt color: Int) {
            swipeItemView.textView.setTextColor(color)
        }
        // internal params
        var iconSize: Int = iconSize
            set(value) {
                field = value
                update()
            }
        var textSize: Float
            get() = swipeItemView.textView.textSize
            set(value) {
                swipeItemView.textView.textSize = value
            }
        fun setTextSize(unit: Int, size: Float) {
            swipeItemView.textView.setTextSize(unit, size)
        }

        internal val swipeItemView: SwipeItemView = SwipeItemView(context)
        val view: View get() = swipeItemView

        var customView: View? = null

        fun setCustomView(@LayoutRes resource: Int) {
            customView = LayoutInflater.from(context).inflate(resource, null)
        }

        @SuppressLint("ClickableViewAccessibility")
        internal fun update(l: View.OnTouchListener) {
            customView?.setOnTouchListener(l) ?: kotlin.run {
                swipeItemView.update(this)
                swipeItemView.setOnTouchListener(l)
            }
        }

        private fun update() {
            swipeItemView.update(this)
        }
        companion object {
            const val INVALID_ITEM_POSITION = -1
        }
    }

    companion object {
        val TAG = SwipeLayout::class.simpleName
        const val NO_ID = 0
        const val ITEM_STATE_LEFT_EXPAND = 0
        const val ITEM_STATE_RIGHT_EXPAND = 1
        const val ITEM_STATE_COLLAPSED = 2
        private const val ANIMATION_MIN_DURATION: Long = 100
        private const val ANIMATION_MAX_DURATION: Long = 300
        private const val ID_RIGHT_VIEW = 1507
        private const val ID_LEFT_VIEW = ID_RIGHT_VIEW + 1
    }
}

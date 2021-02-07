package com.github.chantsune.swipetoaction.views

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.animation.AccelerateInterpolator
import androidx.core.content.res.use
import androidx.core.view.NestedScrollingParent
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.customview.widget.ViewDragHelper
import com.github.chantsune.swipetoaction.R
import com.github.chantsune.swipetoaction.extensions.iterator
import java.lang.ref.WeakReference
import java.util.*
import kotlin.math.abs

class SwipeLayout : ViewGroup {
    private lateinit var dragHelper: ViewDragHelper
    var leftView: View? = null
    var rightView: View? = null
    var centerView: View? = null
    private var velocityThreshold = 0f
    private var touchSlop = 0f
    private var swipeListener: OnSwipeListener? = null
    private var weakAnimator: WeakReference<ObjectAnimator>? = null
    private val hackedParents: MutableMap<View, Boolean> = WeakHashMap()

    /**
     * Enable or disable swipe gesture from left side
     */
    var leftSwipeEnabled = true

    /**
     * Enable or disable swipe gesture from right side
     */
    var rightSwipeEnabled = true
    private var touchState = TouchState.WAIT
    private var touchX = 0f
    private var touchY = 0f

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        dragHelper = ViewDragHelper.create(this, 1f, dragCallback)
        velocityThreshold = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            VELOCITY_THRESHOLD,
            resources.displayMetrics
        )
        touchSlop = ViewConfiguration.get(getContext()).scaledTouchSlop.toFloat()
        if (attrs != null) {
            context.obtainStyledAttributes(attrs, R.styleable.SwipeLayout).use { a ->
                if (a.hasValue(R.styleable.SwipeLayout_swipe_enabled)) {
                    leftSwipeEnabled = a.getBoolean(R.styleable.SwipeLayout_swipe_enabled, true)
                    rightSwipeEnabled = a.getBoolean(R.styleable.SwipeLayout_swipe_enabled, true)
                }
                if (a.hasValue(R.styleable.SwipeLayout_left_swipe_enabled)) {
                    leftSwipeEnabled =
                        a.getBoolean(R.styleable.SwipeLayout_left_swipe_enabled, true)
                }
                if (a.hasValue(R.styleable.SwipeLayout_right_swipe_enabled)) {
                    rightSwipeEnabled =
                        a.getBoolean(R.styleable.SwipeLayout_right_swipe_enabled, true)
                }
            }
        }
    }

    fun setOnSwipeListener(swipeListener: OnSwipeListener?) {
        this.swipeListener = swipeListener
    }

    /**
     * reset swipe-layout state to initial position
     */
    fun reset() {
        centerView?.let {
            finishAnimator()
            dragHelper.abort()
            offsetChildren(null, -it.left)
        }
    }

    /**
     * reset swipe-layout state to initial position with animation (200ms)
     */
    fun animateReset() {
        centerView?.let {
            runAnimation(it.left, 0)
        }
    }

    /**
     * Swipe with animation to left by right view's width
     *
     *
     * Ignores [SwipeLayout.swipeEnabled] and [SwipeLayout.leftSwipeEnabled]
     */
    fun animateSwipeLeft() {
        if (centerView != null && rightView != null) {
            val target = -rightView!!.width
            runAnimation(offset, target)
        }
    }

    /**
     * Swipe with animation to right by left view's width
     *
     *
     * Ignores [SwipeLayout.swipeEnabled] and [SwipeLayout.rightSwipeEnabled]
     */
    fun animateSwipeRight() {
        if (centerView != null && leftView != null) {
            val target = leftView!!.width
            runAnimation(offset, target)
        }
    }

    private fun runAnimation(initialX: Int, targetX: Int) {
        finishAnimator()
        dragHelper.abort()
        val animator = ObjectAnimator()
        animator.target = this
        animator.setPropertyName("offset")
        animator.interpolator = AccelerateInterpolator()
        animator.setIntValues(initialX, targetX)
        animator.duration = 200
        animator.start()
        weakAnimator = WeakReference(animator)
    }

    private fun finishAnimator() {
        weakAnimator?.let { weakAnimator ->
            weakAnimator.get()?.let { animator ->
                weakAnimator.clear()
                if (animator.isRunning) {
                    animator.end()
                }
            }
        }
    }

    /**
     * horizontal offset from initial position
     */
    var offset: Int
        get() = centerView?.left ?: 0
        set(value) {
            centerView?.let {
                offsetChildren(null, value - it.left)
            }
        }

    /**
     * enable or disable swipe gesture handling
     */
    var swipeEnabled: Boolean
        get() = leftSwipeEnabled || rightSwipeEnabled
        set(value) {
            leftSwipeEnabled = value
            rightSwipeEnabled = value
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        var maxHeight = 0

        // Find out how big everyone wants to be
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            measureChildren(widthMeasureSpec, heightMeasureSpec)
        } else {
            //find a child with biggest height
            for (child in children) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec)
                maxHeight = maxHeight.coerceAtLeast(child.measuredHeight)
            }
            if (maxHeight > 0) {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY)
                measureChildren(widthMeasureSpec, heightMeasureSpec)
            }
        }

        // Find rightmost and bottom-most child
        for (child in children) {
            if (child.visibility != GONE) {
                val childBottom: Int = child.measuredHeight
                maxHeight = maxHeight.coerceAtLeast(childBottom)
            }
        }
        maxHeight += paddingTop + paddingBottom
        maxHeight = maxHeight.coerceAtLeast(suggestedMinimumHeight)
        setMeasuredDimension(
            resolveSize(suggestedMinimumWidth, widthMeasureSpec),
            resolveSize(maxHeight, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        layoutChildren(left, top, right, bottom)
    }

    private fun layoutChildren(left: Int, top: Int, right: Int, bottom: Int) {
        val parentTop = paddingTop
        for (child in children) {
            if (child.visibility == GONE) continue
            val lp = child.layoutParams as LayoutParams
            when (lp.gravity) {
                LayoutParams.GRAVITY_CENTER -> centerView = child
                LayoutParams.GRAVITY_LEFT -> leftView = child
                LayoutParams.GRAVITY_RIGHT -> rightView = child
            }
        }
        val centerView = centerView ?: throw RuntimeException("Center view must be added")
        for (child in children) {
            if (child.visibility != GONE) {
                val lp = child.layoutParams as LayoutParams
                val orientation = lp.gravity
                val width = child.measuredWidth
                val height = child.measuredHeight
                val childLeft = when (orientation) {
                    LayoutParams.GRAVITY_LEFT -> centerView.left - width
                    LayoutParams.GRAVITY_RIGHT -> centerView.right
                    LayoutParams.GRAVITY_CENTER -> child.left
                    else -> child.left
                }
                val childTop: Int = parentTop
                child.layout(childLeft, childTop, childLeft + width, childTop + height)
            }
        }
    }

    private val dragCallback: ViewDragHelper.Callback = object : ViewDragHelper.Callback() {
        private var initLeft = 0
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            initLeft = child.left
            return true
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return if (dx > 0) {
                clampMoveRight(child, left)
            } else {
                clampMoveLeft(child, left)
            }
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return width
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            Log.d(TAG, "VELOCITY $xvel; THRESHOLD $velocityThreshold")
            val dx = releasedChild.left - initLeft
            if (dx == 0) return
            val handled: Boolean = if (dx > 0) {
                if (xvel >= 0) onMoveRightReleased(releasedChild, dx, xvel)
                else onMoveLeftReleased(releasedChild, dx, xvel)
            } else {
                if (xvel <= 0) onMoveLeftReleased(releasedChild, dx, xvel)
                else onMoveRightReleased(releasedChild, dx, xvel)
            }
            if (!handled) {
                // go back to center
                startScrollAnimation(
                    releasedChild,
                    releasedChild.left - centerView!!.left,
                    false,
                    dx > 0
                )
            }
        }

        private fun leftViewClampReached(leftViewLP: LayoutParams): Boolean {
            return leftView?.let { view ->
                when (leftViewLP.clamp) {
                    LayoutParams.CLAMP_PARENT -> view.right >= width
                    LayoutParams.CLAMP_SELF -> view.right >= view.width
                    else -> view.right >= leftViewLP.clamp
                }
            } ?: false
        }

        private fun rightViewClampReached(lp: LayoutParams): Boolean {
            return rightView?.let { view ->
                when (lp.clamp) {
                    LayoutParams.CLAMP_PARENT -> view.right <= width
                    LayoutParams.CLAMP_SELF -> view.right <= width
                    else -> view.left + lp.clamp <= width
                }
            } ?: false
        }

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
            offsetChildren(changedView, dx)
            if (swipeListener == null) return
            if (dx > 0) {
                //move to right
                leftView?.let { leftView ->
                    val stickyBound = getStickyBound(leftView)
                    if (stickyBound != LayoutParams.STICKY_NONE) {
                        if (leftView.right - stickyBound > 0 && leftView.right - stickyBound - dx <= 0) {
                            swipeListener?.onLeftStickyEdge(this@SwipeLayout, true)
                        }
                    }
                }
                rightView?.let { rightView ->
                    val stickyBound = getStickyBound(rightView)
                    if (stickyBound != LayoutParams.STICKY_NONE) {
                        if (rightView.left + stickyBound > width && rightView.left + stickyBound - dx <= width) {
                            swipeListener?.onRightStickyEdge(this@SwipeLayout, true)
                        }
                    }
                }
            } else if (dx < 0) {
                //move to left
                leftView?.let { leftView ->
                    val stickyBound = getStickyBound(leftView)
                    if (stickyBound != LayoutParams.STICKY_NONE) {
                        if (leftView.right - stickyBound <= 0 && leftView.right - stickyBound - dx > 0) {
                            swipeListener?.onLeftStickyEdge(this@SwipeLayout, false)
                        }
                    }
                }
                rightView?.let { rightView ->
                    val stickyBound = getStickyBound(rightView)
                    if (stickyBound != LayoutParams.STICKY_NONE) {
                        if (rightView.left + stickyBound <= width && rightView.left + stickyBound - dx > width) {
                            swipeListener?.onRightStickyEdge(this@SwipeLayout, false)
                        }
                    }
                }
            }
        }

        private fun getStickyBound(view: View): Int {
            val lp = getLayoutParams(view)
            if (lp.sticky == LayoutParams.STICKY_NONE) return LayoutParams.STICKY_NONE
            return if (lp.sticky == LayoutParams.STICKY_SELF) view.width else lp.sticky
        }

        private fun clampMoveRight(child: View, left: Int): Int {
            if (leftView == null) {
                return if (child === centerView) left.coerceAtMost(0) else left.coerceAtMost(width)
            }
            val lp = getLayoutParams(
                leftView!!
            )
            return when (lp.clamp) {
                LayoutParams.CLAMP_PARENT -> left.coerceAtMost(width + child.left - leftView!!.right)
                LayoutParams.CLAMP_SELF -> left.coerceAtMost(child.left - leftView!!.left)
                else -> left.coerceAtMost(child.left - leftView!!.right + lp.clamp)
            }
        }

        private fun clampMoveLeft(child: View, left: Int): Int {
            if (rightView == null) {
                return if (child === centerView) left.coerceAtLeast(0) else left.coerceAtLeast(-child.width)
            }
            val lp = getLayoutParams(
                rightView!!
            )
            return when (lp.clamp) {
                LayoutParams.CLAMP_PARENT -> (child.left - rightView!!.left).coerceAtLeast(left)
                LayoutParams.CLAMP_SELF -> left.coerceAtLeast(width - rightView!!.left + child.left - rightView!!.width)
                else -> left.coerceAtLeast(width - rightView!!.left + child.left - lp.clamp)
            }
        }

        private fun onMoveRightReleased(child: View, dx: Int, xvel: Float): Boolean {
            if (xvel > velocityThreshold) {
                val left = if (centerView!!.left < 0) child.left - centerView!!.left else width
                val moveToOriginal = centerView!!.left < 0
                startScrollAnimation(child, clampMoveRight(child, left), !moveToOriginal, true)
                return true
            }
            if (leftView == null) {
                startScrollAnimation(child, child.left - centerView!!.left, false, true)
                return true
            }
            val lp = getLayoutParams(
                leftView!!
            )
            if (dx > 0 && xvel >= 0 && leftViewClampReached(lp)) {
                swipeListener?.onSwipeClampReached(this@SwipeLayout, true)
                return true
            }
            if (dx > 0 && xvel >= 0 && lp.bringToClamp != LayoutParams.BRING_TO_CLAMP_NO && leftView!!.right > lp.bringToClamp) {
                val left = if (centerView!!.left < 0) child.left - centerView!!.left else width
                startScrollAnimation(child, clampMoveRight(child, left), true, true)
                return true
            }
            if (lp.sticky != LayoutParams.STICKY_NONE) {
                val stickyBound =
                    if (lp.sticky == LayoutParams.STICKY_SELF) leftView!!.width else lp.sticky
                val amplitude = stickyBound * lp.stickySensitivity
                if (isBetween(-amplitude, amplitude, (centerView!!.left - stickyBound).toFloat())) {
                    val toClamp =
                        lp.clamp == LayoutParams.CLAMP_SELF && stickyBound == leftView!!.width || lp.clamp == stickyBound ||
                                lp.clamp == LayoutParams.CLAMP_PARENT && stickyBound == width
                    startScrollAnimation(
                        child,
                        child.left - centerView!!.left + stickyBound,
                        toClamp,
                        true
                    )
                    return true
                }
            }
            return false
        }

        private fun onMoveLeftReleased(child: View, dx: Int, xvel: Float): Boolean {
            if (-xvel > velocityThreshold) {
                val left = if (centerView!!.left > 0) child.left - centerView!!.left else -width
                val moveToOriginal = centerView!!.left > 0
                startScrollAnimation(child, clampMoveLeft(child, left), !moveToOriginal, false)
                return true
            }
            if (rightView == null) {
                startScrollAnimation(child, child.left - centerView!!.left, false, false)
                return true
            }
            val lp = getLayoutParams(
                rightView!!
            )
            if (dx < 0 && xvel <= 0 && rightViewClampReached(lp)) {
                swipeListener?.onSwipeClampReached(this@SwipeLayout, false)
                return true
            }
            if (dx < 0 && xvel <= 0 && lp.bringToClamp != LayoutParams.BRING_TO_CLAMP_NO && rightView!!.left + lp.bringToClamp < width) {
                val left = if (centerView!!.left > 0) child.left - centerView!!.left else -width
                startScrollAnimation(child, clampMoveLeft(child, left), true, false)
                return true
            }
            if (lp.sticky != LayoutParams.STICKY_NONE) {
                val stickyBound =
                    if (lp.sticky == LayoutParams.STICKY_SELF) rightView!!.width else lp.sticky
                val amplitude = stickyBound * lp.stickySensitivity
                if (isBetween(
                        -amplitude,
                        amplitude,
                        (centerView!!.right + stickyBound - width).toFloat()
                    )
                ) {
                    val toClamp =
                        lp.clamp == LayoutParams.CLAMP_SELF && stickyBound == rightView!!.width || lp.clamp == stickyBound ||
                                lp.clamp == LayoutParams.CLAMP_PARENT && stickyBound == width
                    startScrollAnimation(
                        child,
                        child.left - rightView!!.left + width - stickyBound,
                        toClamp,
                        false
                    )
                    return true
                }
            }
            return false
        }

        private fun isBetween(left: Float, right: Float, check: Float): Boolean {
            return check in left..right
        }
    }

    private fun startScrollAnimation(
        view: View,
        targetX: Int,
        moveToClamp: Boolean,
        toRight: Boolean
    ) {
        if (dragHelper.settleCapturedViewAt(targetX, view.top)) {
            ViewCompat.postOnAnimation(view, SettleRunnable(view, moveToClamp, toRight))
        } else if (moveToClamp) {
            swipeListener?.onSwipeClampReached(this@SwipeLayout, toRight)
        }
    }

    private fun getLayoutParams(view: View): LayoutParams {
        return view.layoutParams as LayoutParams
    }

    private fun offsetChildren(skip: View?, dx: Int) {
        if (dx == 0) return
        for (child in children) {
            if (child === skip) continue
            child.offsetLeftAndRight(dx)
            invalidate(child.left, child.top, child.right, child.bottom)
        }
    }

    private fun hackParents() {
        var parent = parent
        while (parent != null) {
            if (parent is NestedScrollingParent) {
                val view = parent as View
                hackedParents[view] = view.isEnabled
            }
            parent = parent.parent
        }
    }

    private fun unHackParents() {
        for ((view, value) in hackedParents) {
            view.isEnabled = value
        }
        hackedParents.clear()
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (swipeEnabled) internalOnInterceptTouchEvent(event)
        else super.onInterceptTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val defaultResult = super.onTouchEvent(event)
        if (!swipeEnabled) {
            return defaultResult
        }
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> onTouchBegin(event)
            MotionEvent.ACTION_MOVE -> if (touchState == TouchState.WAIT) {
                val dx = abs(event.x - touchX)
                val dy = abs(event.y - touchY)
                val isLeftToRight = event.x - touchX > 0
                if ((isLeftToRight && !leftSwipeEnabled || !isLeftToRight && !rightSwipeEnabled)
                    &&
                    offset == 0
                ) {
                    return defaultResult
                }
                if (dx >= touchSlop || dy >= touchSlop) {
                    touchState =
                        if (dy == 0f || dx / dy > 1f) TouchState.SWIPE else TouchState.SKIP
                    if (touchState == TouchState.SWIPE) {
                        requestDisallowInterceptTouchEvent(true)
                        hackParents()
                        swipeListener?.onBeginSwipe(this, event.x > touchX)
                    }
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (touchState == TouchState.SWIPE) {
                    unHackParents()
                    requestDisallowInterceptTouchEvent(false)
                }
                touchState = TouchState.WAIT
            }
        }
        if (event.actionMasked != MotionEvent.ACTION_MOVE || touchState == TouchState.SWIPE) {
            dragHelper.processTouchEvent(event)
        }
        return true
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): LayoutParams {
        return LayoutParams(p)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is LayoutParams
    }

    private fun internalOnInterceptTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            onTouchBegin(event)
        }
        return dragHelper.shouldInterceptTouchEvent(event)
    }

    private fun onTouchBegin(event: MotionEvent) {
        touchState = TouchState.WAIT
        touchX = event.x
        touchY = event.y
    }

    private inner class SettleRunnable(
        private val view: View,
        private val moveToClamp: Boolean,
        private val moveToRight: Boolean
    ) : Runnable {
        override fun run() {
            if (dragHelper.continueSettling(true)) {
                ViewCompat.postOnAnimation(view, this)
            } else {
                Log.d(TAG, "ONSWIPE clamp: $moveToClamp ; moveToRight: $moveToRight")
                if (moveToClamp) {
                    swipeListener?.onSwipeClampReached(this@SwipeLayout, moveToRight)
                }
            }
        }
    }

    class LayoutParams : ViewGroup.LayoutParams {
        var gravity = GRAVITY_CENTER
        var sticky = 0
        var stickySensitivity = DEFAULT_STICKY_SENSITIVITY
        var clamp = CLAMP_SELF
        var bringToClamp = BRING_TO_CLAMP_NO

        constructor(c: Context, attrs: AttributeSet) : super(c, attrs) {
            c.obtainStyledAttributes(attrs, R.styleable.SwipeLayout).use { a ->
                for (attr in a) {
                    when (attr) {
                        R.styleable.SwipeLayout_gravity -> {
                            gravity = a.getInt(attr, GRAVITY_CENTER)
                        }
                        R.styleable.SwipeLayout_sticky -> {
                            sticky = a.getLayoutDimension(attr, STICKY_SELF)
                        }
                        R.styleable.SwipeLayout_clamp -> {
                            clamp = a.getLayoutDimension(attr, CLAMP_SELF)
                        }
                        R.styleable.SwipeLayout_bring_to_clamp -> {
                            bringToClamp = a.getLayoutDimension(attr, BRING_TO_CLAMP_NO)
                        }
                        R.styleable.SwipeLayout_sticky_sensitivity -> {
                            stickySensitivity = a.getFloat(attr, DEFAULT_STICKY_SENSITIVITY)
                        }
                    }
                }
            }
        }

        constructor(source: ViewGroup.LayoutParams) : super(source)
        constructor(width: Int, height: Int) : super(width, height)

        companion object {
            const val GRAVITY_LEFT = -1
            const val GRAVITY_RIGHT = 1
            const val GRAVITY_CENTER = 0
            const val CLAMP_PARENT = -1
            const val CLAMP_SELF = -2
            const val BRING_TO_CLAMP_NO = -1
            const val STICKY_SELF = -1
            const val STICKY_NONE = -2
            const val DEFAULT_STICKY_SENSITIVITY = 0.4f
        }
    }

    interface OnSwipeListener {
        fun onBeginSwipe(swipeLayout: SwipeLayout, moveToRight: Boolean)
        fun onSwipeClampReached(swipeLayout: SwipeLayout, moveToRight: Boolean)
        fun onLeftStickyEdge(swipeLayout: SwipeLayout, moveToRight: Boolean)
        fun onRightStickyEdge(swipeLayout: SwipeLayout, moveToRight: Boolean)
    }

    private enum class TouchState {
        WAIT, SWIPE, SKIP
    }

    companion object {
        private val TAG = SwipeLayout::class.java.simpleName
        private const val VELOCITY_THRESHOLD = 1500f
    }
}

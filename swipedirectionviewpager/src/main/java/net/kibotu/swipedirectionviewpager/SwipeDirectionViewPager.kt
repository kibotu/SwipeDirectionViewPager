package net.kibotu.swipedirectionviewpager

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import java.util.*

/**
 * Created by [Jan Rabe](https://about.me/janrabe).
 *
 *
 * source: [how-to-disable-viewpager-from-swiping-in-one-direction](https://stackoverflow.com/questions/19602369/how-to-disable-viewpager-from-swiping-in-one-direction/34076649#34076649)
 */
class SwipeDirectionViewPager : ViewPager, LogTag {

    private val uuid = UUID.randomUUID().toString()

    private val SWIPE_THRESH_HOLD = 25.dpToPx()

    private var initialXValue: Float = 0f

    private var direction: SwipeDirection? = null

    val swipeCallbacks: List<SwipeCallback> = ArrayList()

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize()
    }

    private fun initialize() {
        direction = SwipeDirection.ALL
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return isSwipeAllowed(event) && super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return isSwipeAllowed(event) && super.onInterceptTouchEvent(event)
    }

    private fun isSwipeAllowed(event: MotionEvent): Boolean {
        if (this.direction == SwipeDirection.ALL) return true

        // disable any swipe
        if (direction == SwipeDirection.NONE)
            return false

        if (event.action == MotionEvent.ACTION_DOWN) {
            initialXValue = event.x
            return true
        }

        if (event.action == MotionEvent.ACTION_MOVE) {
            try {
                val diffX = event.x - initialXValue
                if (diffX > SWIPE_THRESH_HOLD && direction == SwipeDirection.RIGHT) {
                    // swipe from left to right detected
                    for (swipeCallback in swipeCallbacks)
                        swipeCallback.swipe(SwipeDirection.RIGHT)

                    return false

                } else if (diffX < -SWIPE_THRESH_HOLD && direction == SwipeDirection.LEFT) {
                    // swipe from right to left detected
                    for (swipeCallback in swipeCallbacks)
                        swipeCallback.swipe(SwipeDirection.LEFT)

                    return false
                }
            } catch (exception: Exception) {
                if (enableLogging)
                    exception.printStackTrace()
            }

        }

        return true
    }

    fun setAllowedSwipeDirection(direction: SwipeDirection) {
        if (this.direction != direction)
            log("[setAllowedSwipeDirection] " + direction)
        this.direction = direction
    }

    override fun tag(): String {
        return javaClass.simpleName + "[" + uuid.substring(0, 8) + "]"
    }
}

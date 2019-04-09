package net.kibotu.swipedirectionviewpager

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.math.MathUtils.clamp
import androidx.core.view.ViewCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import net.kibotu.logger.Logger.logv
import java.util.*

/**
 * Created by [Jan Rabe](https://about.me/janrabe).
 *
 *
 */

class SwipeDirectionViewPager : ViewPager {

    private val uuid = UUID.randomUUID().toString()

    private val SWIPE_THRESH_HOLD = 10.dpToPx()

    private var initialXValue: Float = 0f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return isSwipeAllowed(event) && super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return isSwipeAllowed(event) && super.onInterceptTouchEvent(event)
    }

    /**
     * Intercepts touch events and notifies pages.
     *
     * based on [how-to-disable-viewpager-from-swiping-in-one-direction](https://stackoverflow.com/questions/19602369/how-to-disable-viewpager-from-swiping-in-one-direction/34076649#34076649)
     */
    private fun isSwipeAllowed(event: MotionEvent): Boolean {
        val allowSwipeDirection = this.allowSwipeDirection()

        if (event.action == MotionEvent.ACTION_DOWN) {
            initialXValue = event.x
            return true
        }

        if (event.action != MotionEvent.ACTION_MOVE)
            return true

        try {
            val diffX = event.x - initialXValue

            getViewPagerPresenterAdapter()?.let {

                when {

                // we're on last page and scroll right
                    it.scrollHandler.isLeftPage()
                            && diffX > SWIPE_THRESH_HOLD -> getViewPagerPresenterAdapter()?.swipeLeftEdgeListener?.run()

                // we're on first page and scroll left
                    it.scrollHandler.isFirstPage()
                            && diffX < -SWIPE_THRESH_HOLD -> getViewPagerPresenterAdapter()?.swipeRightEdgeListener?.run()

                    else -> {
                    }
                }
            }

            // disable any swipe
            if (allowSwipeDirection == SwipeDirection.NONE)
                return false

            // we don't intercept any swipe events
            if (allowSwipeDirection == SwipeDirection.ALL)
                return true

            when {

            // swipe from left to right detected
                diffX > SWIPE_THRESH_HOLD
                        && allowSwipeDirection == SwipeDirection.RIGHT -> {

                    (getViewPagerPresenterAdapter()
                            ?.getFragment(currentItem) as? SwipeDirectionListener)
                            ?.onSwipeIntercepted(SwipeDirection.RIGHT)

                    return false

                }

            // swipe from right to left detected
                diffX < -SWIPE_THRESH_HOLD
                        && allowSwipeDirection == SwipeDirection.LEFT -> {

                    (getViewPagerPresenterAdapter()
                            ?.getFragment(currentItem) as? SwipeDirectionListener)
                            ?.onSwipeIntercepted(SwipeDirection.LEFT)

                    return false
                }
                else -> {
                }
            }
        } catch (exception: Exception) {
            if (enableLogging)
                exception.printStackTrace()
        }

        return true
    }

    override fun setAdapter(adapter: PagerAdapter?) {
        super.setAdapter(adapter)

        if (adapter is ViewPagerPresenterAdapter<*, *>) {
            adapter.scrollHandler = createScrollListener()
        }
    }

    private var mLayoutDirection = ViewCompat.LAYOUT_DIRECTION_LTR

    override fun onRtlPropertiesChanged(layoutDirection: Int) {
        super.onRtlPropertiesChanged(layoutDirection)

        mLayoutDirection = if (layoutDirection == View.LAYOUT_DIRECTION_RTL) ViewCompat.LAYOUT_DIRECTION_RTL else ViewCompat.LAYOUT_DIRECTION_LTR
        if (enableLogging) logv("isRtl = $mLayoutDirection")
    }

    private fun createScrollListener(): ScrollHandler {
        return object : ScrollHandler {

            override fun isRtl(): Boolean = mLayoutDirection == ViewCompat.LAYOUT_DIRECTION_RTL

            override fun isLtr(): Boolean = mLayoutDirection == ViewCompat.LAYOUT_DIRECTION_RTL

            override fun skip(amount: Int, smooth: Boolean) {
                if (enableLogging) logv("[skip] $amount")
                if (currentItem + amount >= adapter!!.count - 1)
                    getViewPagerPresenterAdapter()?.swipeRightEdgeListener?.run()
                else
                    scrollTo(currentItem + amount, smooth)
            }

            override fun isLastPage(): Boolean {
                return when {
                    isRtl() -> isLeftPage()
                    else -> isRightPage()
                }
            }

            override fun isFirstPage(): Boolean {
                return when {
                    isRtl() -> isRightPage()
                    else -> isLeftPage()
                }
            }

            override fun isRightPage() = currentItem == adapter!!.count - 1

            override fun isLeftPage() = currentItem == 0

            override fun scrollTo(position: Int, smooth: Boolean) {
                if (enableLogging) logv("""[scrollTo] position=$position /  ${adapter!!.count - 1} smooth=$smooth position == viewPager.getCurrentItem()${position == currentItem}""")
                if (position == currentItem)
                    return

                setCurrentItem(clamp(position, 0, adapter!!.count - 1), smooth)
            }

            override fun scrollToNextPage() {
                when {
                    isRtl() -> swipeLeft()
                    else -> swipeRight()
                }
            }

            override fun scrollToPreviousPage() {
                when {
                    isRtl() -> swipeRight()
                    else -> swipeLeft()
                }
            }

            override fun swipeRight() {
                scrollTo(currentItem + 1, true)
            }

            override fun swipeLeft() {
                scrollTo(currentItem - 1, true)
            }
        }
    }

    private fun getViewPagerPresenterAdapter(): ViewPagerPresenterAdapter<*, *>? {
        return when (adapter) {
            is ViewPagerPresenterAdapter<*, *> -> adapter!! as ViewPagerPresenterAdapter<*, *>
            else -> null
        }
    }

    private fun allowSwipeDirection(): SwipeDirection {
        getViewPagerPresenterAdapter()?.let {
            return (it.getFragment(currentItem) as SwipeDirectionListener).allowSwipeDirection()
        }

        return SwipeDirection.ALL
    }
}

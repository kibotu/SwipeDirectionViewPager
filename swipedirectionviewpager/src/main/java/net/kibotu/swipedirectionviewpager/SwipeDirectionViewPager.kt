package net.kibotu.swipedirectionviewpager

import android.content.Context
import android.support.v4.math.MathUtils.clamp
import android.support.v4.view.PagerAdapter
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
                    it.scrollHandler.isFirstPage()
                            && diffX > SWIPE_THRESH_HOLD -> getViewPagerPresenterAdapter()?.swipeLeftEdgeListener?.run()

                // we're on first page and scroll left
                    it.scrollHandler.isLastPage()
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

    private fun createScrollListener(): ScrollHandler {
        return object : ScrollHandler {

            override fun skip(amount: Int, smooth: Boolean) {
                log("[skip] $amount")
                if (currentItem + amount >= adapter!!.count - 1)
                    getViewPagerPresenterAdapter()?.swipeRightEdgeListener?.run()
                else
                    scrollTo(currentItem + amount, smooth)
            }

            override fun isLastPage(): Boolean {
                return currentItem == adapter!!.count - 1
            }

            override fun isFirstPage(): Boolean {
                return currentItem == 0
            }

            override fun scrollTo(position: Int, smooth: Boolean) {
                log("""[scrollTo] position=$position /  ${adapter!!.count - 1} smooth=$smooth position == viewPager.getCurrentItem()${position == currentItem}""")
                if (position == currentItem)
                    return

                setCurrentItem(clamp(position, 0, adapter!!.count - 1), smooth)
            }

            override fun scrollToNextPage() {
                scrollTo(currentItem + 1, true)
            }

            override fun scrollToPreviousPage() {
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

    override fun tag(): String {
        return javaClass.simpleName + "[" + uuid.substring(0, 8) + "]"
    }
}

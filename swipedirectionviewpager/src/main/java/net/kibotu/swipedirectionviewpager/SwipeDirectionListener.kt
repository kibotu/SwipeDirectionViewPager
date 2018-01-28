package net.kibotu.swipedirectionviewpager

/**
 * Created by [Jan Rabe](https://about.me/janrabe).
 */

interface SwipeDirectionListener {

    /**
     * Event if [allowSwipeDirection] is set and user scrolls to previous or next page.
     */
    fun onSwipeIntercepted(direction: SwipeDirection)

    /**
     * @return Allowed [SwipeDirection].
     */
    fun allowSwipeDirection(): SwipeDirection
}

package net.kibotu.swipedirectionviewpager

/**
 * Created by [Jan Rabe](https://about.me/janrabe).
 */
interface SwipeDirectionListener {

    fun onSwipeIntercepted(direction: SwipeDirection)

    fun allowSwipeDirection(): SwipeDirection
}
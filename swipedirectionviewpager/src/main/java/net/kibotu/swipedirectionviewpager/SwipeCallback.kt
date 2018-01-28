package net.kibotu.swipedirectionviewpager

/**
 * Created by [Jan Rabe](https://about.me/janrabe).
 */
interface SwipeCallback {
    fun swipe(direction: SwipeDirection): Boolean
}

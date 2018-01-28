package net.kibotu.swipedirectionviewpager

/**
 * Created by [Jan Rabe](https://about.me/janrabe).
 */
interface ScrollListener {
    fun skip(amount: Int)
    fun scrollTo(position: Int, smooth: Boolean)
    fun scrollToNextPage()
    fun scrollToPreviousPage()
    fun isFirstPage(): Boolean
    fun isLastPage(): Boolean
    fun swipeLeftToRight()
    fun swipeRightToLeft()
}
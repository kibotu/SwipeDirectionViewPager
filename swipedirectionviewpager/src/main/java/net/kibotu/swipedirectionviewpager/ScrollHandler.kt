package net.kibotu.swipedirectionviewpager

/**
 * Created by [Jan Rabe](https://about.me/janrabe).
 */
interface ScrollHandler {

    /**
     * Skips a given amount of pages.
     */
    fun skip(amount: Int, smooth: Boolean = true)

    /**
     * Scrolls to a given position.
     */
    fun scrollTo(position: Int, smooth: Boolean = true)

    /**
     * Scrolls to next page.
     */
    fun scrollToNextPage()

    /**
     * Scrolls to previous page.
     */
    fun scrollToPreviousPage()

    /**
     * @return true if current page is first.
     */
    fun isFirstPage(): Boolean

    /**
     * @return true if current page is last.
     */
    fun isLastPage(): Boolean
}
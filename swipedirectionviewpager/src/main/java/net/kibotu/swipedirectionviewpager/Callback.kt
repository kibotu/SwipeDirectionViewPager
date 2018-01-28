package net.kibotu.swipedirectionviewpager

/**
 * Created by [Jan Rabe](https://about.me/janrabe).
 */

interface Callback<in T> {
    fun update(t: T)
}

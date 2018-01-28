package net.kibotu.swipedirectionviewpager

/**
 * Created by [Jan Rabe](https://about.me/janrabe).
 */

internal interface Updatable<in T> {
    fun onUpdate(t: T)
}
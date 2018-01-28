package net.kibotu.swipedirectionviewpager

/**
 * Created by [Jan Rabe](https://about.me/janrabe).
 */

interface Updatable<in T> {
    fun onUpdate(t: T)
}
@file:JvmName("Extensions")

package net.kibotu.swipedirectionviewpager

import android.content.res.Resources
import android.util.Log
import android.util.TypedValue

internal val enableLogging: Boolean = true

internal fun Int.dpToPx(): Int {
    return toFloat().dpToPx().toInt()
}

internal fun Float.dpToPx(): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)
}

internal fun Any?.log() {
    this?.log(this.toString())
}

internal fun Any.log(message: Any?): Any {
    return when {
        enableLogging -> Log.v((this as? LogTag)?.tag() ?: this.javaClass.simpleName, "$message")
        else -> {
        }
    }
}
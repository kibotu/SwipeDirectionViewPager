@file:JvmName("Extensions")

package net.kibotu.swipedirectionviewpager

import android.content.res.Resources
import android.util.Log
import android.util.TypedValue

internal val enableLogging: Boolean = false

internal fun Int.dpToPx(): Int {
    return toFloat().dpToPx().toInt()
}

internal fun Float.dpToPx(): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)
}
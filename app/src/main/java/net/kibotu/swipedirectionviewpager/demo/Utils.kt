@file:JvmName("Utils")

package net.kibotu.swipedirectionviewpager.demo

import android.app.Activity
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import java.util.*

/**
 * Created by [Jan Rabe](https://about.me/janrabe).
 */

fun snackbar(context: FragmentActivity?, message: String) {
    if (context == null) {
        Log.v("snackbar", message)
        return
    }
    val snackBar = Snackbar.make(getContentRoot(context), message, Snackbar.LENGTH_SHORT)
    val text: TextView = snackBar.view.findViewById(com.google.android.material.R.id.snackbar_text)
    text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
    snackBar.show()
}

fun getContentRoot(context: Activity): View {
    return context
            .window
            .decorView
            .findViewById(android.R.id.content)
}

fun createRandomImageUrl(): String {
    val landscape = Random().nextBoolean()
    return "https://lorempixel.com/${if (landscape) 400 else 200}/${if (landscape) 200 else 400}/"
}
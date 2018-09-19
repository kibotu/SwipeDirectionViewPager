package net.kibotu.swipedirectionviewpager.demo

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.multidex.MultiDexApplication

/**
 * Created by [Jan Rabe](https://about.me/janrabe).
 */
open class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        Log.v("App", "[onCreate]")

        LocaleHelper.setLocale(this, "ar")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleHelper.setLocale(this, LocaleHelper.getLanguage(this))
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }
}
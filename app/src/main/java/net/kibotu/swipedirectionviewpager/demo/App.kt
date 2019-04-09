package net.kibotu.swipedirectionviewpager.demo

import android.content.Context
import android.content.res.Configuration
import androidx.multidex.MultiDexApplication
import net.kibotu.logger.LogcatLogger
import net.kibotu.logger.Logger

/**
 * Created by [Jan Rabe](https://about.me/janrabe).
 */
open class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        Logger.addLogger(LogcatLogger())

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
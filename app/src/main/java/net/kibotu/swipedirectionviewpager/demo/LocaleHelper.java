package net.kibotu.swipedirectionviewpager.demo;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Locale;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;
import static android.os.Build.VERSION_CODES.N;
import static net.kibotu.ContextHelper.getApplication;

public class LocaleHelper {

    private static final String TAG = LocaleHelper.class.getSimpleName();
    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";

    public static Context onAttach(Context context) {
        String lang = getPersistedData(context, "en");
        return setLocale(context, lang);
    }

    public static Context onAttach(Context context, String defaultLanguage) {
        String lang = getPersistedData(context, defaultLanguage);
        return setLocale(context, lang);
    }

    public static String getLanguage(Context context) {
        return getPersistedData(context, "en");
    }

    public static String getLanguage() {
        return getLanguage(getApplication());
    }

    public static Locale getLocale() {
        return new Locale(getLanguage(getApplication()));
    }

    @SuppressLint("NewApi")
    public static Locale getLocaleFrom(Configuration configuration) {
        return SDK_INT >= N
                ? configuration.getLocales().get(0)
                : configuration.locale;
    }

    public static Context setLocale(Context context, String language) {
        Log.v(TAG, "[persist] setLocale=" + language);

        if (language.equalsIgnoreCase(Locale.getDefault().getDisplayLanguage()))
            return context;

        persist(context, language);

        if (SDK_INT >= N) {
            return updateResources(context, language);
        }

        return updateResourcesLegacy(context, language);
    }

    private static String getPersistedData(Context context, String defaultLanguage) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage);
    }

    private static void persist(Context context, String language) {
        Log.v(TAG, "[persist] language=" + language);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(SELECTED_LANGUAGE, language);
        editor.apply();
    }

    @TargetApi(N)
    private static Context updateResources(Context context, String language) {
        Log.v(TAG, "[persist] updateResources=" + language);
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String language) {
        Log.v(TAG, "[persist] updateResourcesLegacy=" + language);
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        if (SDK_INT >= JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }
}
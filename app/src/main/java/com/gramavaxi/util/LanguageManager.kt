package com.gramavaxi.util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object LanguageManager {

    private const val PREFS_NAME   = "grama_vaxi_prefs"
    private const val KEY_LOCALE   = "app_locale"
    private const val DEFAULT_LANG = "en"

    private val _currentLocale = MutableStateFlow(DEFAULT_LANG)
    val currentLocale: StateFlow<String> = _currentLocale

    fun applySavedLocale(context: Context) {
        val saved = getSavedLocale(context)
        _currentLocale.value = saved
        applyLocale(saved)
    }

    fun setLocale(context: Context, localeCode: String) {
        _currentLocale.value = localeCode
        saveLocale(context, localeCode)
        applyLocale(localeCode)
    }

    fun toggle(context: Context) {
        val next = if (_currentLocale.value == DEFAULT_LANG) "kn" else DEFAULT_LANG
        setLocale(context, next)
    }

    fun getSavedLocale(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LOCALE, DEFAULT_LANG) ?: DEFAULT_LANG
    }

    private fun saveLocale(context: Context, localeCode: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LOCALE, localeCode)
            .apply()
    }

    fun applyLocale(localeCode: String) {
        val localeList = LocaleListCompat.forLanguageTags(localeCode)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}

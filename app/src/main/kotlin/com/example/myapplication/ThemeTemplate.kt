package com.example.myapplication

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.DpSize
import com.example.myapplication.ThemeTemplate.ThemeValue

val LocalScreenSize = compositionLocalOf<DpSize> { error(message = "No screen size provided!") }

interface ThemeTemplate {
    val isDark: Boolean
    val themeFlag: ThemeValue

    fun changeThemeValue(newValue: ThemeValue)

    sealed interface ThemeValue {
        data object AsSystem : ThemeValue
        data object Dark : ThemeValue
        data object Light : ThemeValue
    }
}

class ThemeTemplateImpl(
    override val themeFlag: ThemeValue,
    private val changeThemeValue: (ThemeValue) -> Unit,
    private val isSystemDark: () -> Boolean
) : ThemeTemplate {
    override val isDark: Boolean
        get() = themeFlag == ThemeValue.Dark || themeFlag == ThemeValue.AsSystem && isSystemDark()

    override fun changeThemeValue(newValue: ThemeValue) = changeThemeValue.invoke(newValue)
}

val LocalThemeTemplate =
    compositionLocalOf<ThemeTemplate> { error(message = "No theme is dark flag provided!") }

/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic

import android.R.attr
import android.content.res.ColorStateList
import androidx.annotation.ColorInt

/** @author Aidan Follestad (afollestad) */
internal data class ActiveInactiveColors(
  @field:ColorInt val activeColor: Int,
  @field:ColorInt val inactiveColor: Int
) {
  fun toEnabledSl(): ColorStateList {
    return ColorStateList(
        arrayOf(
            intArrayOf(attr.state_enabled), intArrayOf(-attr.state_enabled)
        ),
        intArrayOf(activeColor, inactiveColor)
    )
  }
}

/** @author Aidan Follestad (afollestad) */
internal data class ColorIsDarkState(
  @field:ColorInt val color: Int,
  val isDark: Boolean
)

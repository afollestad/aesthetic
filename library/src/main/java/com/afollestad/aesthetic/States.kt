/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic

import android.R.attr
import android.content.res.ColorStateList
import android.support.annotation.ColorInt
import io.reactivex.functions.BiFunction

/** @author Aidan Follestad (afollestad) */
data class ActiveInactiveColors(
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
internal data class BgIconColorState(
  @field:ColorInt val bgColor: Int,
  val iconTitleColor: ActiveInactiveColors?
) {

  companion object {
    fun creator(): BiFunction<Int, ActiveInactiveColors, BgIconColorState> {
      return BiFunction { color, iconTitleColors -> BgIconColorState(color, iconTitleColors) }
    }
  }
}

/** @author Aidan Follestad (afollestad) */
internal data class ColorIsDarkState(
  @field:ColorInt val color: Int,
  val isDark: Boolean
) {
  companion object {
    fun creator(): BiFunction<Int, Boolean, ColorIsDarkState> {
      return BiFunction { color, isDark -> ColorIsDarkState(color, isDark) }
    }
  }
}

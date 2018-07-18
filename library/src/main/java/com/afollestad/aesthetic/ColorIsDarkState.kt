package com.afollestad.aesthetic

import android.support.annotation.ColorInt
import io.reactivex.functions.BiFunction

/** @author Aidan Follestad (afollestad)
 */
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

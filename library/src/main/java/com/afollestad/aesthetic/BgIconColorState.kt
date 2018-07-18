package com.afollestad.aesthetic

import android.support.annotation.ColorInt
import io.reactivex.functions.BiFunction

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

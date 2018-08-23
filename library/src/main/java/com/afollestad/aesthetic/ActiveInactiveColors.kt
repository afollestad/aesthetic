/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic

import android.content.res.ColorStateList
import android.support.annotation.ColorInt

/** @author Aidan Follestad (afollestad) */
data class ActiveInactiveColors(
  @field:ColorInt val activeColor: Int,
  @field:ColorInt val inactiveColor: Int
) {

  fun toEnabledSl(): ColorStateList {
    return ColorStateList(
        arrayOf(
            intArrayOf(android.R.attr.state_enabled), intArrayOf(-android.R.attr.state_enabled)
        ),
        intArrayOf(activeColor, inactiveColor)
    )
  }
}

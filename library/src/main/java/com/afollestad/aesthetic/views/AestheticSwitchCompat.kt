/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.SwitchCompat
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.ColorIsDarkState
import com.afollestad.aesthetic.utils.allOf
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.resId
import com.afollestad.aesthetic.utils.setTint
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.unsubscribeOnDetach
import com.afollestad.aesthetic.utils.watchColor

/** @author Aidan Follestad (afollestad) */
class AestheticSwitchCompat(
  context: Context,
  attrs: AttributeSet? = null
) : SwitchCompat(context, attrs) {

  private var backgroundResId: Int = 0

  init {
    if (attrs != null) {
      backgroundResId = context.resId(attrs, android.R.attr.background)
    }
  }

  private fun invalidateColors(state: ColorIsDarkState) = setTint(state.color, state.isDark)

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    allOf(
        watchColor(
            context,
            backgroundResId,
            get().colorAccent()
        ),
        get().isDark
    ) { color, isDark -> ColorIsDarkState(color, isDark) }
        .distinctToMainThread()
        .subscribeTo(::invalidateColors)
        .unsubscribeOnDetach(this)
  }
}

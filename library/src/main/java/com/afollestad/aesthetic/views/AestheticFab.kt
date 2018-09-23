/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.ColorIsDarkState
import com.afollestad.aesthetic.utils.allOf
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.isColorLight
import com.afollestad.aesthetic.utils.resId
import com.afollestad.aesthetic.utils.setTintAuto
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.tint
import com.afollestad.aesthetic.utils.unsubscribeOnDetach
import com.afollestad.aesthetic.utils.watchColor
import com.google.android.material.floatingactionbutton.FloatingActionButton

/** @author Aidan Follestad (afollestad) */
class AestheticFab(
  context: Context,
  attrs: AttributeSet? = null
) : FloatingActionButton(context, attrs) {

  private var backgroundResId: Int = 0
  private var iconColor: Int = 0

  init {
    if (attrs != null) {
      backgroundResId = context.resId(attrs, android.R.attr.background)
    }
  }

  private fun invalidateColors(state: ColorIsDarkState) {
    setTintAuto(state.color, true, state.isDark)
    iconColor = if (state.color.isColorLight()) Color.BLACK else Color.WHITE
    setImageDrawable(drawable)
  }

  override fun setImageDrawable(drawable: Drawable?) =
    super.setImageDrawable(drawable.tint(iconColor))

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

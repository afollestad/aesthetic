/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.content.Context
import android.util.AttributeSet
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.ColorIsDarkState
import com.afollestad.aesthetic.internal.AttrWizard
import com.afollestad.aesthetic.utils.combine
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.observableForAttrName
import com.afollestad.aesthetic.utils.setTintAuto
import com.afollestad.aesthetic.utils.subscribeHintTextColor
import com.afollestad.aesthetic.utils.subscribeTextColor
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.unsubscribeOnDetach
import com.google.android.material.textfield.TextInputEditText

/** @author Aidan Follestad (afollestad) */
class AestheticTextInputEditText(
  context: Context,
  attrs: AttributeSet? = null
) : TextInputEditText(context, attrs) {

  private var lastState: ColorIsDarkState? = null
  private val wizard = AttrWizard(context, attrs)
  private val backgroundColorValue = wizard.getRawValue(android.R.attr.background)

  private fun invalidateColors(state: ColorIsDarkState) {
    this.lastState = state
    setTintAuto(state.color, true, state.isDark)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    get().textColorPrimary()
        .distinctToMainThread()
        .subscribeTextColor(this)
        .unsubscribeOnDetach(this)

    get().textColorSecondary()
        .distinctToMainThread()
        .subscribeHintTextColor(this)
        .unsubscribeOnDetach(this)

    combine(
        get().observableForAttrName(
            backgroundColorValue,
            get().colorAccent()
        )!!,
        get().isDark
    ) { color, isDark -> ColorIsDarkState(color, isDark) }
        .distinctToMainThread()
        .subscribeTo(::invalidateColors)
        .unsubscribeOnDetach(this)
  }

  override fun refreshDrawableState() {
    super.refreshDrawableState()
    if (lastState != null) {
      post { invalidateColors(lastState!!) }
    }
  }
}

/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.ColorIsDarkState
import com.afollestad.aesthetic.internal.AttrWizard
import com.afollestad.aesthetic.utils.combine
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.observableForAttrName
import com.afollestad.aesthetic.utils.setTint
import com.afollestad.aesthetic.utils.subscribeTextColor
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.unsubscribeOnDetach

/** @author Aidan Follestad (afollestad) */
class AestheticRadioButton(
  context: Context,
  attrs: AttributeSet? = null
) : AppCompatRadioButton(context, attrs) {

  private val wizard = AttrWizard(context, attrs)
  private val backgroundColorValue = wizard.getRawValue(android.R.attr.background)

  private fun invalidateColors(state: ColorIsDarkState) = setTint(state.color, state.isDark)

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

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

    get().textColorPrimary()
        .distinctToMainThread()
        .subscribeTextColor(this)
        .unsubscribeOnDetach(this)
  }
}

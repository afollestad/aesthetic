/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.ColorIsDarkState
import com.afollestad.aesthetic.R
import com.afollestad.aesthetic.internal.AttrWizard
import com.afollestad.aesthetic.utils.combine
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.observableForAttrName
import com.afollestad.aesthetic.utils.setTintAuto
import com.afollestad.aesthetic.utils.subscribeHintTextColor
import com.afollestad.aesthetic.utils.subscribeTextColor
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.unsubscribeOnDetach

/** @author Aidan Follestad (afollestad) */
@SuppressLint("ResourceType")
class AestheticEditText(
  context: Context,
  attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

  private val wizard = AttrWizard(context, attrs)
  private val tintColorValue = wizard.getRawValue(R.attr.tint)
  private val textColorValue = wizard.getRawValue(android.R.attr.textColor)
  private val textColorHintValue = wizard.getRawValue(android.R.attr.textColorHint)

  private fun invalidateColors(state: ColorIsDarkState) =
    setTintAuto(state.color, true, state.isDark)

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    combine(
        get().observableForAttrName(
            tintColorValue,
            get().colorAccent()
        )!!,
        get().isDark
    ) { color, isDark -> ColorIsDarkState(color, isDark) }
        .distinctToMainThread()
        .subscribeTo(::invalidateColors)
        .unsubscribeOnDetach(this)

    get().observableForAttrName(
        textColorValue,
        get().textColorPrimary()
    )!!
        .distinctToMainThread()
        .subscribeTextColor(this)
        .unsubscribeOnDetach(this)

    get().observableForAttrName(
        textColorHintValue,
        get().textColorSecondary()
    )!!
        .distinctToMainThread()
        .subscribeHintTextColor(this)
        .unsubscribeOnDetach(this)
  }
}

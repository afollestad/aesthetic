/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.content.Context
import android.util.AttributeSet
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.internal.AttrWizard
import com.afollestad.aesthetic.utils.adjustAlpha
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.observableForAttrName
import com.afollestad.aesthetic.utils.setAccentColor
import com.afollestad.aesthetic.utils.setHintColor
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.unsubscribeOnDetach
import com.google.android.material.textfield.TextInputLayout

/** @author Aidan Follestad (afollestad) */
class AestheticTextInputLayout(
  context: Context,
  attrs: AttributeSet? = null
) : TextInputLayout(context, attrs) {

  private val wizard = AttrWizard(context, attrs)
  private val backgroundColorValue = wizard.getRawValue(android.R.attr.background)

  private fun invalidateColors(color: Int) = setAccentColor(color)

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    get().textColorSecondary()
        .distinctToMainThread()
        .subscribeTo { setHintColor(it.adjustAlpha(0.7f)) }
        .unsubscribeOnDetach(this)

    get().observableForAttrName(
        backgroundColorValue,
        get().colorAccent()
    )!!
        .distinctToMainThread()
        .subscribeTo(::invalidateColors)
        .unsubscribeOnDetach(this)
  }
}

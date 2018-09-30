/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.internal.AttrWizard
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.observableForAttrName
import com.afollestad.aesthetic.utils.subscribeTextColor
import com.afollestad.aesthetic.utils.unsubscribeOnDetach

/** @author Aidan Follestad (afollestad) */
class AestheticTextView(
  context: Context,
  attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

  private val wizard = AttrWizard(context, attrs)
  private val textColorValue = wizard.getRawValue(android.R.attr.textColor)

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    val defaultObs = if (id == android.R.id.title) {
      get().textColorPrimary()
    } else {
      get().textColorSecondary()
    }
    get().observableForAttrName(textColorValue, defaultObs)
        ?.distinctToMainThread()
        ?.subscribeTextColor(this)
        ?.unsubscribeOnDetach(this)
  }
}

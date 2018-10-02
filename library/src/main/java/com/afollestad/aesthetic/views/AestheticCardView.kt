/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.R
import com.afollestad.aesthetic.internal.AttrWizard
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.observableForAttrName
import com.afollestad.aesthetic.utils.subscribeBackgroundColor
import com.afollestad.aesthetic.utils.unsubscribeOnDetach

/** @author Aidan Follestad (afollestad) */
@SuppressLint("PrivateResource")
class AestheticCardView(
  context: Context,
  attrs: AttributeSet? = null
) : CardView(context, attrs) {

  private val wizard = AttrWizard(context, attrs)
  private val backgroundColorValue = wizard.getRawValue(R.attr.cardBackgroundColor)

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    get().observableForAttrName(
        backgroundColorValue,
        get().colorCardViewBackground()
    )!!
        .distinctToMainThread()
        .subscribeBackgroundColor(this)
        .unsubscribeOnDetach(this)
  }
}

/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.R
import com.afollestad.aesthetic.internal.AttrWizard
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.observableForAttrName
import com.afollestad.aesthetic.utils.subscribeBackgroundColor
import com.afollestad.aesthetic.utils.subscribeImageViewTint
import com.afollestad.aesthetic.utils.unsubscribeOnDetach

/** @author Aidan Follestad (afollestad) */
class AestheticImageView(
  context: Context,
  attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

  private val wizard = AttrWizard(context, attrs)
  private val backgroundColorValue = wizard.getRawValue(android.R.attr.background)
  private val tintColorValue = wizard.getRawValue(R.attr.tint)

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    get().observableForAttrName(backgroundColorValue)
        ?.distinctToMainThread()
        ?.subscribeBackgroundColor(this)
        ?.unsubscribeOnDetach(this)

    get().observableForAttrName(tintColorValue)
        ?.distinctToMainThread()
        ?.subscribeImageViewTint(this)
        ?.unsubscribeOnDetach(this)
  }
}

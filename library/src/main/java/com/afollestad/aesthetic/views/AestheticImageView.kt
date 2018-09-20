/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.afollestad.aesthetic.R.attr
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.resId
import com.afollestad.aesthetic.utils.subscribeBackgroundColor
import com.afollestad.aesthetic.utils.subscribeImageViewTint
import com.afollestad.aesthetic.utils.unsubscribeOnDetach
import com.afollestad.aesthetic.utils.watchColor

/** @author Aidan Follestad (afollestad) */
class AestheticImageView(
  context: Context,
  attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

  private var backgroundResId: Int = 0
  private var tintResId: Int = 0

  init {
    if (attrs != null) {
      backgroundResId = context.resId(attrs, android.R.attr.background)
      tintResId = context.resId(attrs, attr.tint)
      if (tintResId == 0) {
        tintResId = context.resId(attrs, android.R.attr.tint)
      }
    }
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    watchColor(context, backgroundResId)
        .distinctToMainThread()
        .subscribeBackgroundColor(this)
        .unsubscribeOnDetach(this)

    watchColor(context, tintResId)
        .distinctToMainThread()
        .subscribeImageViewTint(this)
        .unsubscribeOnDetach(this)
  }
}

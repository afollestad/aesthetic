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
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.resId
import com.afollestad.aesthetic.utils.subscribeTextColor
import com.afollestad.aesthetic.utils.watchColor
import io.reactivex.disposables.Disposable

/** @author Aidan Follestad (afollestad) */
class AestheticTextView(
  context: Context,
  attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

  private var subscription: Disposable? = null
  private var textColorResId: Int = 0

  init {
    if (attrs != null) {
      textColorResId = context.resId(attrs, android.R.attr.textColor)
    }
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    subscription = watchColor(
        context,
        textColorResId,
        if (id == android.R.id.title)
          get().textColorPrimary()
        else
          get().textColorSecondary()
    )
        .distinctToMainThread()
        .subscribeTextColor(this)
  }

  override fun onDetachedFromWindow() {
    subscription?.dispose()
    super.onDetachedFromWindow()
  }
}

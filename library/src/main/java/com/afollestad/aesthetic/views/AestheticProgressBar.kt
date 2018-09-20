/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.setTint
import com.afollestad.aesthetic.utils.subscribeTo
import io.reactivex.disposables.Disposable

/** @author Aidan Follestad (afollestad) */
class AestheticProgressBar(
  context: Context?,
  attrs: AttributeSet? = null
) : ProgressBar(context, attrs) {

  private var subscription: Disposable? = null

  private fun invalidateColors(color: Int) = setTint(color)

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    subscription = get().colorAccent()
        .distinctToMainThread()
        .subscribeTo(::invalidateColors)
  }

  override fun onDetachedFromWindow() {
    subscription?.dispose()
    super.onDetachedFromWindow()
  }
}

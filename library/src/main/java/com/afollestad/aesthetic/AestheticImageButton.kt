/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic

import android.content.Context
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet
import com.afollestad.aesthetic.utils.ViewUtil
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.onErrorLogAndRethrow
import com.afollestad.aesthetic.utils.resId
import io.reactivex.disposables.Disposable

/** @author Aidan Follestad (afollestad) */
class AestheticImageButton(
  context: Context,
  attrs: AttributeSet? = null
) : AppCompatImageButton(context, attrs) {

  private var bgSubscription: Disposable? = null
  private var backgroundResId: Int = 0

  init {
    if (attrs != null) {
      backgroundResId = context.resId(attrs, android.R.attr.background)
    }
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    val obs = ViewUtil.getObservableForResId(context, backgroundResId, null)
    if (obs != null) {
      bgSubscription = obs
          .distinctToMainThread()
          .subscribe(
              ViewBackgroundAction(this),
              onErrorLogAndRethrow()
          )
    }
  }

  override fun onDetachedFromWindow() {
    bgSubscription?.dispose()
    super.onDetachedFromWindow()
  }
}

/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import com.afollestad.aesthetic.utils.EdgeGlowUtil.setEdgeGlowColor
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.onErrorLogAndRethrow
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/** @author Aidan Follestad (afollestad) */
class AestheticViewPager(
  context: Context,
  attrs: AttributeSet? = null
) : ViewPager(context, attrs) {

  private var subscription: Disposable? = null

  private fun invalidateColors(color: Int) =
    setEdgeGlowColor(this, color)

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    subscription = Aesthetic.get()
        .colorAccent()
        .distinctToMainThread()
        .subscribe(
            Consumer { invalidateColors(it) },
            onErrorLogAndRethrow()
        )
  }

  override fun onDetachedFromWindow() {
    subscription?.dispose()
    super.onDetachedFromWindow()
  }
}

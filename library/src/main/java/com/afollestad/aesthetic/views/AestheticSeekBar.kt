/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSeekBar
import com.afollestad.aesthetic.Aesthetic
import com.afollestad.aesthetic.ColorIsDarkState
import com.afollestad.aesthetic.utils.TintHelper.setTint
import com.afollestad.aesthetic.utils.watchColor
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.onErrorLogAndRethrow
import com.afollestad.aesthetic.utils.resId
import io.reactivex.Observable.combineLatest
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/** @author Aidan Follestad (afollestad) */
class AestheticSeekBar(
  context: Context,
  attrs: AttributeSet? = null
) : AppCompatSeekBar(context, attrs) {

  private var subscription: Disposable? = null
  private var backgroundResId: Int = 0

  init {
    if (attrs != null) {
      backgroundResId = context.resId(attrs, android.R.attr.background)
    }
  }

  private fun invalidateColors(state: ColorIsDarkState) =
    setTint(this, state.color, state.isDark)

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    subscription = combineLatest(
        watchColor(
            context, backgroundResId, Aesthetic.get().colorAccent()
        ),
        Aesthetic.get().isDark,
        ColorIsDarkState.creator()
    )
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

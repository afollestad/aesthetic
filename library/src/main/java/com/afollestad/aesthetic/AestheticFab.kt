/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.afollestad.aesthetic.utils.TintHelper.createTintedDrawable
import com.afollestad.aesthetic.utils.TintHelper.setTintAuto
import com.afollestad.aesthetic.utils.watchColor
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.isColorLight
import com.afollestad.aesthetic.utils.onErrorLogAndRethrow
import com.afollestad.aesthetic.utils.resId
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.reactivex.Observable.combineLatest
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/** @author Aidan Follestad (afollestad) */
class AestheticFab(
  context: Context,
  attrs: AttributeSet? = null
) : FloatingActionButton(context, attrs) {

  private var subscription: Disposable? = null
  private var backgroundResId: Int = 0
  private var iconColor: Int = 0

  init {
    if (attrs != null) {
      backgroundResId = context.resId(attrs, android.R.attr.background)
    }
  }

  private fun invalidateColors(state: ColorIsDarkState) {
    setTintAuto(this, state.color, true, state.isDark)
    iconColor = if (state.color.isColorLight()) Color.BLACK else Color.WHITE
    setImageDrawable(drawable)
  }

  override fun setImageDrawable(drawable: Drawable?) {
    super.setImageDrawable(createTintedDrawable(drawable, iconColor))
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    subscription = combineLatest(
        watchColor(
            context,
            backgroundResId,
            Aesthetic.get().colorAccent()
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

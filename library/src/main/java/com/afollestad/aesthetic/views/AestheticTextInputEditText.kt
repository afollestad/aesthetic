/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.content.Context
import android.util.AttributeSet
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.ColorIsDarkState
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.plusAssign
import com.afollestad.aesthetic.utils.resId
import com.afollestad.aesthetic.utils.setTintAuto
import com.afollestad.aesthetic.utils.subscribeHintTextColor
import com.afollestad.aesthetic.utils.subscribeTextColor
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.watchColor
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.Observable.combineLatest
import io.reactivex.disposables.CompositeDisposable

/** @author Aidan Follestad (afollestad) */
class AestheticTextInputEditText(
  context: Context,
  attrs: AttributeSet? = null
) : TextInputEditText(context, attrs) {

  private var subs: CompositeDisposable? = null
  private var backgroundResId: Int = 0
  private var lastState: ColorIsDarkState? = null

  init {
    if (attrs != null) {
      backgroundResId = context.resId(attrs, android.R.attr.background)
    }
  }

  private fun invalidateColors(state: ColorIsDarkState) {
    this.lastState = state
    setTintAuto(state.color, true, state.isDark)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    subs = CompositeDisposable()

    subs += get().textColorPrimary()
        .distinctToMainThread()
        .subscribeTextColor(this)

    subs += get().textColorSecondary()
        .distinctToMainThread()
        .subscribeHintTextColor(this)

    subs += combineLatest(
        watchColor(
            context,
            backgroundResId,
            get().colorAccent()
        ),
        get().isDark,
        ColorIsDarkState.creator()
    )
        .distinctToMainThread()
        .subscribeTo(::invalidateColors)
  }

  override fun onDetachedFromWindow() {
    subs?.clear()
    super.onDetachedFromWindow()
  }

  override fun refreshDrawableState() {
    super.refreshDrawableState()
    if (lastState != null) {
      post { invalidateColors(lastState!!) }
    }
  }
}

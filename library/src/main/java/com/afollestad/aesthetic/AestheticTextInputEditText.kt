/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic

import android.content.Context
import android.support.design.widget.TextInputEditText
import android.util.AttributeSet
import com.afollestad.aesthetic.utils.TintHelper
import com.afollestad.aesthetic.utils.ViewUtil
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.onErrorLogAndRethrow
import com.afollestad.aesthetic.utils.plusAssign
import com.afollestad.aesthetic.utils.resId
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer

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
    TintHelper.setTintAuto(this, state.color, true, state.isDark)
    TintHelper.setCursorTint(this, state.color)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    subs = CompositeDisposable()
    subs!! +=
        Aesthetic.get()
            .textColorPrimary()
            .distinctToMainThread()
            .subscribe(
                ViewTextColorAction(this),
                onErrorLogAndRethrow()
            )
    subs!! +=
        Aesthetic.get()
            .textColorSecondary()
            .distinctToMainThread()
            .subscribe(
                ViewHintTextColorAction(this),
                onErrorLogAndRethrow()
            )
    subs!! +=
        Observable.combineLatest(
            ViewUtil.getObservableForResId(
                context, backgroundResId, Aesthetic.get().colorAccent()
            )!!,
            Aesthetic.get().isDark,
            ColorIsDarkState.creator()
        )
            .distinctToMainThread()
            .subscribe(
                Consumer { this.invalidateColors(it) },
                onErrorLogAndRethrow()
            )
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

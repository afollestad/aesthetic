/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic

import android.content.Context
import android.support.v7.widget.AppCompatRadioButton
import android.util.AttributeSet
import com.afollestad.aesthetic.actions.ViewTextColorAction
import com.afollestad.aesthetic.utils.TintHelper
import com.afollestad.aesthetic.utils.TintHelper.setTint
import com.afollestad.aesthetic.utils.ViewUtil
import com.afollestad.aesthetic.utils.ViewUtil.getObservableForResId
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.onErrorLogAndRethrow
import com.afollestad.aesthetic.utils.plusAssign
import com.afollestad.aesthetic.utils.resId
import io.reactivex.Observable
import io.reactivex.Observable.combineLatest
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer

/** @author Aidan Follestad (afollestad) */
class AestheticRadioButton(
  context: Context,
  attrs: AttributeSet? = null
) : AppCompatRadioButton(context, attrs) {

  private var subscriptions: CompositeDisposable? = null
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
    subscriptions = CompositeDisposable()
    subscriptions!! +=
        combineLatest(
            getObservableForResId(
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
    subscriptions!! +=
        Aesthetic.get()
            .textColorPrimary()
            .distinctToMainThread()
            .subscribe(ViewTextColorAction(this))
  }

  override fun onDetachedFromWindow() {
    subscriptions?.clear()
    super.onDetachedFromWindow()
  }
}

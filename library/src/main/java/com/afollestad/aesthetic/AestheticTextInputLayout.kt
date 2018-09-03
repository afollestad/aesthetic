/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic

import android.content.Context
import android.support.design.widget.TextInputLayout
import android.util.AttributeSet
import com.afollestad.aesthetic.utils.ViewUtil.getObservableForResId
import com.afollestad.aesthetic.utils.adjustAlpha
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.onErrorLogAndRethrow
import com.afollestad.aesthetic.utils.plusAssign
import com.afollestad.aesthetic.utils.resId
import com.afollestad.aesthetic.utils.setAccentColor
import com.afollestad.aesthetic.utils.setHintColor
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer

/** @author Aidan Follestad (afollestad) */
class AestheticTextInputLayout(
  context: Context,
  attrs: AttributeSet? = null
) : TextInputLayout(context, attrs) {

  private var subs: CompositeDisposable? = null
  private var backgroundResId: Int = 0

  init {
    if (attrs != null) {
      backgroundResId = context.resId(attrs, android.R.attr.background)
    }
  }

  private fun invalidateColors(color: Int) = setAccentColor(color)

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    subs = CompositeDisposable()

    subs!! +=
        Aesthetic.get()
            .textColorSecondary()
            .distinctToMainThread()
            .subscribe(
                Consumer { setHintColor(it.adjustAlpha(0.7f)) },
                onErrorLogAndRethrow()
            )
    subs!! +=
        getObservableForResId(
            context,
            backgroundResId,
            Aesthetic.get().colorAccent()
        )!!
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
}

/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.annotation.SuppressLint

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.afollestad.aesthetic.Aesthetic
import com.afollestad.aesthetic.ColorIsDarkState
import com.afollestad.aesthetic.actions.ViewHintTextColorAction
import com.afollestad.aesthetic.actions.ViewTextColorAction
import com.afollestad.aesthetic.utils.TintHelper.setCursorTint
import com.afollestad.aesthetic.utils.TintHelper.setTintAuto
import com.afollestad.aesthetic.utils.watchColor
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.onErrorLogAndRethrow
import com.afollestad.aesthetic.utils.plusAssign
import io.reactivex.Observable.combineLatest
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer

/** @author Aidan Follestad (afollestad) */
@SuppressLint("ResourceType")
class AestheticEditText(
  context: Context,
  attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

  private var subs: CompositeDisposable? = null
  private var backgroundResId: Int = 0
  private var textColorResId: Int = 0
  private var textColorHintResId: Int = 0

  init {
    if (attrs != null) {
      val attrsArray = intArrayOf(
          android.R.attr.background, android.R.attr.textColor, android.R.attr.textColorHint
      )
      val ta = context.obtainStyledAttributes(attrs, attrsArray)
      backgroundResId = ta.getResourceId(0, 0)
      textColorResId = ta.getResourceId(1, 0)
      textColorHintResId = ta.getResourceId(2, 0)
      ta.recycle()
    }
  }

  private fun invalidateColors(state: ColorIsDarkState) {
    setTintAuto(this, state.color, true, state.isDark)
    setCursorTint(this, state.color)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    subs = CompositeDisposable()
    subs +=
        combineLatest(
            watchColor(
                context,
                backgroundResId,
                Aesthetic.get().colorAccent()
            )!!,
            Aesthetic.get().isDark,
            ColorIsDarkState.creator()
        )
            .distinctToMainThread()
            .subscribe(
                Consumer { invalidateColors(it) },
                onErrorLogAndRethrow()
            )
    subs +=
        watchColor(
            context,
            textColorResId,
            Aesthetic.get().textColorPrimary()
        )!!
            .distinctToMainThread()
            .subscribe(
                ViewTextColorAction(this),
                onErrorLogAndRethrow()
            )
    subs +=
        watchColor(
            context,
            textColorHintResId,
            Aesthetic.get().textColorSecondary()
        )!!
            .distinctToMainThread()
            .subscribe(
                ViewHintTextColorAction(this),
                onErrorLogAndRethrow()
            )
  }

  override fun onDetachedFromWindow() {
    subs?.clear()
    super.onDetachedFromWindow()
  }
}

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
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.ColorIsDarkState
import com.afollestad.aesthetic.utils.TintHelper.setCursorTint
import com.afollestad.aesthetic.utils.TintHelper.setTintAuto
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.plusAssign
import com.afollestad.aesthetic.utils.subscribeHintTextColor
import com.afollestad.aesthetic.utils.subscribeTextColor
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.watchColor
import io.reactivex.Observable.combineLatest
import io.reactivex.disposables.CompositeDisposable

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
          android.R.attr.background,
          android.R.attr.textColor,
          android.R.attr.textColorHint
      )
      val ta = context.obtainStyledAttributes(attrs, attrsArray)
      try {
        backgroundResId = ta.getResourceId(0, 0)
        textColorResId = ta.getResourceId(1, 0)
        textColorHintResId = ta.getResourceId(2, 0)
      } finally {
        ta.recycle()
      }
    }
  }

  private fun invalidateColors(state: ColorIsDarkState) {
    setTintAuto(this, state.color, true, state.isDark)
    setCursorTint(this, state.color)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    subs = CompositeDisposable()

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

    subs += watchColor(
        context,
        textColorResId,
        get().textColorPrimary()
    )
        .distinctToMainThread()
        .subscribeTextColor(this)

    subs +=
        watchColor(
            context,
            textColorHintResId,
            get().textColorSecondary()
        )
            .distinctToMainThread()
            .subscribeHintTextColor(this)
  }

  override fun onDetachedFromWindow() {
    subs?.clear()
    super.onDetachedFromWindow()
  }
}

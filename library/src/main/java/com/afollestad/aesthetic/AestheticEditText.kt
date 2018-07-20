package com.afollestad.aesthetic

import android.annotation.SuppressLint

import android.content.Context
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet
import com.afollestad.aesthetic.utils.TintHelper
import com.afollestad.aesthetic.utils.ViewUtil
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.onErrorLogAndRethrow
import com.afollestad.aesthetic.utils.plusAssign
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer

/** @author Aidan Follestad (afollestad) */
@SuppressLint("ResourceType")
class AestheticEditText(
  context: Context,
  attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

  private var subscriptions: CompositeDisposable? = null
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
    TintHelper.setTintAuto(this, state.color, true, state.isDark)
    TintHelper.setCursorTint(this, state.color)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    subscriptions = CompositeDisposable()
    subscriptions!! +=
        Observable.combineLatest(
            ViewUtil.getObservableForResId(
                context, backgroundResId, Aesthetic.get().colorAccent()
            )!!,
            Aesthetic.get().isDark,
            ColorIsDarkState.creator()
        )
            .distinctToMainThread()
            .subscribe(Consumer { this.invalidateColors(it) },
                onErrorLogAndRethrow()
            )
    subscriptions!! +=
        ViewUtil.getObservableForResId(
            context, textColorResId, Aesthetic.get().textColorPrimary()
        )!!
            .distinctToMainThread()
            .subscribe(ViewTextColorAction(this),
                onErrorLogAndRethrow()
            )
    subscriptions!! +=
        ViewUtil.getObservableForResId(
            context, textColorHintResId, Aesthetic.get().textColorSecondary()
        )!!
            .distinctToMainThread()
            .subscribe(ViewHintTextColorAction(this),
                onErrorLogAndRethrow()
            )
  }

  override fun onDetachedFromWindow() {
    subscriptions?.clear()
    super.onDetachedFromWindow()
  }
}

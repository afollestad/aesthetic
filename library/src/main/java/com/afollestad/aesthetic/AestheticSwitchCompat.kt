package com.afollestad.aesthetic

import android.content.Context
import android.support.v7.widget.SwitchCompat
import android.util.AttributeSet
import com.afollestad.aesthetic.utils.TintHelper
import com.afollestad.aesthetic.utils.ViewUtil
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.onErrorLogAndRethrow
import com.afollestad.aesthetic.utils.resId
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/** @author Aidan Follestad (afollestad) */
class AestheticSwitchCompat(
  context: Context,
  attrs: AttributeSet? = null
) : SwitchCompat(context, attrs) {

  private var subscription: Disposable? = null
  private var backgroundResId: Int = 0

  init {
    if (attrs != null) {
      backgroundResId = context.resId(attrs, android.R.attr.background)
    }
  }

  private fun invalidateColors(state: ColorIsDarkState) {
    TintHelper.setTint(this, state.color, state.isDark)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    subscription = Observable.combineLatest(
        ViewUtil.getObservableForResId(
            context, backgroundResId, Aesthetic.get().colorAccent()
        ),
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
    subscription?.dispose()
    super.onDetachedFromWindow()
  }
}

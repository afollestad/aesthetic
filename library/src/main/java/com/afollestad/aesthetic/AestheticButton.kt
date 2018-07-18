package com.afollestad.aesthetic

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.support.v7.widget.AppCompatButton
import android.util.AttributeSet
import com.afollestad.aesthetic.utils.TintHelper
import com.afollestad.aesthetic.utils.ViewUtil
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.isColorLight
import com.afollestad.aesthetic.utils.onErrorLogAndRethrow
import com.afollestad.aesthetic.utils.resId
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/** @author Aidan Follestad (afollestad)
 */
class AestheticButton(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

  private var subscription: Disposable? = null
  private var backgroundResId: Int = 0

  init {
    if (attrs != null) {
      backgroundResId = context.resId(attrs, android.R.attr.background)
    }
  }

  private fun invalidateColors(state: ColorIsDarkState) {
    TintHelper.setTintAuto(this, state.color, true, state.isDark)
    val textColorSl = ColorStateList(
        arrayOf(
            intArrayOf(android.R.attr.state_enabled), intArrayOf(-android.R.attr.state_enabled)
        ),
        intArrayOf(
            if (state.color.isColorLight()) Color.BLACK else Color.WHITE,
            if (state.isDark) Color.WHITE else Color.BLACK
        )
    )
    setTextColor(textColorSl)

    // Hack around button color not updating
    isEnabled = !isEnabled
    isEnabled = !isEnabled
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    subscription = Observable.combineLatest(
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
    subscription?.dispose()
    super.onDetachedFromWindow()
  }
}

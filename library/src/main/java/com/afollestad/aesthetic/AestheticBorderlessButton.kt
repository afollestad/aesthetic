package com.afollestad.aesthetic

import android.content.Context
import android.content.res.ColorStateList
import android.support.v7.widget.AppCompatButton
import android.util.AttributeSet
import com.afollestad.aesthetic.utils.adjustAlpha
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.onErrorLogAndRethrow
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/** @author Aidan Follestad (afollestad) */
class AestheticBorderlessButton(
  context: Context,
  attrs: AttributeSet? = null
) : AppCompatButton(context, attrs) {

  private var subscription: Disposable? = null

  private fun invalidateColors(accentColor: Int) {
    val textColorSl = ColorStateList(
        arrayOf(
            intArrayOf(android.R.attr.state_enabled), intArrayOf(-android.R.attr.state_enabled)
        ),
        intArrayOf(
            accentColor, accentColor.adjustAlpha(0.56f)
        )
    )
    setTextColor(textColorSl)

    // Hack around button color not updating
    isEnabled = !isEnabled
    isEnabled = !isEnabled
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    subscription = Aesthetic.get()
        .colorAccent()
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

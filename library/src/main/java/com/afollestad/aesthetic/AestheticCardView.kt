/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import com.afollestad.aesthetic.utils.watchColor
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.onErrorLogAndRethrow
import com.afollestad.aesthetic.utils.resId
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/** @author Aidan Follestad (afollestad) */
@SuppressLint("PrivateResource")
class AestheticCardView(
  context: Context,
  attrs: AttributeSet? = null
) : CardView(context, attrs) {

  private var bgSubscription: Disposable? = null
  private var backgroundResId: Int = 0

  init {
    if (attrs != null) {
      backgroundResId = context.resId(attrs, R.attr.cardBackgroundColor)
    }
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    val obs = watchColor(
        context, backgroundResId, Aesthetic.get().colorCardViewBackground()
    )!!
    bgSubscription = obs
        .distinctToMainThread()
        .subscribe(
            Consumer { setCardBackgroundColor(it) },
            onErrorLogAndRethrow()
        )
  }

  override fun onDetachedFromWindow() {
    bgSubscription?.dispose()
    super.onDetachedFromWindow()
  }
}

/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.subscribeTextColor
import io.reactivex.disposables.Disposable

/** @author Aidan Follestad (afollestad)
 */
internal class AestheticSnackBarTextView(
  context: Context,
  attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

  private var subscription: Disposable? = null

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    subscription = get().snackbarTextColor()
        .distinctToMainThread()
        .subscribeTextColor(this)
  }

  override fun onDetachedFromWindow() {
    subscription?.dispose()
    super.onDetachedFromWindow()
  }
}

package com.afollestad.aesthetic

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.onErrorLogAndRethrow
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
    subscription = Aesthetic.get()
        .snackbarTextColor()
        .distinctToMainThread()
        .subscribe(ViewTextColorAction(this), onErrorLogAndRethrow())
  }

  override fun onDetachedFromWindow() {
    subscription?.dispose()
    super.onDetachedFromWindow()
  }
}

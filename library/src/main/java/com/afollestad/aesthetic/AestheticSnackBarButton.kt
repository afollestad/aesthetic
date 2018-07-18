package com.afollestad.aesthetic

import android.content.Context
import android.support.v7.widget.AppCompatButton
import android.util.AttributeSet
import com.afollestad.aesthetic.utils.distinctToMainThread
import io.reactivex.disposables.Disposable

/** @author Aidan Follestad (afollestad) */
internal class AestheticSnackBarButton(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

  private var subscription: Disposable? = null

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    subscription = Aesthetic.get()
        .snackbarActionTextColor()
        .distinctToMainThread()
        .subscribe(ViewTextColorAction(this))
  }

  override fun onDetachedFromWindow() {
    subscription?.dispose()
    super.onDetachedFromWindow()
  }
}

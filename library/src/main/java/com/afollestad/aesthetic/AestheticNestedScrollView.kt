package com.afollestad.aesthetic

import android.content.Context
import android.support.v4.widget.NestedScrollView
import android.util.AttributeSet
import com.afollestad.aesthetic.utils.EdgeGlowUtil
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.onErrorLogAndRethrow
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/** @author Aidan Follestad (afollestad) */
class AestheticNestedScrollView(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {

  private var subscription: Disposable? = null

  private fun invalidateColors(color: Int) {
    EdgeGlowUtil.setEdgeGlowColor(this, color)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    subscription = Aesthetic.get()
        .colorAccent()
        .distinctToMainThread()
        .subscribe(Consumer { this.invalidateColors(it) },
            onErrorLogAndRethrow()
        )
  }

  override fun onDetachedFromWindow() {
    subscription?.dispose()
    super.onDetachedFromWindow()
  }
}

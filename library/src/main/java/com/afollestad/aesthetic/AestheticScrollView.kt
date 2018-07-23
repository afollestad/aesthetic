package com.afollestad.aesthetic

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import com.afollestad.aesthetic.utils.EdgeGlowUtil
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.onErrorLogAndRethrow
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/** @author Aidan Follestad (afollestad) */
class AestheticScrollView(
  context: Context?,
  attrs: AttributeSet? = null
) : ScrollView(context, attrs) {

  private var subscription: Disposable? = null

  private fun invalidateColors(color: Int) {
    EdgeGlowUtil.setEdgeGlowColor(this, color)
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

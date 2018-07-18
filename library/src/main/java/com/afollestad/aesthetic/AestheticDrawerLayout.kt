package com.afollestad.aesthetic

import android.content.Context
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.graphics.drawable.DrawerArrowDrawable
import android.util.AttributeSet
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.onErrorLogAndRethrow
import io.reactivex.disposables.Disposable

import io.reactivex.functions.Consumer

/** @author Aidan Follestad (afollestad) */
class AestheticDrawerLayout(
  context: Context,
  attrs: AttributeSet? = null,
  defStyle: Int = 0
) : DrawerLayout(context, attrs, defStyle) {

  private var lastState: ActiveInactiveColors? = null
  private var arrowDrawable: DrawerArrowDrawable? = null
  private var subscription: Disposable? = null

  private fun invalidateColor(colors: ActiveInactiveColors?) {
    if (colors == null) {
      return
    }
    this.lastState = colors
    if (this.arrowDrawable != null) {
      this.arrowDrawable!!.color = lastState!!.activeColor
    }
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    subscription = Aesthetic.get()
        .colorIconTitle(null)
        .distinctToMainThread()
        .subscribe(Consumer { this.invalidateColor(it) },
            onErrorLogAndRethrow()
        )
  }

  override fun onDetachedFromWindow() {
    subscription!!.dispose()
    super.onDetachedFromWindow()
  }

  override fun addDrawerListener(listener: DrawerLayout.DrawerListener) {
    super.addDrawerListener(listener)
    if (listener is ActionBarDrawerToggle) {
      this.arrowDrawable = listener.drawerArrowDrawable
    }
    invalidateColor(lastState)
  }

  override fun setDrawerListener(listener: DrawerLayout.DrawerListener) {
    super.setDrawerListener(listener)
    if (listener is ActionBarDrawerToggle) {
      this.arrowDrawable = listener.drawerArrowDrawable
    }
    invalidateColor(lastState)
  }
}

/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.drawerlayout.widget.DrawerLayout
import com.afollestad.aesthetic.ActiveInactiveColors
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.unsubscribeOnDetach

/** @author Aidan Follestad (afollestad) */
class AestheticDrawerLayout(
  context: Context,
  attrs: AttributeSet? = null
) : DrawerLayout(context, attrs) {

  private var lastState: ActiveInactiveColors? = null
  private var arrowDrawable: DrawerArrowDrawable? = null

  private fun invalidateColor(colors: ActiveInactiveColors?) {
    if (colors == null) {
      return
    }
    this.lastState = colors
    this.arrowDrawable?.color = lastState!!.activeColor
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    get().colorIconTitle()
        .distinctToMainThread()
        .subscribeTo(::invalidateColor)
        .unsubscribeOnDetach(this)
  }

  override fun addDrawerListener(listener: DrawerLayout.DrawerListener) {
    super.addDrawerListener(listener)
    if (listener is ActionBarDrawerToggle) {
      this.arrowDrawable = listener.drawerArrowDrawable
    }
    invalidateColor(lastState)
  }

  @Suppress("OverridingDeprecatedMember", "DEPRECATION")
  override fun setDrawerListener(listener: DrawerLayout.DrawerListener) {
    super.setDrawerListener(listener)
    if (listener is ActionBarDrawerToggle) {
      this.arrowDrawable = listener.drawerArrowDrawable
    }
    invalidateColor(lastState)
  }
}

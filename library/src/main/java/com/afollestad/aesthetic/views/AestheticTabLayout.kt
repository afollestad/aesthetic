/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.annotation.ColorInt
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.ColorMode
import com.afollestad.aesthetic.utils.adjustAlpha
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.one
import com.afollestad.aesthetic.utils.subscribeBackgroundColor
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.tint
import com.afollestad.aesthetic.utils.unsubscribeOnDetach
import com.google.android.material.tabs.TabLayout

/** @author Aidan Follestad (afollestad) */
class AestheticTabLayout(
  context: Context,
  attrs: AttributeSet? = null
) : TabLayout(context, attrs) {

  companion object {
    const val UNFOCUSED_ALPHA = 0.5f
  }

  @SuppressLint("CheckResult")
  override fun setBackgroundColor(@ColorInt color: Int) {
    super.setBackgroundColor(color)
    get().toolbarIconColor()
        .one()
        .subscribeTo(::setIconsColor)
        .unsubscribeOnDetach(this)
    get().toolbarTitleColor()
        .one()
        .subscribeTo { setTabTextColors(it.adjustAlpha(UNFOCUSED_ALPHA), it) }
        .unsubscribeOnDetach(this)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    get().toolbarIconColor()
        .subscribeTo(::setIconsColor)
        .unsubscribeOnDetach(this)

    get().toolbarTitleColor()
        .subscribeTo { setTabTextColors(it.adjustAlpha(UNFOCUSED_ALPHA), it) }
        .unsubscribeOnDetach(this)

    get().tabLayoutBackgroundMode()
        .distinctToMainThread()
        .flatMap {
          when (it) {
            ColorMode.PRIMARY -> get().colorPrimary()
            ColorMode.ACCENT -> get().colorAccent()
          }
        }
        .distinctToMainThread()
        .subscribeBackgroundColor(this@AestheticTabLayout)
        .unsubscribeOnDetach(this)

    get().tabLayoutIndicatorMode()
        .distinctToMainThread()
        .flatMap {
          when (it) {
            ColorMode.PRIMARY -> get().colorPrimary()
            ColorMode.ACCENT -> get().colorAccent()
          }
        }
        .distinctToMainThread()
        .subscribeTo(::setSelectedTabIndicatorColor)
        .unsubscribeOnDetach(this)
  }

  private fun setIconsColor(color: Int) {
    val sl = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_selected),
            intArrayOf(android.R.attr.state_selected)
        ),
        intArrayOf(
            color.adjustAlpha(UNFOCUSED_ALPHA),
            color
        )
    )
    for (i in 0 until tabCount) {
      val tab = getTabAt(i)
      if (tab != null && tab.icon != null) {
        tab.icon = tab.icon.tint(sl)
      }
    }
  }
}

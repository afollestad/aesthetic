/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.ColorIsDarkState
import com.afollestad.aesthetic.NavigationViewMode.NONE
import com.afollestad.aesthetic.NavigationViewMode.SELECTED_ACCENT
import com.afollestad.aesthetic.NavigationViewMode.SELECTED_PRIMARY
import com.afollestad.aesthetic.R
import com.afollestad.aesthetic.utils.adjustAlpha
import com.afollestad.aesthetic.utils.combine
import com.afollestad.aesthetic.utils.color
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.unsubscribeOnDetach
import com.google.android.material.navigation.NavigationView
import io.reactivex.Observable.never
import io.reactivex.disposables.Disposable

/** @author Aidan Follestad (afollestad) */
@SuppressLint("RestrictedApi")
class AestheticNavigationView(
  context: Context,
  attrs: AttributeSet? = null
) : NavigationView(context, attrs) {

  private var colorSubscription: Disposable? = null

  private fun invalidateColors(state: ColorIsDarkState) {
    val selectedColor = state.color
    val isDark = state.isDark
    val baseColor = if (isDark) Color.WHITE else Color.BLACK
    val unselectedIconColor = baseColor.adjustAlpha(.54f)
    val unselectedTextColor = baseColor.adjustAlpha(.87f)

    val selectedItemBgColor = context.color(
        if (isDark) R.color.ate_navigation_drawer_selected_dark
        else R.color.ate_navigation_drawer_selected_light
    )

    val iconSl = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_checked)
        ),
        intArrayOf(unselectedIconColor, selectedColor)
    )
    val textSl = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_checked)
        ),
        intArrayOf(unselectedTextColor, selectedColor)
    )

    itemTextColor = textSl
    itemIconTintList = iconSl

    val bgDrawable = StateListDrawable()
    bgDrawable.addState(
        intArrayOf(android.R.attr.state_checked),
        ColorDrawable(selectedItemBgColor)
    )
    itemBackground = bgDrawable
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    get().navigationViewMode()
        .distinctToMainThread()
        .flatMap {
          when (it) {
            SELECTED_PRIMARY -> combine(
                get().colorPrimary(),
                get().isDark
            ) { color, isDark -> ColorIsDarkState(color, isDark) }

            SELECTED_ACCENT -> combine(
                get().colorAccent(),
                get().isDark
            ) { color, isDark -> ColorIsDarkState(color, isDark) }

            NONE -> never()
          }
        }
        .distinctUntilChanged()
        .subscribeTo(::invalidateColors)
        .unsubscribeOnDetach(this)
  }

  override fun onDetachedFromWindow() {
    colorSubscription?.dispose()
    super.onDetachedFromWindow()
  }
}

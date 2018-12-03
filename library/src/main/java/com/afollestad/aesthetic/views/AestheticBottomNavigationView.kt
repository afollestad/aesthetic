/**
 * Designed and developed by Aidan Follestad (@afollestad)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.afollestad.aesthetic.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.util.AttributeSet
import androidx.annotation.ColorInt
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.BottomNavBgMode
import com.afollestad.aesthetic.BottomNavIconTextMode
import com.afollestad.aesthetic.R
import com.afollestad.aesthetic.utils.adjustAlpha
import com.afollestad.aesthetic.utils.combine
import com.afollestad.aesthetic.utils.color
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.isColorLight
import com.afollestad.aesthetic.utils.plusAssign
import com.afollestad.aesthetic.utils.subscribeBackgroundColor
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.unsubscribeOnDetach
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.reactivex.disposables.CompositeDisposable

/** @author Aidan Follestad (afollestad) */
class AestheticBottomNavigationView(
  context: Context?,
  attrs: AttributeSet? = null
) : BottomNavigationView(context, attrs) {

  private var colorSubs: CompositeDisposable? = null
  private var lastTextIconColor: Int = 0
  private var backgroundColor: Int? = null
  private var iconTextMode = BottomNavIconTextMode.NONE

  private fun invalidateIconTextColor(
    backgroundColor: Int,
    selectedColor: Int
  ) {
    if (iconTextMode == BottomNavIconTextMode.NONE) return
    val baseColor = context.color(
        if (backgroundColor.isColorLight()) R.color.ate_icon_light
        else R.color.ate_icon_dark
    )
    val unselectedIconTextColor = baseColor.adjustAlpha(.87f)
    val iconColor = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_checked)
        ),
        intArrayOf(unselectedIconTextColor, selectedColor)
    )
    val textColor = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_checked)
        ),
        intArrayOf(unselectedIconTextColor, selectedColor)
    )
    itemIconTintList = iconColor
    itemTextColor = textColor
  }

  override fun setBackgroundColor(@ColorInt color: Int) {
    super.setBackgroundColor(color)
    this.backgroundColor = color
    this.itemBackground = null
    if (lastTextIconColor == Color.TRANSPARENT) {
      lastTextIconColor = if (color.isColorLight()) BLACK else WHITE
    }
    invalidateIconTextColor(color, lastTextIconColor)
  }

  private fun onState(state: State) {
    colorSubs?.clear()
    colorSubs = CompositeDisposable()
    this.iconTextMode = state.iconTextMode

    when (state.iconTextMode) {
      BottomNavIconTextMode.SELECTED_PRIMARY ->
        colorSubs += get().colorPrimary()
            .distinctToMainThread()
            .subscribeTo {
              lastTextIconColor = it
              invalidateWithBackgroundColor()
            }

      BottomNavIconTextMode.SELECTED_ACCENT ->
        colorSubs += get().colorAccent()
            .distinctToMainThread()
            .subscribeTo {
              lastTextIconColor = it
              invalidateWithBackgroundColor()
            }

      BottomNavIconTextMode.BLACK_WHITE_AUTO -> {
        // We will automatically set the icon/text color when the background color is set
        lastTextIconColor = Color.TRANSPARENT
        invalidateWithBackgroundColor()
      }

      BottomNavIconTextMode.NONE -> {
        // no-op
      }
    }

    when (state.bgMode) {
      BottomNavBgMode.PRIMARY ->
        colorSubs += get().colorPrimary()
            .distinctToMainThread()
            .subscribeBackgroundColor(this)

      BottomNavBgMode.PRIMARY_DARK ->
        colorSubs += get().colorStatusBar()
            .distinctToMainThread()
            .subscribeBackgroundColor(this)

      BottomNavBgMode.ACCENT ->
        colorSubs += get().colorAccent()
            .distinctToMainThread()
            .subscribeBackgroundColor(this)

      BottomNavBgMode.BLACK_WHITE_AUTO ->
        setBackgroundColor(
            context.color(
                if (state.isDark) R.color.ate_bottom_nav_default_dark_bg
                else R.color.ate_bottom_nav_default_light_bg
            )
        )

      BottomNavBgMode.NONE -> {
        // no-op
      }
    }
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    combine(
        get().bottomNavigationBackgroundMode(),
        get().bottomNavigationIconTextMode(),
        get().isDark
    ) { bgMode, iconTextMode, isDark -> State(bgMode, iconTextMode, isDark) }
        .distinctToMainThread()
        .subscribeTo(::onState)
        .unsubscribeOnDetach(this)
  }

  override fun onDetachedFromWindow() {
    colorSubs?.clear()
    super.onDetachedFromWindow()
  }

  private fun invalidateWithBackgroundColor() {
    if (backgroundColor != null) {
      setBackgroundColor(backgroundColor!!)
    }
  }

  private data class State(
    val bgMode: BottomNavBgMode,
    val iconTextMode: BottomNavIconTextMode,
    val isDark: Boolean
  )
}

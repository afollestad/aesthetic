/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.support.annotation.ColorInt
import android.util.AttributeSet
import com.afollestad.aesthetic.Aesthetic
import com.afollestad.aesthetic.BottomNavBgMode
import com.afollestad.aesthetic.BottomNavBgMode.ACCENT
import com.afollestad.aesthetic.BottomNavBgMode.PRIMARY
import com.afollestad.aesthetic.BottomNavBgMode.PRIMARY_DARK
import com.afollestad.aesthetic.BottomNavIconTextMode
import com.afollestad.aesthetic.BottomNavIconTextMode.BLACK_WHITE_AUTO
import com.afollestad.aesthetic.BottomNavIconTextMode.SELECTED_ACCENT
import com.afollestad.aesthetic.BottomNavIconTextMode.SELECTED_PRIMARY
import com.afollestad.aesthetic.R.color
import com.afollestad.aesthetic.actions.ViewBackgroundAction
import com.afollestad.aesthetic.utils.adjustAlpha
import com.afollestad.aesthetic.utils.color
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.isColorLight
import com.afollestad.aesthetic.utils.onErrorLogAndRethrow
import com.afollestad.aesthetic.utils.plusAssign
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.reactivex.Observable.combineLatest
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function3

/** @author Aidan Follestad (afollestad)
 */
class AestheticBottomNavigationView(
  context: Context?,
  attrs: AttributeSet? = null
) : BottomNavigationView(context, attrs) {

  private var modesSubscription: Disposable? = null
  private var colorSubs: CompositeDisposable? = null
  private var lastTextIconColor: Int = 0

  private fun invalidateIconTextColor(
    backgroundColor: Int,
    selectedColor: Int
  ) {
    val baseColor = context.color(
        if (backgroundColor.isColorLight()) color.ate_icon_light else color.ate_icon_dark
    )
    val unselectedIconTextColor = baseColor.adjustAlpha(.87f)
    val iconColor = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_checked)
        ),
        intArrayOf(unselectedIconTextColor, selectedColor)
    )
    val textColor = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_checked)
        ),
        intArrayOf(unselectedIconTextColor, selectedColor)
    )
    itemIconTintList = iconColor
    itemTextColor = textColor
  }

  override fun setBackgroundColor(@ColorInt color: Int) {
    super.setBackgroundColor(color)
    if (lastTextIconColor == Color.TRANSPARENT) {
      lastTextIconColor = if (color.isColorLight()) Color.BLACK else Color.WHITE
    }
    invalidateIconTextColor(color, lastTextIconColor)
  }

  private fun onState(state: State) {
    colorSubs?.clear()
    colorSubs = CompositeDisposable()

    when (state.iconTextMode) {
      SELECTED_PRIMARY ->
        colorSubs +=
            Aesthetic.get()
                .colorPrimary()
                .distinctToMainThread()
                .subscribe(
                    Consumer { lastTextIconColor = it },
                    onErrorLogAndRethrow()
                )

      SELECTED_ACCENT ->
        colorSubs +=
            Aesthetic.get()
                .colorAccent()
                .distinctToMainThread()
                .subscribe(
                    Consumer { lastTextIconColor = it },
                    onErrorLogAndRethrow()
                )
      BLACK_WHITE_AUTO ->
        // We will automatically set the icon/text color when the background color is set
        lastTextIconColor = Color.TRANSPARENT
    }

    when (state.bgMode) {
      PRIMARY ->
        colorSubs +=
            Aesthetic.get()
                .colorPrimary()
                .distinctToMainThread()
                .subscribe(
                    ViewBackgroundAction(this),
                    onErrorLogAndRethrow()
                )
      PRIMARY_DARK -> colorSubs +=
          Aesthetic.get()
              .colorStatusBar()
              .distinctToMainThread()
              .subscribe(
                  ViewBackgroundAction(this),
                  onErrorLogAndRethrow()
              )
      ACCENT -> colorSubs +=
          Aesthetic.get()
              .colorAccent()
              .distinctToMainThread()
              .subscribe(
                  ViewBackgroundAction(this),
                  onErrorLogAndRethrow()
              )
      BottomNavBgMode.BLACK_WHITE_AUTO ->
        setBackgroundColor(
            context.color(
                if (state.isDark)
                  color.ate_bottom_nav_default_dark_bg
                else
                  color.ate_bottom_nav_default_light_bg
            )
        )
    }
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    modesSubscription =
        combineLatest(
            Aesthetic.get().bottomNavigationBackgroundMode(),
            Aesthetic.get().bottomNavigationIconTextMode(),
            Aesthetic.get().isDark,
            State.creator()
        )
            .distinctToMainThread()
            .subscribe(
                Consumer { onState(it) },
                onErrorLogAndRethrow()
            )
  }

  override fun onDetachedFromWindow() {
    modesSubscription?.dispose()
    colorSubs?.clear()
    super.onDetachedFromWindow()
  }

  private data class State(
    val bgMode: BottomNavBgMode,
    val iconTextMode: BottomNavIconTextMode,
    val isDark: Boolean
  ) {
    companion object {

      internal fun creator(): Function3<BottomNavBgMode, BottomNavIconTextMode, Boolean, State> {
        return Function3 { bgMode, iconTextMode, isDark ->
          State(
              bgMode, iconTextMode, isDark
          )
        }
      }
    }
  }
}

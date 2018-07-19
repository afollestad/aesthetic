package com.afollestad.aesthetic

import android.annotation.SuppressLint

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import com.afollestad.aesthetic.NavigationViewMode.SELECTED_ACCENT
import com.afollestad.aesthetic.NavigationViewMode.SELECTED_PRIMARY
import com.afollestad.aesthetic.utils.adjustAlpha
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.onErrorLogAndRethrow
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/** @author Aidan Follestad (afollestad) */
@SuppressLint("RestrictedApi")
class AestheticNavigationView(
  context: Context,
  attrs: AttributeSet? = null
) : NavigationView(context, attrs) {

  private var modeSubscription: Disposable? = null
  private var colorSubscription: Disposable? = null

  private fun invalidateColors(state: ColorIsDarkState) {
    val selectedColor = state.color
    val isDark = state.isDark
    val baseColor = if (isDark) Color.WHITE else Color.BLACK
    val unselectedIconColor = baseColor.adjustAlpha(.54f)
    val unselectedTextColor = baseColor.adjustAlpha(.87f)
    val selectedItemBgColor = ContextCompat.getColor(
        context,
        if (isDark)
          R.color.ate_navigation_drawer_selected_dark
        else
          R.color.ate_navigation_drawer_selected_light
    )

    val iconSl = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_checked)
        ),
        intArrayOf(unselectedIconColor, selectedColor)
    )
    val textSl = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_checked)
        ),
        intArrayOf(unselectedTextColor, selectedColor)
    )
    itemTextColor = textSl
    itemIconTintList = iconSl

    val bgDrawable = StateListDrawable()
    bgDrawable.addState(
        intArrayOf(android.R.attr.state_checked), ColorDrawable(selectedItemBgColor)
    )
    itemBackground = bgDrawable
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    modeSubscription = Aesthetic.get()
        .navigationViewMode()
        .distinctToMainThread()
        .subscribe(
            Consumer {
              when (it) {
                SELECTED_PRIMARY ->
                  colorSubscription = Observable.combineLatest(
                      Aesthetic.get().colorPrimary(),
                      Aesthetic.get().isDark,
                      ColorIsDarkState.creator()
                  )
                      .distinctToMainThread()
                      .subscribe(Consumer { this.invalidateColors(it) },
                          onErrorLogAndRethrow()
                      )
                SELECTED_ACCENT ->
                  colorSubscription = Observable.combineLatest(
                      Aesthetic.get().colorAccent(),
                      Aesthetic.get().isDark,
                      ColorIsDarkState.creator()
                  )
                      .distinctToMainThread()
                      .subscribe(Consumer { this.invalidateColors(it) },
                          onErrorLogAndRethrow()
                      )
                else -> throw IllegalStateException("Unknown nav view mode: $it")
              }
            },
            onErrorLogAndRethrow()
        )
  }

  override fun onDetachedFromWindow() {
    modeSubscription?.dispose()
    colorSubscription?.dispose()
    super.onDetachedFromWindow()
  }
}

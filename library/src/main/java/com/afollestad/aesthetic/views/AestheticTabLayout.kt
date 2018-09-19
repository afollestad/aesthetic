/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.support.annotation.ColorInt
import android.util.AttributeSet
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.TabLayoutBgMode
import com.afollestad.aesthetic.TabLayoutIndicatorMode
import com.afollestad.aesthetic.utils.TintHelper.createTintedDrawable
import com.afollestad.aesthetic.utils.adjustAlpha
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.one
import com.afollestad.aesthetic.utils.plusAssign
import com.afollestad.aesthetic.utils.subscribeBackgroundColor
import com.afollestad.aesthetic.utils.subscribeTo
import com.google.android.material.tabs.TabLayout
import io.reactivex.Observable.just
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/** @author Aidan Follestad (afollestad) */
class AestheticTabLayout(
  context: Context,
  attrs: AttributeSet? = null
) : TabLayout(context, attrs) {

  private var topLevelSubs: CompositeDisposable? = null
  private var indicatorColorSubscription: Disposable? = null
  private var bgColorSubscription: Disposable? = null

  companion object {
    const val UNFOCUSED_ALPHA = 0.5f
  }

  private fun setIconsColor(color: Int) {
    val sl = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_selected),
            intArrayOf(android.R.attr.state_selected)
        ),
        intArrayOf(
            color.adjustAlpha(
                UNFOCUSED_ALPHA
            ), color
        )
    )

    for (i in 0 until tabCount) {
      val tab = getTabAt(i)
      if (tab != null && tab.icon != null) {
        tab.icon = createTintedDrawable(tab.icon, sl)
      }
    }
  }

  @SuppressLint("CheckResult")
  override fun setBackgroundColor(@ColorInt color: Int) {
    super.setBackgroundColor(color)

    get().colorIconTitle(just(color))
        .one()
        .subscribe { (activeColor, inactiveColor) ->
          setIconsColor(activeColor)
          setTabTextColors(
              inactiveColor.adjustAlpha(
                  UNFOCUSED_ALPHA
              ),
              activeColor
          )
        }
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    topLevelSubs = CompositeDisposable()

    topLevelSubs += get().tabLayoutBackgroundMode()
        .distinctToMainThread()
        .subscribeTo {
          bgColorSubscription?.dispose()
          bgColorSubscription = when (it) {
            TabLayoutBgMode.PRIMARY ->
              get().colorPrimary()
                  .distinctToMainThread()
                  .subscribeBackgroundColor(this@AestheticTabLayout)

            TabLayoutBgMode.ACCENT ->
              get().colorAccent()
                  .distinctToMainThread()
                  .subscribeBackgroundColor(this@AestheticTabLayout)
          }
        }

    topLevelSubs += get().tabLayoutIndicatorMode()
        .distinctToMainThread()
        .subscribeTo {
          indicatorColorSubscription?.dispose()
          indicatorColorSubscription = when (it) {
            TabLayoutIndicatorMode.PRIMARY ->
              get().colorPrimary()
                  .distinctToMainThread()
                  .subscribeTo(::setSelectedTabIndicatorColor)

            TabLayoutIndicatorMode.ACCENT ->
              get().colorAccent()
                  .distinctToMainThread()
                  .subscribeTo(::setSelectedTabIndicatorColor)
          }
        }
  }

  override fun onDetachedFromWindow() {
    topLevelSubs?.dispose()
    bgColorSubscription?.dispose()
    indicatorColorSubscription?.dispose()
    super.onDetachedFromWindow()
  }
}

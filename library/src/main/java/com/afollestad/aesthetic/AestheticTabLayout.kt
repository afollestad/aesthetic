package com.afollestad.aesthetic

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.support.annotation.ColorInt
import android.support.design.widget.TabLayout
import android.util.AttributeSet
import com.afollestad.aesthetic.TabLayoutBgMode.ACCENT
import com.afollestad.aesthetic.TabLayoutBgMode.PRIMARY
import com.afollestad.aesthetic.utils.TintHelper.createTintedDrawable
import com.afollestad.aesthetic.utils.adjustAlpha
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.onErrorLogAndRethrow
import com.afollestad.aesthetic.utils.one
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/** @author Aidan Follestad (afollestad) */
class AestheticTabLayout(
  context: Context,
  attrs: AttributeSet? = null
) : TabLayout(context, attrs) {

  private var indicatorModeSubscription: Disposable? = null
  private var bgModeSubscription: Disposable? = null
  private var indicatorColorSubscription: Disposable? = null
  private var bgColorSubscription: Disposable? = null

  companion object {
    const val UNFOCUSED_ALPHA = 0.5f
  }

  private fun setIconsColor(color: Int) {
    val sl = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_selected), intArrayOf(android.R.attr.state_selected)
        ),
        intArrayOf(color.adjustAlpha(UNFOCUSED_ALPHA), color)
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
    Aesthetic.get()
        .colorIconTitle(Observable.just(color))
        .one()
        .subscribe { (activeColor, inactiveColor) ->
          setIconsColor(activeColor)
          setTabTextColors(
              inactiveColor.adjustAlpha(UNFOCUSED_ALPHA),
              activeColor
          )
        }
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    bgModeSubscription = Aesthetic.get()
        .tabLayoutBackgroundMode()
        .distinctToMainThread()
        .subscribe(
            Consumer {
              bgColorSubscription?.dispose()
              when (it) {
                PRIMARY ->
                  bgColorSubscription = Aesthetic.get()
                      .colorPrimary()
                      .distinctToMainThread()
                      .subscribe(
                          ViewBackgroundAction(this@AestheticTabLayout),
                          onErrorLogAndRethrow()
                      )
                ACCENT ->
                  bgColorSubscription = Aesthetic.get()
                      .colorAccent()
                      .distinctToMainThread()
                      .subscribe(
                          ViewBackgroundAction(this@AestheticTabLayout),
                          onErrorLogAndRethrow()
                      )
                else -> throw IllegalStateException("Unimplemented bg mode: $it")
              }
            },
            onErrorLogAndRethrow()
        )

    indicatorModeSubscription = Aesthetic.get()
        .tabLayoutIndicatorMode()
        .distinctToMainThread()
        .subscribe(
            Consumer {
              indicatorColorSubscription?.dispose()
              when (it) {
                PRIMARY ->
                  indicatorColorSubscription = Aesthetic.get()
                      .colorPrimary()
                      .distinctToMainThread()
                      .subscribe(
                          Consumer { this.setSelectedTabIndicatorColor(it) },
                          onErrorLogAndRethrow()
                      )
                ACCENT ->
                  indicatorColorSubscription = Aesthetic.get()
                      .colorAccent()
                      .distinctToMainThread()
                      .subscribe(
                          Consumer { this.setSelectedTabIndicatorColor(it) },
                          onErrorLogAndRethrow()
                      )
                else -> throw IllegalStateException("Unimplemented bg mode: $it")
              }
            },
            onErrorLogAndRethrow()
        )
  }

  override fun onDetachedFromWindow() {
    bgModeSubscription?.dispose()
    indicatorModeSubscription?.dispose()
    bgColorSubscription?.dispose()
    indicatorColorSubscription?.dispose()
    super.onDetachedFromWindow()
  }
}

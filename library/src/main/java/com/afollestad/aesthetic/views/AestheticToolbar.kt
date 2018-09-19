/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.util.AttributeSet
import androidx.appcompat.widget.Toolbar
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.BgIconColorState
import com.afollestad.aesthetic.BgIconColorState.Companion.creator
import com.afollestad.aesthetic.utils.TintHelper.createTintedDrawable
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.setOverflowButtonColor
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.tintMenu
import io.reactivex.Observable.combineLatest
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

/** @author Aidan Follestad (afollestad) */
class AestheticToolbar(
  context: Context,
  attrs: AttributeSet? = null
) : Toolbar(context, attrs) {

  private var lastState: BgIconColorState? = null
  private var subscription: Disposable? = null
  private var onColorUpdated = PublishSubject.create<Int>()

  private fun invalidateColors(state: BgIconColorState) {
    lastState = state
    setBackgroundColor(state.bgColor)
    val iconTitleColors = state.iconTitleColor
    if (iconTitleColors != null) {
      setTitleTextColor(iconTitleColors.activeColor)
      setOverflowButtonColor(iconTitleColors.activeColor)
      tintMenu(menu, iconTitleColors)
    }
    if (navigationIcon != null) {
      this.navigationIcon = navigationIcon
    }
    onColorUpdated.onNext(state.bgColor)
  }

  fun colorUpdated() = onColorUpdated

  override fun setNavigationIcon(icon: Drawable?) {
    if (lastState == null) {
      super.setNavigationIcon(icon)
      return
    }
    val iconTitleColors = lastState!!.iconTitleColor
    if (iconTitleColors != null) {
      super.setNavigationIcon(createTintedDrawable(icon, iconTitleColors.toEnabledSl()))
    } else {
      super.setNavigationIcon(icon)
    }
  }

  fun setNavigationIcon(icon: Drawable?, @ColorInt color: Int) {
    if (lastState == null) {
      super.setNavigationIcon(icon)
      return
    }
    super.setNavigationIcon(createTintedDrawable(icon, color))
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    subscription = combineLatest(
        get().colorPrimary(),
        get().colorIconTitle(null),
        creator()
    )
        .distinctToMainThread()
        .subscribeTo(::invalidateColors)
  }

  override fun onDetachedFromWindow() {
    lastState = null
    subscription?.dispose()
    super.onDetachedFromWindow()
  }
}

/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.Toolbar
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.BgIconColorState
import com.afollestad.aesthetic.utils.allOf
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.setOverflowButtonColor
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.tint
import com.afollestad.aesthetic.utils.tintMenu
import com.afollestad.aesthetic.utils.unsubscribeOnDetach
import io.reactivex.subjects.PublishSubject

/** @author Aidan Follestad (afollestad) */
class AestheticToolbar(
  context: Context,
  attrs: AttributeSet? = null
) : Toolbar(context, attrs) {

  private var lastState: BgIconColorState? = null
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
    val iconTitleColors = lastState?.iconTitleColor
    if (iconTitleColors != null) {
      super.setNavigationIcon(icon.tint(iconTitleColors.toEnabledSl()))
    } else {
      super.setNavigationIcon(icon)
    }
  }

  fun setNavigationIcon(icon: Drawable?, @ColorInt color: Int) {
    if (lastState == null) {
      super.setNavigationIcon(icon)
      return
    }
    super.setNavigationIcon(icon.tint(color))
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    allOf(
        get().colorPrimary(),
        get().colorIconTitle()
    ) { color, iconTitleColors -> BgIconColorState(color, iconTitleColors) }
        .distinctToMainThread()
        .subscribeTo(::invalidateColors)
        .unsubscribeOnDetach(this)
  }

  override fun onDetachedFromWindow() {
    lastState = null
    super.onDetachedFromWindow()
  }
}

package com.afollestad.aesthetic

import com.afollestad.aesthetic.utils.TintHelper.createTintedDrawable

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.onErrorLogAndRethrow
import com.afollestad.aesthetic.utils.setOverflowButtonColor
import com.afollestad.aesthetic.utils.tintMenu
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject

/** @author Aidan Follestad (afollestad) */
class AestheticToolbar(
  context: Context,
  attrs: AttributeSet? = null
) : Toolbar(context, attrs) {

  private var lastState: BgIconColorState? = null
  private var subscription: Disposable? = null
  private var onColorUpdated: PublishSubject<Int>? = null

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
    onColorUpdated!!.onNext(state.bgColor)
  }

  fun colorUpdated(): Observable<Int>? {
    return onColorUpdated
  }

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
    onColorUpdated = PublishSubject.create()
    subscription = Observable.combineLatest(
        Aesthetic.get().colorPrimary(),
        Aesthetic.get().colorIconTitle(null),
        BgIconColorState.creator()
    )
        .distinctToMainThread()
        .subscribe(
            Consumer { this.invalidateColors(it) },
            onErrorLogAndRethrow()
        )
  }

  override fun onDetachedFromWindow() {
    lastState = null
    onColorUpdated = null
    subscription?.dispose()
    super.onDetachedFromWindow()
  }
}

package com.afollestad.aesthetic.utils

import android.content.Context
import android.support.annotation.IdRes
import com.afollestad.aesthetic.Aesthetic
import com.afollestad.aesthetic.R.attr
import io.reactivex.Observable

/** @author Aidan Follestad (afollestad) */
object ViewUtil {

  fun getObservableForResId(
    context: Context,
    @IdRes resId: Int,
    fallback: Observable<Int>?
  ): Observable<Int>? {
    with(Aesthetic.get()) {
      return when (resId) {
        0 -> fallback
        context.resId(attr.colorPrimary) -> colorPrimary()
        context.resId(attr.colorPrimaryDark) -> colorPrimaryDark()
        context.resId(android.R.attr.statusBarColor) -> colorStatusBar()
        context.resId(attr.colorAccent) -> colorAccent()
        context.resId(android.R.attr.windowBackground) -> colorWindowBackground()
        context.resId(android.R.attr.textColorPrimary) -> textColorPrimary()
        context.resId(android.R.attr.textColorPrimaryInverse) -> textColorPrimaryInverse()
        context.resId(android.R.attr.textColorSecondary) -> textColorSecondary()
        context.resId(android.R.attr.textColorSecondaryInverse) -> textColorSecondaryInverse()
        else -> fallback
      }
    }
  }
}

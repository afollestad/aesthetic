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
    when (resId) {
      0 -> return fallback
      context.resId(
          attr.colorPrimary, 0) -> return Aesthetic.get()
          .colorPrimary()
      context.resId(
          attr.colorPrimaryDark, 0) -> return Aesthetic.get()
          .colorPrimaryDark()
      context.resId(android.R.attr.statusBarColor, 0) -> return Aesthetic.get()
          .colorStatusBar()
      context.resId(
          attr.colorAccent, 0) -> return Aesthetic.get()
          .colorAccent()
      context.resId(android.R.attr.windowBackground, 0) -> return Aesthetic.get()
          .colorWindowBackground()
      context.resId(android.R.attr.textColorPrimary, 0) -> return Aesthetic.get()
          .textColorPrimary()
      context.resId(android.R.attr.textColorPrimaryInverse, 0) -> return Aesthetic.get()
          .textColorPrimaryInverse()
      context.resId(android.R.attr.textColorSecondary, 0) -> return Aesthetic.get()
          .textColorSecondary()
      context.resId(android.R.attr.textColorSecondaryInverse, 0) -> return Aesthetic.get()
          .textColorSecondaryInverse()
      else -> return fallback
    }
  }
}

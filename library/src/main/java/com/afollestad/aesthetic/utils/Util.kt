/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.utils

import android.content.Context
import android.support.annotation.IdRes
import com.afollestad.aesthetic.Aesthetic
import com.afollestad.aesthetic.R.attr
import io.reactivex.Observable

fun watchColor(
  context: Context,
  @IdRes resId: Int,
  fallback: Observable<Int>?
): Observable<Int>? {
  with(Aesthetic.get()) {
    return when (resId) {
      0 -> fallback
      context.resId(attr.colorPrimary),
      context.resId(android.R.attr.colorPrimary) -> colorPrimary()

      context.resId(attr.colorPrimaryDark),
      context.resId(android.R.attr.colorPrimaryDark) -> colorPrimaryDark()

      context.resId(attr.colorAccent),
      context.resId(android.R.attr.colorAccent) -> colorAccent()

      context.resId(android.R.attr.statusBarColor) -> colorStatusBar()
      context.resId(android.R.attr.windowBackground) -> colorWindowBackground()
      
      context.resId(android.R.attr.textColorPrimary) -> textColorPrimary()
      context.resId(android.R.attr.textColorPrimaryInverse) -> textColorPrimaryInverse()
      context.resId(android.R.attr.textColorSecondary) -> textColorSecondary()
      context.resId(android.R.attr.textColorSecondaryInverse) -> textColorSecondaryInverse()

      else -> fallback
    }
  }
}

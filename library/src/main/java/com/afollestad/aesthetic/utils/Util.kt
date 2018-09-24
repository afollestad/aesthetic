/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.utils

import android.content.Context
import androidx.annotation.CheckResult
import androidx.annotation.IdRes
import com.afollestad.aesthetic.Aesthetic
import com.afollestad.aesthetic.R.attr
import io.reactivex.Observable
import io.reactivex.Observable.empty

@CheckResult fun watchColor(
  context: Context,
  @IdRes resId: Int,
  fallback: Observable<Int> = empty()
): Observable<Int> {
  with(Aesthetic.get()) {
    return when (resId) {
      0 -> fallback
      context.resId(attrId = attr.colorPrimary),
      context.resId(attrId = android.R.attr.colorPrimary) -> colorPrimary()

      context.resId(attrId = attr.colorPrimaryDark),
      context.resId(attrId = android.R.attr.colorPrimaryDark) -> colorPrimaryDark()

      context.resId(attrId = attr.colorAccent),
      context.resId(attrId = android.R.attr.colorAccent) -> colorAccent()

      context.resId(attrId = android.R.attr.statusBarColor) -> colorStatusBar()
      context.resId(attrId = android.R.attr.windowBackground) -> colorWindowBackground()

      context.resId(attrId = android.R.attr.textColorPrimary) -> textColorPrimary()
      context.resId(attrId = android.R.attr.textColorPrimaryInverse) -> textColorPrimaryInverse()

      context.resId(attrId = android.R.attr.textColorSecondary) -> textColorSecondary()
      context.resId(attrId = android.R.attr.textColorSecondaryInverse) ->
        textColorSecondaryInverse()

      else -> fallback
    }
  }
}

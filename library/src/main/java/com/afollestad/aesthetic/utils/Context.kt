/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.util.AttributeSet
import androidx.core.content.ContextCompat

@ColorInt internal fun Context.color(@ColorRes color: Int): Int {
  return ContextCompat.getColor(this, color)
}

internal fun Context.drawable(@DrawableRes drawable: Int): Drawable? {
  return ContextCompat.getDrawable(this, drawable)
}

@ColorInt internal fun Context.colorAttr(@AttrRes attr: Int, @ColorInt fallback: Int = 0): Int {
  val a = theme.obtainStyledAttributes(intArrayOf(attr))
  return try {
    a.getColor(0, fallback)
  } catch (ignored: Throwable) {
    fallback
  } finally {
    a.recycle()
  }
}

internal fun Context.resId(
  @AttrRes attr: Int,
  fallback: Int = 0
): Int {
  val a = theme.obtainStyledAttributes(intArrayOf(attr))
  try {
    return a.getResourceId(0, fallback)
  } finally {
    a.recycle()
  }
}

internal fun Context.resId(
  attrs: AttributeSet?,
  @AttrRes attrId: Int
): Int {
  if (attrs == null) {
    return 0
  }
  val ta = obtainStyledAttributes(attrs, intArrayOf(attrId))
  val result = ta.getResourceId(0, 0)
  ta.recycle()
  return result
}

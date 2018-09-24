/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
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

@SuppressLint("Recycle")
@IdRes
internal fun Context.resId(
  attrs: AttributeSet? = null,
  @AttrRes attrId: Int,
  fallback: Int = 0
): Int {
  val typedArray = if (attrs != null) {
    obtainStyledAttributes(attrs, intArrayOf(attrId))
  } else {
    theme.obtainStyledAttributes(intArrayOf(attrId))
  }
  try {
    return typedArray.getResourceId(0, fallback)
  } finally {
    typedArray.recycle()
  }
}

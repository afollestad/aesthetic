package com.afollestad.aesthetic.utils

import android.content.Context
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.IdRes
import android.support.v4.content.ContextCompat
import android.util.AttributeSet

@ColorInt internal fun Context.color(@ColorRes color: Int): Int {
  return ContextCompat.getColor(this, color)
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

@IdRes internal fun Context.resId(
  @AttrRes attr: Int,
  fallback: Int
): Int {
  val a = theme.obtainStyledAttributes(intArrayOf(attr))
  try {
    return a.getResourceId(0, fallback)
  } finally {
    a.recycle()
  }
}

@IdRes internal fun Context.resId(
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
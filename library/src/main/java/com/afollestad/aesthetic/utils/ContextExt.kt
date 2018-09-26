/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import java.lang.reflect.Array
import java.lang.reflect.Field

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

private var mConstructorArgsField: Field? = null

/**
 * Gets around an issue existing before API 16. See
 * https://github.com/afollestad/aesthetic/issues/101.
 */
internal fun Context.fixedLayoutInflater(): LayoutInflater {
  val inflater = LayoutInflater.from(this)
  if (SDK_INT >= O) return inflater

  if (mConstructorArgsField == null) {
    //mConstructorArgs
    mConstructorArgsField = LayoutInflater::class.findField("mConstructorArgs")
  }
  val constructorArgs = mConstructorArgsField!!.get(inflater)
  Array.set(constructorArgs, 0, this)
  mConstructorArgsField!!.set(inflater, constructorArgs)

  return inflater
}

/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.utils

import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O

@TargetApi(O)
internal fun PackageManager.getAppIcon(packageName: String): Bitmap? {
  try {
    val drawable = getApplicationIcon(packageName)
    if (drawable is BitmapDrawable) {
      return drawable.bitmap
    } else if (SDK_INT >= O && drawable is AdaptiveIconDrawable) {
      val drr = arrayOf(drawable.background, drawable.foreground)
      val layerDrawable = LayerDrawable(drr)

      val width = layerDrawable.intrinsicWidth
      val height = layerDrawable.intrinsicHeight

      val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)

      layerDrawable.setBounds(0, 0, canvas.width, canvas.height)
      layerDrawable.draw(canvas)

      return bitmap
    }
  } catch (e: PackageManager.NameNotFoundException) {
    e.printStackTrace()
  }

  return null
}

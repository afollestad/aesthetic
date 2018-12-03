/**
 * Designed and developed by Aidan Follestad (@afollestad)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

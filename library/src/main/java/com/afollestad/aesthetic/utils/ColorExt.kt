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

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange

@ColorInt internal fun Int.blendWith(
  @ColorInt otherColor: Int,
  ratio: Float
): Int {
  val inverseRatio = 1f - ratio
  val a = Color.alpha(this) * inverseRatio + Color.alpha(otherColor) * ratio
  val r = Color.red(this) * inverseRatio + Color.red(otherColor) * ratio
  val g = Color.green(this) * inverseRatio + Color.green(otherColor) * ratio
  val b = Color.blue(this) * inverseRatio + Color.blue(otherColor) * ratio
  return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
}

@ColorInt internal fun Int.stripAlpha(): Int {
  return Color.rgb(
      Color.red(this),
      Color.green(this),
      Color.blue(this)
  )
}

@ColorInt internal fun Int.adjustAlpha(factor: Float): Int {
  val alpha = Math.round(Color.alpha(this) * factor)
  val red = Color.red(this)
  val green = Color.green(this)
  val blue = Color.blue(this)
  return Color.argb(alpha, red, green, blue)
}

@ColorInt
internal fun Int.shiftColor(
  @FloatRange(from = 0.0, to = 2.0) by: Float
): Int {
  if (by == 1f) return this
  val hsv = FloatArray(3)
  Color.colorToHSV(this, hsv)
  hsv[2] *= by // value component
  return Color.HSVToColor(hsv)
}

@ColorInt
internal fun Int.darkenColor(): Int {
  return shiftColor(0.9f)
}

internal fun Int.isColorLight(): Boolean {
  if (this == Color.BLACK) {
    return false
  } else if (this == Color.WHITE || this == Color.TRANSPARENT) {
    return true
  }
  val darkness =
    1 - (0.299 * Color.red(this) +
        0.587 * Color.green(this) +
        0.114 * Color.blue(this)) / 255
  return darkness < 0.4
}

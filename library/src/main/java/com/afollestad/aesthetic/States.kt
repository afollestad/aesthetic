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
package com.afollestad.aesthetic

import android.R.attr
import android.content.res.ColorStateList
import androidx.annotation.ColorInt

/** @author Aidan Follestad (afollestad) */
internal data class ActiveInactiveColors(
  @field:ColorInt val activeColor: Int,
  @field:ColorInt val inactiveColor: Int
) {
  fun toEnabledSl(): ColorStateList {
    return ColorStateList(
        arrayOf(
            intArrayOf(attr.state_enabled), intArrayOf(-attr.state_enabled)
        ),
        intArrayOf(activeColor, inactiveColor)
    )
  }
}

/** @author Aidan Follestad (afollestad) */
internal data class ColorIsDarkState(
  @field:ColorInt val color: Int,
  val isDark: Boolean
)

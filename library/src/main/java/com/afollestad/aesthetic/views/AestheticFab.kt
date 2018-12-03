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
package com.afollestad.aesthetic.views

import android.content.Context
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.ColorIsDarkState
import com.afollestad.aesthetic.internal.AttrWizard
import com.afollestad.aesthetic.utils.combine
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.isColorLight
import com.afollestad.aesthetic.utils.observableForAttrName
import com.afollestad.aesthetic.utils.setTintAuto
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.tint
import com.afollestad.aesthetic.utils.unsubscribeOnDetach
import com.google.android.material.floatingactionbutton.FloatingActionButton

/** @author Aidan Follestad (afollestad) */
class AestheticFab(
  context: Context,
  attrs: AttributeSet? = null
) : FloatingActionButton(context, attrs) {

  private val wizard = AttrWizard(context, attrs)
  private val backgroundColorValue = wizard.getRawValue(android.R.attr.background)
  private var iconColor: Int = 0

  private fun invalidateColors(state: ColorIsDarkState) {
    setTintAuto(state.color, true, state.isDark)
    iconColor = if (state.color.isColorLight()) BLACK else WHITE
    setImageDrawable(drawable)
  }

  override fun setImageDrawable(drawable: Drawable?) =
    super.setImageDrawable(drawable?.tint(iconColor))

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    combine(
        get().observableForAttrName(
            backgroundColorValue,
            get().colorAccent()
        )!!,
        get().isDark
    ) { color, isDark -> ColorIsDarkState(color, isDark) }
        .distinctToMainThread()
        .subscribeTo(::invalidateColors)
        .unsubscribeOnDetach(this)
  }
}

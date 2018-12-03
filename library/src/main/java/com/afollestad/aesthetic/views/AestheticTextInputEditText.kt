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
import android.util.AttributeSet
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.ColorIsDarkState
import com.afollestad.aesthetic.internal.AttrWizard
import com.afollestad.aesthetic.utils.combine
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.observableForAttrName
import com.afollestad.aesthetic.utils.setTintAuto
import com.afollestad.aesthetic.utils.subscribeHintTextColor
import com.afollestad.aesthetic.utils.subscribeTextColor
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.unsubscribeOnDetach
import com.google.android.material.textfield.TextInputEditText

/** @author Aidan Follestad (afollestad) */
class AestheticTextInputEditText(
  context: Context,
  attrs: AttributeSet? = null
) : TextInputEditText(context, attrs) {

  private var lastState: ColorIsDarkState? = null
  private val wizard = AttrWizard(context, attrs)
  private val backgroundColorValue = wizard.getRawValue(android.R.attr.background)

  private fun invalidateColors(state: ColorIsDarkState) {
    this.lastState = state
    setTintAuto(state.color, true, state.isDark)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    get().textColorPrimary()
        .distinctToMainThread()
        .subscribeTextColor(this)
        .unsubscribeOnDetach(this)

    get().textColorSecondary()
        .distinctToMainThread()
        .subscribeHintTextColor(this)
        .unsubscribeOnDetach(this)

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

  override fun refreshDrawableState() {
    super.refreshDrawableState()
    if (lastState != null) {
      post { invalidateColors(lastState!!) }
    }
  }
}

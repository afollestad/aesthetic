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

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.ColorIsDarkState
import com.afollestad.aesthetic.R
import com.afollestad.aesthetic.internal.AttrWizard
import com.afollestad.aesthetic.utils.combine
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.observableForAttrName
import com.afollestad.aesthetic.utils.setTintAuto
import com.afollestad.aesthetic.utils.subscribeHintTextColor
import com.afollestad.aesthetic.utils.subscribeTextColor
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.unsubscribeOnDetach

/** @author Aidan Follestad (afollestad) */
@SuppressLint("ResourceType")
class AestheticEditText(
  context: Context,
  attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

  private val wizard = AttrWizard(context, attrs)
  private val tintColorValue = wizard.getRawValue(R.attr.tint)
  private val textColorValue = wizard.getRawValue(android.R.attr.textColor)
  private val textColorHintValue = wizard.getRawValue(android.R.attr.textColorHint)

  private fun invalidateColors(state: ColorIsDarkState) =
    setTintAuto(state.color, true, state.isDark)

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    combine(
        get().observableForAttrName(
            tintColorValue,
            get().colorAccent()
        )!!,
        get().isDark
    ) { color, isDark -> ColorIsDarkState(color, isDark) }
        .distinctToMainThread()
        .subscribeTo(::invalidateColors)
        .unsubscribeOnDetach(this)

    get().observableForAttrName(
        textColorValue,
        get().textColorPrimary()
    )!!
        .distinctToMainThread()
        .subscribeTextColor(this)
        .unsubscribeOnDetach(this)

    get().observableForAttrName(
        textColorHintValue,
        get().textColorSecondary()
    )!!
        .distinctToMainThread()
        .subscribeHintTextColor(this)
        .unsubscribeOnDetach(this)
  }
}

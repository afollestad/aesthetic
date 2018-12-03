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
import com.afollestad.aesthetic.internal.AttrWizard
import com.afollestad.aesthetic.utils.adjustAlpha
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.observableForAttrName
import com.afollestad.aesthetic.utils.setAccentColor
import com.afollestad.aesthetic.utils.setHintColor
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.unsubscribeOnDetach
import com.google.android.material.textfield.TextInputLayout

/** @author Aidan Follestad (afollestad) */
class AestheticTextInputLayout(
  context: Context,
  attrs: AttributeSet? = null
) : TextInputLayout(context, attrs) {

  private val wizard = AttrWizard(context, attrs)
  private val backgroundColorValue = wizard.getRawValue(android.R.attr.background)

  private fun invalidateColors(color: Int) = setAccentColor(color)

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    get().textColorSecondary()
        .distinctToMainThread()
        .subscribeTo { setHintColor(it.adjustAlpha(0.7f)) }
        .unsubscribeOnDetach(this)

    get().observableForAttrName(
        backgroundColorValue,
        get().colorAccent()
    )!!
        .distinctToMainThread()
        .subscribeTo(::invalidateColors)
        .unsubscribeOnDetach(this)
  }
}

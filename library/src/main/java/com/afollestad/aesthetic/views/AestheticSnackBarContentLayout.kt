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
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.subscribeTextColor
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.tint
import com.afollestad.aesthetic.utils.unsubscribeOnDetach
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.SnackbarContentLayout

/** @author Aidan Follestad (afollestad) */
@SuppressLint("RestrictedApi")
internal class AestheticSnackBarContentLayout(
  context: Context,
  attrs: AttributeSet? = null
) : SnackbarContentLayout(context, attrs) {

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    get().snackbarBackgroundColor()
        .distinctToMainThread()
        .subscribeTo(this::invalidateBgColors)
        .unsubscribeOnDetach(this)

    get().snackbarTextColor()
        .distinctToMainThread()
        .subscribeTextColor(messageView)
        .unsubscribeOnDetach(this)

    get().snackbarActionTextColor()
        .distinctToMainThread()
        .subscribeTextColor(actionView)
        .unsubscribeOnDetach(this)
  }

  private fun invalidateBgColors(color: Int) {
    setBackgroundColor(color)
    val parent = this.parent
    if (parent is Snackbar.SnackbarLayout) {
      val background = parent.background
      if (background != null) {
        parent.background = background.tint(color)
      } else {
        parent.setBackgroundColor(color)
      }
    }
  }
}

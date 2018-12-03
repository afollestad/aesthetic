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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.unsubscribeOnDetach

/** @author Aidan Follestad (afollestad) */
@SuppressLint("PrivateResource")
class AestheticSwipeRefreshLayout(
  context: Context,
  attrs: AttributeSet? = null
) : SwipeRefreshLayout(context, attrs) {

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    get().swipeRefreshLayoutColors()
        .distinctToMainThread()
        .subscribeTo(::setColorSchemeColors)
        .unsubscribeOnDetach(this)
  }
}

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

import androidx.annotation.CheckResult
import com.afollestad.aesthetic.Aesthetic
import io.reactivex.Observable
import io.reactivex.Observable.empty

@CheckResult internal fun Aesthetic.observableForAttrName(
  name: String,
  fallback: Observable<Int>? = null
): Observable<Int>? {
  if (name.isNotEmpty() && !name.startsWith('?')) {
    // Don't override the hardcoded or resource value that is set.
    return empty()
  }
  return when (name) {
    "" -> fallback

    "?attr/colorPrimary", "?android:attr/colorPrimary" -> colorPrimary()
    "?attr/colorPrimaryDark", "?android:attr/colorPrimaryDark" -> colorPrimaryDark()
    "?attr/colorAccent", "?android:attr/colorAccent" -> colorAccent()

    "?android:attr/statusBarColor" -> colorStatusBar()
    "?android:attr/navigationBarColor" -> colorNavigationBar()
    "?android:attr/windowBackground" -> colorWindowBackground()

    "?android:attr/textColorPrimary" -> textColorPrimary()
    "?android:attr/textColorPrimaryInverse" -> textColorPrimaryInverse()
    "?android:attr/textColorSecondary" -> textColorSecondary()
    "?android:attr/textColorSecondaryInverse" -> textColorSecondaryInverse()

    else -> fallback ?: attribute(name.substring(1))
  }
}

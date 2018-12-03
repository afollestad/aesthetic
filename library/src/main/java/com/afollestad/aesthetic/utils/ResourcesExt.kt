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

import android.content.res.Resources
import android.util.Log
import androidx.annotation.CheckResult
import com.afollestad.aesthetic.BuildConfig

@CheckResult
internal fun Resources.safeResourceName(resId: Int): String {
  if (resId == 0) {
    return ""
  }
  return try {
    getResourceName(resId)
  } catch (_: Resources.NotFoundException) {
    if (BuildConfig.DEBUG) Log.w("AttrWizard", "Unable to get resource name for $resId")
    ""
  }
}

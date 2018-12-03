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

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.aesthetic.internal.InflationDelegate

/** @author Aidan Follestad (afollestad) */
open class AestheticActivity : AppCompatActivity() {

  open fun getInflationDelegate(): InflationDelegate? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    Aesthetic.attach(this, getInflationDelegate())
    super.onCreate(savedInstanceState)
  }

  override fun onResume() {
    super.onResume()
    Aesthetic.resume(this)
  }

  override fun onPause() {
    Aesthetic.pause(this)
    super.onPause()
  }
}

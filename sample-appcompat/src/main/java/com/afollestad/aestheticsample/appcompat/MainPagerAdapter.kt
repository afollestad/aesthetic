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
package com.afollestad.aestheticsample.appcompat

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/** @author Aidan Follestad (afollestad) */
internal class MainPagerAdapter(
  private val context: Context,
  fm: FragmentManager
) : FragmentStatePagerAdapter(fm) {

  override fun getItem(position: Int): Fragment {
    return if (position == 0) MainFragment() else SecondaryFragment()
  }

  override fun getCount(): Int {
    return 2
  }

  override fun getPageTitle(position: Int): CharSequence? {
    return context.getString(if (position == 0) R.string.main else R.string.other)
  }
}

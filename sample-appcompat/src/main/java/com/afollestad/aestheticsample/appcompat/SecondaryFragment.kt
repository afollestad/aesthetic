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

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_secondary.view.drawer_layout
import kotlinx.android.synthetic.main.fragment_secondary.view.recycler_view
import kotlinx.android.synthetic.main.fragment_secondary.view.swipe_refresh

/** @author Aidan Follestad (afollestad) */
class SecondaryFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_secondary, container, false)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    view.drawer_layout.setOnClickListener {
      startActivity(
          Intent(activity, DrawerActivity::class.java)
      )
    }
    view.recycler_view.setOnClickListener {
      startActivity(
          Intent(activity, RecyclerViewActivity::class.java)
      )
    }
    view.swipe_refresh.setOnClickListener {
      startActivity(
          Intent(activity, SwipeRefreshActivity::class.java)
      )
    }
  }
}

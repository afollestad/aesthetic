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

import android.os.Bundle
import com.afollestad.aesthetic.AestheticActivity
import io.reactivex.Observable.just
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_swipe_refresh.swipe_refresh_layout
import kotlinx.android.synthetic.main.activity_swipe_refresh.toolbar
import java.util.concurrent.TimeUnit.SECONDS

/** @author Aidan Follestad (afollestad) */
class SwipeRefreshActivity : AestheticActivity() {

  private var delayed: Disposable? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_swipe_refresh)

    toolbar.setNavigationOnClickListener { finish() }
    swipe_refresh_layout.setOnRefreshListener {
      delayed = just(true)
          .delay(2, SECONDS)
          .subscribe { swipe_refresh_layout.isRefreshing = false }
    }
  }

  override fun onPause() {
    delayed?.dispose()
    super.onPause()
  }
}

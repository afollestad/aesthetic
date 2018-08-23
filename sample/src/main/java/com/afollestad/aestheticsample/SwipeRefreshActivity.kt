/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aestheticsample

import android.os.Bundle
import com.afollestad.aesthetic.AestheticActivity
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_swipe_refresh.swipe_refresh_layout
import kotlinx.android.synthetic.main.activity_swipe_refresh.toolbar
import java.util.concurrent.TimeUnit

/** @author Aidan Follestad (afollestad) */
class SwipeRefreshActivity : AestheticActivity() {

  private var delayed: Disposable? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_swipe_refresh)

    toolbar.setNavigationOnClickListener { finish() }
    swipe_refresh_layout.setOnRefreshListener {
      delayed = Observable.just(true)
          .delay(2, TimeUnit.SECONDS)
          .subscribe { swipe_refresh_layout.isRefreshing = false }
    }
  }

  override fun onPause() {
    delayed?.dispose()
    super.onPause()
  }
}

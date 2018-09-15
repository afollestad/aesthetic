/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aestheticsample

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.aesthetic.AestheticActivity
import kotlinx.android.synthetic.main.activity_recyclerview.recycler_view
import kotlinx.android.synthetic.main.activity_recyclerview.toolbar

/** @author Aidan Follestad (afollestad) */
class RecyclerViewActivity : AestheticActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_recyclerview)

    toolbar.setNavigationOnClickListener { finish() }
    recycler_view.layoutManager = LinearLayoutManager(this)
    recycler_view.adapter = MainAdapter()
  }
}

package com.afollestad.aestheticsample

import android.os.Bundle
import com.afollestad.aesthetic.AestheticActivity
import kotlinx.android.synthetic.main.activity_collapsing_appbar.toolbar

/** @author Aidan Follestad (afollestad) */
class CoordinatorLayoutActivity : AestheticActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_collapsing_appbar)

    toolbar.inflateMenu(R.menu.coordinatorlayout)
    toolbar.setNavigationOnClickListener { finish() }
  }
}

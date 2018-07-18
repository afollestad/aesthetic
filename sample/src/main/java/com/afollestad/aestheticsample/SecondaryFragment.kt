package com.afollestad.aestheticsample

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_secondary.view.bottom_tabs
import kotlinx.android.synthetic.main.fragment_secondary.view.coordinator_layout
import kotlinx.android.synthetic.main.fragment_secondary.view.drawer_layout
import kotlinx.android.synthetic.main.fragment_secondary.view.recycler_view

/** @author Aidan Follestad (afollestad)
 */
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
    view.coordinator_layout.setOnClickListener {
      startActivity(
          Intent(activity, CoordinatorLayoutActivity::class.java)
      )
    }
    view.bottom_tabs.setOnClickListener {
      startActivity(
          Intent(activity, BottomNavActivity::class.java)
      )
    }
    view.recycler_view.setOnClickListener {
      startActivity(
          Intent(activity, RecyclerViewActivity::class.java)
      )
    }
  }
}

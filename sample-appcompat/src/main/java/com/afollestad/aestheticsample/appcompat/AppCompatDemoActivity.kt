/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aestheticsample.appcompat

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import com.afollestad.aesthetic.Aesthetic
import com.afollestad.aesthetic.AestheticActivity
import com.afollestad.aesthetic.BottomNavBgMode
import com.afollestad.aesthetic.BottomNavIconTextMode
import com.afollestad.aesthetic.NavigationViewMode
import kotlinx.android.synthetic.main.activity_demo_appcompat.pager
import kotlinx.android.synthetic.main.activity_demo_appcompat.tabs
import kotlinx.android.synthetic.main.activity_demo_appcompat.toolbar

/** @author Aidan Follestad (afollestad) */
class AppCompatDemoActivity : AestheticActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_demo_appcompat)

    toolbar.inflateMenu(R.menu.main)
    toolbar.setOnMenuItemClickListener {
      if (it.itemId == R.id.settings) {
        startActivity(Intent(this@AppCompatDemoActivity, SettingsActivity::class.java))
      }
      true
    }

    val searchItem = toolbar.menu.findItem(R.id.search)
    val searchView = searchItem.actionView as SearchView
    searchView.queryHint = getString(R.string.search_view_example)

    // If we haven't set any defaults, do that now
    if (Aesthetic.isFirstTime) {
      Aesthetic.config {
        activityTheme(R.style.AppCompatDemoTheme)
        textColorPrimary(res = R.color.text_color_primary)
        textColorSecondary(res = R.color.text_color_secondary)
        colorPrimary(res = R.color.md_white)
        colorAccent(res = R.color.md_blue)
        colorStatusBarAuto()
        colorNavigationBarAuto()
        textColorPrimary(Color.BLACK)
        navigationViewMode(NavigationViewMode.SELECTED_ACCENT)
        bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY)
        bottomNavigationIconTextMode(BottomNavIconTextMode.SELECTED_ACCENT)
        swipeRefreshLayoutColorsRes(
            R.color.md_blue,
            R.color.md_blue_grey,
            R.color.md_green
        )
        attribute(R.attr.my_custom_attr, res = R.color.md_red)
      }
    }

    pager.adapter =
        MainPagerAdapter(this, supportFragmentManager)
    tabs.setupWithViewPager(pager)
  }
}

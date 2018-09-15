/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aestheticsample

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import com.afollestad.aesthetic.Aesthetic
import com.afollestad.aesthetic.AestheticActivity
import com.afollestad.aesthetic.BottomNavBgMode
import com.afollestad.aesthetic.BottomNavIconTextMode
import com.afollestad.aesthetic.NavigationViewMode
import kotlinx.android.synthetic.main.activity_main.pager
import kotlinx.android.synthetic.main.activity_main.tabs
import kotlinx.android.synthetic.main.activity_main.toolbar

/** @author Aidan Follestad (afollestad) */
class MainActivity : AestheticActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    toolbar.inflateMenu(R.menu.main)
    val searchItem = toolbar.menu.findItem(R.id.search)
    val searchView = searchItem.actionView as SearchView
    searchView.queryHint = getString(R.string.search_view_example)

    // If we haven't set any defaults, do that now
    if (Aesthetic.isFirstTime) {
      Aesthetic.config {
        activityTheme(R.style.AppTheme)
        textColorPrimaryRes(R.color.text_color_primary)
        textColorSecondaryRes(R.color.text_color_secondary)
        colorPrimaryRes(R.color.md_white)
        colorAccentRes(R.color.md_blue)
        colorStatusBarAuto()
        colorNavigationBarAuto()
        textColorPrimary(Color.BLACK)
        navigationViewMode(NavigationViewMode.SELECTED_ACCENT)
        bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY)
        bottomNavigationIconTextMode(BottomNavIconTextMode.SELECTED_ACCENT)
        swipeRefreshLayoutColorsRes(R.color.md_blue, R.color.md_blue_grey, R.color.md_green)
      }
    }

    pager.adapter = MainPagerAdapter(this, supportFragmentManager)
    tabs.setupWithViewPager(pager)
  }
}

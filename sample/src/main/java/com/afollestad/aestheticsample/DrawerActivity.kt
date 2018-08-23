/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aestheticsample

import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import com.afollestad.aesthetic.AestheticActivity
import kotlinx.android.synthetic.main.activity_drawer.drawer_layout
import kotlinx.android.synthetic.main.activity_drawer.navigation_view
import kotlinx.android.synthetic.main.activity_drawer.toolbar

/** @author Aidan Follestad (afollestad) */
class DrawerActivity : AestheticActivity() {

  private lateinit var drawerToggle: ActionBarDrawerToggle

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_drawer)
    setSupportActionBar(toolbar)

    drawerToggle = ActionBarDrawerToggle(
        this, drawer_layout, toolbar, R.string.open_drawer, R.string.close_drawer
    )
    drawer_layout!!.addDrawerListener(drawerToggle)

    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    supportActionBar!!.setHomeButtonEnabled(true)

    navigation_view!!.post { navigation_view!!.setCheckedItem(R.id.item_three) }
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)
    // Sync the toggle state after onRestoreInstanceState has occurred.
    drawerToggle.syncState()
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    drawerToggle.onConfigurationChanged(newConfig)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
  }
}

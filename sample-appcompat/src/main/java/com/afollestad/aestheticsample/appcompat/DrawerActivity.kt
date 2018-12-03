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

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
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
    drawer_layout.addDrawerListener(drawerToggle)

    supportActionBar!!.apply {
      setDisplayHomeAsUpEnabled(true)
      setHomeButtonEnabled(true)
    }

    navigation_view.post { navigation_view.setCheckedItem(R.id.item_three) }
    navigation_view.setNavigationItemSelectedListener {
      navigation_view.setCheckedItem(it.itemId)
      false
    }
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

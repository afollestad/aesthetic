/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aestheticsample

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.afollestad.aesthetic.AestheticActivity
import kotlinx.android.synthetic.main.activity_settings.toolbar

class SettingsFragment : PreferenceFragmentCompat() {

  override fun onCreatePreferences(
    savedInstanceState: Bundle?,
    rootKey: String?
  ) = setPreferencesFromResource(R.xml.preferences, rootKey)
}

class SettingsActivity : AestheticActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings)

    toolbar.setOnClickListener { finish() }

    if (savedInstanceState == null) {
      supportFragmentManager
          .beginTransaction()
          .add(R.id.container, SettingsFragment())
          .commit()
    }
  }
}

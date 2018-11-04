/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.aesthetic.internal.InflationDelegate

/** @author Aidan Follestad (afollestad) */
open class AestheticActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    Aesthetic.attach(this, getInflationDelegate())
    super.onCreate(savedInstanceState)
  }

  open fun getInflationDelegate(): InflationDelegate? = null

  override fun onResume() {
    super.onResume()
    Aesthetic.resume(this)
  }

  override fun onPause() {
    Aesthetic.pause(this)
    super.onPause()
  }
}

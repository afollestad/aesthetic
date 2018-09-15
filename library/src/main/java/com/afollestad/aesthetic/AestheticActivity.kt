/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/** @author Aidan Follestad (afollestad) */
open class AestheticActivity : AppCompatActivity(), AestheticKeyProvider {

  override fun onCreate(savedInstanceState: Bundle?) {
    Aesthetic.attach(this)
    super.onCreate(savedInstanceState)
  }

  override fun onResume() {
    super.onResume()
    Aesthetic.resume(this)
  }

  override fun onPause() {
    Aesthetic.pause(this)
    super.onPause()
  }

  override fun key(): String? {
    return null
  }
}

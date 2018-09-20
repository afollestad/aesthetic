/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.utils

import android.content.SharedPreferences

typealias PrefsEditor = SharedPreferences.Editor

internal fun PrefsEditor.save(exec: PrefsEditor.() -> Unit) {
  this.exec()
  this.apply()
}

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

internal fun SharedPreferences.edit(exec: PrefsEditor.() -> Unit) {
  val editor = this.edit()
  editor.exec()
  editor.apply()
}

internal fun SharedPreferences.clear(vararg keys: String) {
  edit {
    for (key in keys) {
      remove(key)
    }
  }
}

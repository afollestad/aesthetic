/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic

/** @author Aidan Follestad (afollestad) */
enum class AutoSwitchMode(val value: Int) {
  OFF(0),
  ON(1),
  AUTO(2);

  companion object {

    fun fromInt(value: Int): AutoSwitchMode {
      return when (value) {
        0 -> OFF
        1 -> ON
        else -> AUTO
      }
    }
  }
}

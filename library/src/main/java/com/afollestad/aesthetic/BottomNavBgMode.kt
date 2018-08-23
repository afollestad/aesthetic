/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic

/** @author Aidan Follestad (afollestad) */
enum class BottomNavBgMode(val value: Int) {
  BLACK_WHITE_AUTO(0),
  PRIMARY(1),
  PRIMARY_DARK(2),
  ACCENT(3);

  companion object {
    fun fromInt(value: Int): BottomNavBgMode {
      return when (value) {
        0 -> BLACK_WHITE_AUTO
        1 -> PRIMARY
        2 -> PRIMARY_DARK
        else -> ACCENT
      }
    }
  }
}

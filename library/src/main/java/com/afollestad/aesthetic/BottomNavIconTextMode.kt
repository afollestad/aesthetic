/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic

/** @author Aidan Follestad (afollestad) */
enum class BottomNavIconTextMode(val value: Int) {
  SELECTED_PRIMARY(0),
  SELECTED_ACCENT(1),
  BLACK_WHITE_AUTO(2);

  companion object {
    fun fromInt(value: Int): BottomNavIconTextMode {
      return when (value) {
        0 -> SELECTED_PRIMARY
        1 -> SELECTED_ACCENT
        else -> BLACK_WHITE_AUTO
      }
    }
  }
}

package com.afollestad.aesthetic

/** @author Aidan Follestad (afollestad) */
enum class NavigationViewMode(val value: Int) {
  SELECTED_PRIMARY(0),
  SELECTED_ACCENT(1);

  companion object {
    fun fromInt(value: Int): NavigationViewMode {
      return when (value) {
        0 -> SELECTED_PRIMARY
        else -> SELECTED_ACCENT
      }
    }
  }
}

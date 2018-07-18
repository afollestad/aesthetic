package com.afollestad.aesthetic

/** @author Aidan Follestad (afollestad)*/
enum class TabLayoutBgMode(val value: Int) {
  PRIMARY(0),
  ACCENT(1);

  companion object {
    fun fromInt(value: Int): TabLayoutBgMode {
      return when (value) {
        0 -> PRIMARY
        else -> ACCENT
      }
    }
  }
}

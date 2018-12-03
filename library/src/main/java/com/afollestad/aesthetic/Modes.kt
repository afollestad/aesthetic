/**
 * Designed and developed by Aidan Follestad (@afollestad)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("unused")

package com.afollestad.aesthetic

import io.reactivex.Observable

@Deprecated(message = "Use ColorMode instead.", replaceWith = ReplaceWith("ColorMode"))
typealias TabLayoutBgMode = ColorMode

@Deprecated(message = "Use ColorMode instead.", replaceWith = ReplaceWith("ColorMode"))
typealias TabLayoutIndicatorMode = ColorMode

/** @author Aidan Follestad (afollestad)*/
enum class ColorMode(val value: Int) {
  PRIMARY(0),
  ACCENT(1);

  companion object {
    internal fun fromInt(value: Int): ColorMode {
      return when (value) {
        0 -> PRIMARY
        else -> ACCENT
      }
    }
  }
}

internal fun Observable<Int>.mapToColorMode(): Observable<ColorMode> {
  return map { ColorMode.fromInt(it) }
}

/** @author Aidan Follestad (afollestad) */
enum class NavigationViewMode(val value: Int) {
  SELECTED_PRIMARY(0),
  SELECTED_ACCENT(1),
  NONE(2);

  companion object {
    internal fun fromInt(value: Int): NavigationViewMode {
      return when (value) {
        0 -> SELECTED_PRIMARY
        1 -> SELECTED_ACCENT
        else -> NONE
      }
    }
  }
}

internal fun Observable<Int>.mapToNavigationViewMode(): Observable<NavigationViewMode> {
  return map { NavigationViewMode.fromInt(it) }
}

/** @author Aidan Follestad (afollestad) */
enum class AutoSwitchMode(val value: Int) {
  OFF(0),
  ON(1),
  AUTO(2);

  companion object {
    internal fun fromInt(value: Int): AutoSwitchMode {
      return when (value) {
        0 -> OFF
        1 -> ON
        else -> AUTO
      }
    }
  }
}

internal fun Observable<Int>.mapToAutoSwitchMode(): Observable<AutoSwitchMode> {
  return map { AutoSwitchMode.fromInt(it) }
}

/** @author Aidan Follestad (afollestad) */
enum class BottomNavBgMode(val value: Int) {
  BLACK_WHITE_AUTO(0),
  PRIMARY(1),
  PRIMARY_DARK(2),
  ACCENT(3),
  NONE(4);

  companion object {
    internal fun fromInt(value: Int): BottomNavBgMode {
      return when (value) {
        0 -> BLACK_WHITE_AUTO
        1 -> PRIMARY
        2 -> PRIMARY_DARK
        3 -> ACCENT
        else -> NONE
      }
    }
  }
}

internal fun Observable<Int>.mapToBottomNavBgMode(): Observable<BottomNavBgMode> {
  return map { BottomNavBgMode.fromInt(it) }
}

/** @author Aidan Follestad (afollestad) */
enum class BottomNavIconTextMode(val value: Int) {
  SELECTED_PRIMARY(0),
  SELECTED_ACCENT(1),
  BLACK_WHITE_AUTO(2),
  NONE(3);

  companion object {
    internal fun fromInt(value: Int): BottomNavIconTextMode {
      return when (value) {
        0 -> SELECTED_PRIMARY
        1 -> SELECTED_ACCENT
        2 -> BLACK_WHITE_AUTO
        else -> NONE
      }
    }
  }
}

internal fun Observable<Int>.mapToBottomNavIconTextMode(): Observable<BottomNavIconTextMode> {
  return map { BottomNavIconTextMode.fromInt(it) }
}

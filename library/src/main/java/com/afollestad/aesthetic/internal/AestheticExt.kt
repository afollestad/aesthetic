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
package com.afollestad.aesthetic.internal

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.CheckResult
import androidx.drawerlayout.widget.DrawerLayout
import com.afollestad.aesthetic.Aesthetic
import com.afollestad.aesthetic.AutoSwitchMode
import com.afollestad.aesthetic.AutoSwitchMode.OFF
import com.afollestad.aesthetic.AutoSwitchMode.ON
import com.afollestad.aesthetic.R
import com.afollestad.aesthetic.utils.clear
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.getRootView
import com.afollestad.aesthetic.utils.isColorLight
import com.afollestad.aesthetic.utils.safeResourceName
import com.afollestad.aesthetic.utils.setLightNavBarCompat
import com.afollestad.aesthetic.utils.setLightStatusBarCompat
import com.afollestad.aesthetic.utils.setNavBarColorCompat
import com.afollestad.aesthetic.utils.setStatusBarColorCompat
import com.afollestad.aesthetic.utils.subscribeBackgroundColor
import com.afollestad.aesthetic.utils.subscribeHintTextColor
import com.afollestad.aesthetic.utils.subscribeImageViewTint
import com.afollestad.aesthetic.utils.subscribeTextColor
import com.afollestad.aesthetic.utils.unsubscribeOnDetach
import com.afollestad.rxkprefs.rxkPrefs
import io.reactivex.Observable
import java.lang.String.format

@SuppressLint("CommitPrefEdits")
internal fun Aesthetic.initPrefs() {
  rPrefs = rxkPrefs(safeContext, PREFS_NAME)
  prefs = rPrefs!!.getSharedPrefs()
  editor = safePrefs.edit()
  migratePrefs()
  onAttached.onNext(true)
}

internal fun Aesthetic.deInitPrefs() {
  onAttached.onNext(false)
  prefs = null
  editor = null
  rPrefs = null
}

/**
 * Emits the current reactive shared preferences value if and when the instance is attached to
 * an Activity, when the preferences are actually initialized and populated. Without this,
 * we can get Kotlin null exceptions due the instance being unexpectedly null.
 */
internal fun Aesthetic.waitForAttach() = onAttached.filter { it }.map { rPrefs!! }

internal fun addBackgroundSubscriber(
  view: View,
  colorObservable: Observable<Int>?
) {
  if (colorObservable == null) return
  colorObservable
      .distinctToMainThread()
      .subscribeBackgroundColor(view)
      .unsubscribeOnDetach(view)
}

internal fun addTextColorSubscriber(
  view: View,
  colorObservable: Observable<Int>?
) {
  if (colorObservable == null) return
  colorObservable
      .distinctToMainThread()
      .subscribeTextColor(view)
      .unsubscribeOnDetach(view)
}

internal fun addHintTextColorSubscriber(
  view: View,
  colorObservable: Observable<Int>?
) {
  if (colorObservable == null) return
  colorObservable
      .distinctToMainThread()
      .subscribeHintTextColor(view)
      .unsubscribeOnDetach(view)
}

internal fun addImageTintSubscriber(
  view: View,
  colorObservable: Observable<Int>?
) {
  if (colorObservable == null) {
    return
  }
  colorObservable
      .distinctToMainThread()
      .subscribeImageViewTint(view)
      .unsubscribeOnDetach(view)
}

internal fun Aesthetic.invalidateStatusBar(colorAndMode: Pair<Int, AutoSwitchMode>) {
  with(safeContext as? Activity ?: return) {
    val color = colorAndMode.first
    val mode = colorAndMode.second

    val rootView = getRootView()
    if (rootView is DrawerLayout) {
      // Color is set to DrawerLayout, Activity gets transparent status bar
      setLightStatusBarCompat(false)
      setStatusBarColorCompat(Color.TRANSPARENT)
      rootView.setStatusBarBackgroundColor(color)
    } else {
      setStatusBarColorCompat(color)
    }

    when (mode) {
      OFF -> setLightStatusBarCompat(false)
      ON -> setLightStatusBarCompat(true)
      else -> setLightStatusBarCompat(color.isColorLight())
    }
  }
}

internal fun Aesthetic.invalidateNavBar(colorAndMode: Pair<Int, AutoSwitchMode>) {
  with(safeContext as? Activity ?: return) {
    val color = colorAndMode.first
    val mode = colorAndMode.second

    setNavBarColorCompat(color)
    val useLightMode = when (mode) {
      AutoSwitchMode.ON -> true
      AutoSwitchMode.OFF -> false
      else -> color.isColorLight()
    }
    setLightNavBarCompat(useLightMode)
  }
}

@CheckResult internal fun Aesthetic.attrKey(@AttrRes attrId: Int): String {
  var name = safeContext.resources.safeResourceName(attrId)
  if (!name.startsWith("android")) {
    name = name.substring(name.indexOf(':') + 1)
  }
  return attrKey(name)
}

@CheckResult internal fun attrKey(name: String) = format(KEY_ATTRIBUTE, name)

/**
 * Migrates the old preference storage method to the new
 * preference storage method.
 *
 * Basically we go from custom-named parameters to storing
 * values to attribute names.
 */
@Suppress("DEPRECATION")
@SuppressLint("CheckResult")
internal fun Aesthetic.migratePrefs() {
  if (!safePrefs.contains(KEY_PRIMARY_COLOR) &&
      !safePrefs.contains(KEY_ACCENT_COLOR)
  ) {
    // Already migrated
    return
  }

  // Migrate legacy preferences if they exist
  if (safePrefs.contains(KEY_PRIMARY_COLOR)) {
    attribute(
        R.attr.colorPrimary,
        safePrefs.getInt(KEY_PRIMARY_COLOR, 0)
    )
  }
  if (safePrefs.contains(KEY_PRIMARY_DARK_COLOR)) {
    attribute(
        R.attr.colorPrimaryDark,
        safePrefs.getInt(KEY_PRIMARY_DARK_COLOR, 0)
    )
  }
  if (safePrefs.contains(KEY_ACCENT_COLOR)) {
    attribute(
        R.attr.colorAccent,
        safePrefs.getInt(KEY_ACCENT_COLOR, 0)
    )
  }
  if (safePrefs.contains(KEY_PRIMARY_TEXT_COLOR)) {
    attribute(
        android.R.attr.textColorPrimary,
        safePrefs.getInt(KEY_PRIMARY_TEXT_COLOR, 0)
    )
  }
  if (safePrefs.contains(KEY_SECONDARY_TEXT_COLOR)) {
    attribute(
        android.R.attr.textColorSecondary,
        safePrefs.getInt(KEY_SECONDARY_TEXT_COLOR, 0)
    )
  }
  if (safePrefs.contains(KEY_PRIMARY_TEXT_INVERSE_COLOR)) {
    attribute(
        android.R.attr.textColorPrimaryInverse,
        safePrefs.getInt(KEY_PRIMARY_TEXT_INVERSE_COLOR, 0)
    )
  }
  if (safePrefs.contains(KEY_SECONDARY_TEXT_INVERSE_COLOR)) {
    attribute(
        android.R.attr.textColorSecondaryInverse,
        safePrefs.getInt(KEY_SECONDARY_TEXT_INVERSE_COLOR, 0)
    )
  }
  if (safePrefs.contains(KEY_WINDOW_BG_COLOR)) {
    attribute(
        android.R.attr.windowBackground,
        safePrefs.getInt(KEY_WINDOW_BG_COLOR, 0)
    )
  }
  if (safePrefs.contains(KEY_STATUS_BAR_COLOR)) {
    val statusBarColor = safePrefs.getInt(KEY_STATUS_BAR_COLOR, 0)
    safePrefs.clear(KEY_STATUS_BAR_COLOR)
    colorStatusBar(statusBarColor)
  }
  if (safePrefs.contains(KEY_NAV_BAR_COLOR)) {
    val navBarColor = safePrefs.getInt(KEY_NAV_BAR_COLOR, 0)
    safePrefs.clear(KEY_NAV_BAR_COLOR)
    colorNavigationBar(navBarColor)
  }
  if (safePrefs.contains(KEY_ICON_TITLE_ACTIVE_COLOR)) {
    val activeColor = safePrefs.getInt(KEY_ICON_TITLE_ACTIVE_COLOR, 0)
    toolbarIconColor(activeColor)
    toolbarTitleColor(activeColor)
  }
  if (safePrefs.contains(KEY_ICON_TITLE_INACTIVE_COLOR)) {
    val inactiveColor = safePrefs.getInt(KEY_ICON_TITLE_INACTIVE_COLOR, 0)
    toolbarSubtitleColor(inactiveColor)
  }

  // Remove legacy preferences
  safePrefs.clear(
      KEY_PRIMARY_COLOR,
      KEY_PRIMARY_DARK_COLOR,
      KEY_ACCENT_COLOR,
      KEY_PRIMARY_TEXT_COLOR,
      KEY_SECONDARY_TEXT_COLOR,
      KEY_PRIMARY_TEXT_INVERSE_COLOR,
      KEY_SECONDARY_TEXT_INVERSE_COLOR,
      KEY_WINDOW_BG_COLOR,
      KEY_ICON_TITLE_ACTIVE_COLOR,
      KEY_ICON_TITLE_INACTIVE_COLOR
  )
}

@CheckResult internal fun Aesthetic.statusBarColorKey(): String {
  return if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
    attrKey(android.R.attr.statusBarColor)
  } else {
    KEY_STATUS_BAR_COLOR
  }
}

@CheckResult internal fun Aesthetic.navBarColorKey(): String {
  return if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
    attrKey(android.R.attr.navigationBarColor)
  } else {
    KEY_NAV_BAR_COLOR
  }
}

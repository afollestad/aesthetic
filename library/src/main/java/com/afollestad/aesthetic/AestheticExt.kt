/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import com.afollestad.aesthetic.Aesthetic.Companion.key
import com.afollestad.aesthetic.internal.KEY_LIGHT_STATUS_MODE
import com.afollestad.aesthetic.internal.KEY_STATUS_BAR_COLOR
import com.afollestad.aesthetic.internal.PREFS_NAME
import com.afollestad.aesthetic.utils.colorAttr
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.getRootView
import com.afollestad.aesthetic.utils.isColorLight
import com.afollestad.aesthetic.utils.setLightStatusBarCompat
import com.afollestad.aesthetic.utils.setStatusBarColorCompat
import com.afollestad.aesthetic.utils.subscribeBackgroundColor
import com.afollestad.aesthetic.utils.unsubscribeOnDetach
import com.afollestad.rxkprefs.RxkPrefs
import io.reactivex.Observable

@SuppressLint("CommitPrefEdits")
internal fun Aesthetic.initPrefs() {
  rxkPrefs = RxkPrefs(context, PREFS_NAME)
  prefs = rxkPrefs!!.getSharedPrefs()
  editor = safePrefs.edit()
  onAttached.onNext(true)
}

internal fun Aesthetic.deInitPrefs() {
  onAttached.onNext(false)
  prefs = null
  editor = null
  rxkPrefs = null
}

/**
 * Emits the current reactive shared preferences value if and when the instance is attached to
 * an Activity, when the preferences are actually initialized and populated. Without this,
 * we can get Kotlin null exceptions due the instance being unexpectedly null.
 */
internal fun Aesthetic.waitForAttach() = onAttached.filter { it }.map { rxkPrefs!! }

internal fun addBackgroundSubscriber(
  view: View,
  colorObservable: Observable<Int>
) {
  colorObservable
      .distinctToMainThread()
      .subscribeBackgroundColor(view)
      .unsubscribeOnDetach(view)
}

internal fun Aesthetic.invalidateStatusBar() {
  with(context as? Activity ?: return) {
    val key = String.format(KEY_STATUS_BAR_COLOR, key(context))
    val primaryDarkDefault = context.colorAttr(R.attr.colorPrimaryDark)
    val color = safePrefs.getInt(key, primaryDarkDefault)

    val rootView = getRootView()
    if (rootView is DrawerLayout) {
      // Color is set to DrawerLayout, Activity gets transparent status bar
      setLightStatusBarCompat(false)
      setStatusBarColorCompat(Color.TRANSPARENT)
      rootView.setStatusBarBackgroundColor(color)
    } else {
      setStatusBarColorCompat(color)
    }

    val modeRaw = safePrefs.getInt(KEY_LIGHT_STATUS_MODE, AutoSwitchMode.AUTO.value)
    val mode = AutoSwitchMode.fromInt(modeRaw)
    when (mode) {
      AutoSwitchMode.OFF -> setLightStatusBarCompat(false)
      AutoSwitchMode.ON -> setLightStatusBarCompat(true)
      else -> setLightStatusBarCompat(color.isColorLight())
    }
  }
}

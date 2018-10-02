/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.utils

import androidx.annotation.CheckResult
import com.afollestad.aesthetic.Aesthetic
import io.reactivex.Observable
import io.reactivex.Observable.empty

@CheckResult internal fun Aesthetic.observableForAttrName(
  name: String,
  fallback: Observable<Int>? = null
): Observable<Int>? {
  if (name.isNotEmpty() && !name.startsWith('?')) {
    // Don't override the hardcoded or resource value that is set.
    return empty()
  }
  return when (name) {
    "" -> fallback

    "?attr/colorPrimary", "?android:attr/colorPrimary" -> colorPrimary()
    "?attr/colorPrimaryDark", "?android:attr/colorPrimaryDark" -> colorPrimaryDark()
    "?attr/colorAccent", "?android:attr/colorAccent" -> colorAccent()

    "?android:attr/statusBarColor" -> colorStatusBar()
    "?android:attr/navigationBarColor" -> colorNavigationBar()
    "?android:attr/windowBackground" -> colorWindowBackground()

    "?android:attr/textColorPrimary" -> textColorPrimary()
    "?android:attr/textColorPrimaryInverse" -> textColorPrimaryInverse()
    "?android:attr/textColorSecondary" -> textColorSecondary()
    "?android:attr/textColorSecondaryInverse" -> textColorSecondaryInverse()

    else -> fallback ?: attribute(name.substring(1))
  }
}

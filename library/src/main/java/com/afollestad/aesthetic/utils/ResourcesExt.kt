/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.utils

import android.content.res.Resources
import android.util.Log
import androidx.annotation.CheckResult
import com.afollestad.aesthetic.BuildConfig

@CheckResult
internal fun Resources.safeResourceName(resId: Int): String {
  if (resId == 0) {
    return ""
  }
  return try {
    getResourceName(resId)
  } catch (_: Resources.NotFoundException) {
    if (BuildConfig.DEBUG) Log.w("AttrWizard", "Unable to get resource name for $resId")
    ""
  }
}

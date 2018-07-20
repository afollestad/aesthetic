package com.afollestad.aesthetic.utils

import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.support.annotation.ColorInt
import android.support.v4.view.LayoutInflaterCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.aesthetic.InflationInterceptor

internal fun AppCompatActivity.setInflaterFactory(li: LayoutInflater) {
  LayoutInflaterCompat.setFactory2(li, InflationInterceptor(this, li, delegate))
}

internal fun Activity.setStatusBarColorCompat(@ColorInt color: Int) {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    window.statusBarColor = color
  }
}

internal fun Activity.getRootView(): ViewGroup {
  return (findViewById<ViewGroup>(android.R.id.content)).getChildAt(
      0
  ) as ViewGroup
}

internal fun Activity.setNavBarColorCompat(@ColorInt color: Int) {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    window.navigationBarColor = color
  }
}

internal fun Activity.setLightStatusBarCompat(lightMode: Boolean) {
  val view = window.decorView
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    var flags = view.systemUiVisibility
    flags = if (lightMode) {
      flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    } else {
      flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    }
    view.systemUiVisibility = flags
  }
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
internal fun Activity.setTaskDescriptionColor(@ColorInt requestedColor: Int) {
  var color = requestedColor
  if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
    return
  }
  // Task description requires fully opaque color
  color = color.stripAlpha()
  // Default is app's launcher icon
  val icon: Bitmap? = if (Build.VERSION.SDK_INT >= 26) {
    packageManager.getAppIcon(packageName)
  } else {
    (applicationInfo.loadIcon(packageManager) as BitmapDrawable)
        .bitmap
  }
  if (icon != null) {
    // Sets color of entry in the system recents page
    val td = ActivityManager.TaskDescription(title as String, icon, color)
    setTaskDescription(td)
  }
}
/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.tint
import com.afollestad.aesthetic.utils.unsubscribeOnDetach
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.SnackbarContentLayout

/** @author Aidan Follestad (afollestad)
 */
@SuppressLint("RestrictedApi")
internal class AestheticSnackBarContentLayout(
  context: Context,
  attrs: AttributeSet? = null
) : SnackbarContentLayout(context, attrs) {

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    get().snackbarBackgroundColor()
      .distinctToMainThread()
      .subscribeTo(this::invalidateColors)
      .unsubscribeOnDetach(this)
  }

  private fun invalidateColors(color: Int) {
    setBackgroundColor(color)
    val parent = this.parent
    if (parent is Snackbar.SnackbarLayout) {
      val background = parent.background
      if (background != null) {
        parent.background = background.tint(color)
      } else {
        parent.setBackgroundColor(color)
      }
    }
  }
}

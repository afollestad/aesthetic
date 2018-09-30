/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.view.menu.ActionMenuItemView
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.utils.adjustAlpha
import com.afollestad.aesthetic.utils.one
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.tint
import com.afollestad.aesthetic.utils.toMainThread
import com.afollestad.aesthetic.utils.unsubscribeOnDetach

/** @author Aidan Follestad (afollestad) */
@SuppressLint("RestrictedApi")
internal class AestheticActionMenuItemView(
  context: Context,
  attrs: AttributeSet? = null
) : ActionMenuItemView(context, attrs) {

  companion object {
    const val UNFOCUSED_ALPHA = 0.5f
  }

  private var icon: Drawable? = null

  private fun invalidateColors(color: Int) {
    val sl = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_selected),
            intArrayOf(android.R.attr.state_selected)
        ),
        intArrayOf(
            color.adjustAlpha(UNFOCUSED_ALPHA),
            color
        )
    )
    if (icon != null) {
      setIcon(icon!!, sl)
    }
    setTextColor(color)
  }

  override fun setIcon(icon: Drawable) {
    super.setIcon(icon)
    // We need to retrieve the color again here.
    // For some reason, without this, a transparent color is used and the icon disappears
    // when the overflow menu opens.
    get().toolbarIconColor()
        .one()
        .toMainThread()
        .subscribeTo(::invalidateColors)
        .unsubscribeOnDetach(this)
  }

  @Suppress("MemberVisibilityCanBePrivate")
  fun setIcon(
    icon: Drawable,
    colors: ColorStateList
  ) {
    this.icon = icon
    super.setIcon(icon.tint(colors))
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    get().toolbarIconColor()
        .one()
        .toMainThread()
        .subscribeTo(::invalidateColors)
        .unsubscribeOnDetach(this)
  }
}

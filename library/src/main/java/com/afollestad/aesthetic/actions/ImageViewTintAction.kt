/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.actions

import android.widget.ImageView
import io.reactivex.functions.Consumer

/** @author Aidan Follestad (afollestad) */
internal class ImageViewTintAction constructor(val view: ImageView) : Consumer<Int> {
  override fun accept(color: Int) {
    view.setColorFilter(color)
  }
}

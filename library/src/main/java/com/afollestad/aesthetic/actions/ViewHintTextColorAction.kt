/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.actions

import android.widget.TextView
import io.reactivex.functions.Consumer

/** @author Aidan Follestad (afollestad) */
internal class ViewHintTextColorAction constructor(val view: TextView) : Consumer<Int> {
  override fun accept(color: Int) {
    view.setHintTextColor(color)
  }
}

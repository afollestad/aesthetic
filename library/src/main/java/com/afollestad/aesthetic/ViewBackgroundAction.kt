/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic

import android.view.View
import io.reactivex.functions.Consumer

/** @author Aidan Follestad (afollestad) */
internal class ViewBackgroundAction constructor(val view: View) : Consumer<Int> {
  override fun accept(color: Int) {
    view.setBackgroundColor(color)
  }
}

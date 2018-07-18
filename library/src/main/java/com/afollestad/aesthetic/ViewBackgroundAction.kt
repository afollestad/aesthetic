package com.afollestad.aesthetic

import android.view.View
import io.reactivex.functions.Consumer

/** @author Aidan Follestad (afollestad) */
class ViewBackgroundAction constructor(val view: View) : Consumer<Int> {
  override fun accept(color: Int) {
    view.setBackgroundColor(color)
  }
}

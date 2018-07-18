package com.afollestad.aesthetic

import android.widget.TextView
import io.reactivex.functions.Consumer

/** @author Aidan Follestad (afollestad) */
internal class ViewHintTextColorAction constructor(val view: TextView) : Consumer<Int> {
  override fun accept(color: Int) {
    view.setHintTextColor(color)
  }
}

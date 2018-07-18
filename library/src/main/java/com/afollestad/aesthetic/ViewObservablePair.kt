package com.afollestad.aesthetic

import android.view.View
import io.reactivex.Observable

/** @author Aidan Follestad (afollestad) */
internal data class ViewObservablePair(
  val view: View,
  val observable: Observable<Int>
)

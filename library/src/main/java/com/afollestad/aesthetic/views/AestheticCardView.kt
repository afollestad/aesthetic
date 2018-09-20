/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.R.attr
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.resId
import com.afollestad.aesthetic.utils.subscribeBackgroundColor
import com.afollestad.aesthetic.utils.unsubscribeOnDetach
import com.afollestad.aesthetic.utils.watchColor
import io.reactivex.disposables.Disposable

/** @author Aidan Follestad (afollestad) */
@SuppressLint("PrivateResource")
class AestheticCardView(
  context: Context,
  attrs: AttributeSet? = null
) : CardView(context, attrs) {

  private var backgroundResId: Int = 0

  init {
    if (attrs != null) {
      backgroundResId = context.resId(attrs, attr.cardBackgroundColor)
    }
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    watchColor(
        context,
        backgroundResId,
        get().colorCardViewBackground()
    )
        .distinctToMainThread()
        .subscribeBackgroundColor(this)
        .unsubscribeOnDetach(this)
  }
}

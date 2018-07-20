package com.afollestad.aesthetic

import android.annotation.SuppressLint
import com.afollestad.aesthetic.utils.TintHelper.createTintedDrawable

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.support.v7.view.menu.ActionMenuItemView
import android.util.AttributeSet
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.onErrorLogAndRethrow
import com.afollestad.aesthetic.utils.onMainThread
import com.afollestad.aesthetic.utils.one
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/** @author Aidan Follestad (afollestad) */
@SuppressLint("RestrictedApi")
internal class AestheticActionMenuItemView(
  context: Context,
  attrs: AttributeSet? = null
) : ActionMenuItemView(context, attrs) {

  private var icon: Drawable? = null
  private var subscription: Disposable? = null

  private fun invalidateColors(@NonNull colors: ActiveInactiveColors) {
    if (icon != null) {
      setIcon(icon!!, colors.toEnabledSl())
    }
    setTextColor(colors.activeColor)
  }

  override fun setIcon(icon: Drawable) {
    super.setIcon(icon)

    // We need to retrieve the color again here.
    // For some reason, without this, a transparent color is used and the icon disappears
    // when the overflow menu opens.
    Aesthetic.get()
        .colorIconTitle(null)
        .onMainThread()
        .one()
        .subscribe(
            Consumer { invalidateColors(it) },
            onErrorLogAndRethrow()
        )
  }

  fun setIcon(
    icon: Drawable,
    colors: ColorStateList
  ) {
    this.icon = icon
    super.setIcon(createTintedDrawable(icon, colors))
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    subscription = Aesthetic.get()
        .colorIconTitle(null)
        .distinctToMainThread()
        .subscribe(
            Consumer { invalidateColors(it) },
            onErrorLogAndRethrow()
        )
  }

  override fun onDetachedFromWindow() {
    subscription?.dispose()
    super.onDetachedFromWindow()
  }
}

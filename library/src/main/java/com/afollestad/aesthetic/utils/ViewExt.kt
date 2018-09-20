/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.utils

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorInt
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import com.afollestad.aesthetic.ActiveInactiveColors
import com.afollestad.aesthetic.R
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.lang.reflect.Field

@Suppress("DEPRECATION")
internal fun View.setBackgroundCompat(drawable: Drawable?) {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
    background = drawable
  } else {
    setBackgroundDrawable(drawable)
  }
}

internal fun Toolbar.setOverflowButtonColor(@ColorInt color: Int) {
  val overflowDrawable = overflowIcon
  if (overflowDrawable != null) {
    overflowIcon = overflowDrawable.tint(color)
  }
}

internal fun Toolbar.tintMenu(
  menu: Menu,
  titleIconColors: ActiveInactiveColors
) {
  // The collapse icon displays when action views are expanded (e.g. SearchView)
  try {
    val field = Toolbar::class.java.getDeclaredField("mCollapseIcon")
    field.isAccessible = true
    val collapseIcon = field.get(this) as? Drawable
    if (collapseIcon != null) {
      field.set(this, collapseIcon.tint(titleIconColors.toEnabledSl()))
    }
  } catch (e: Exception) {
    e.printStackTrace()
  }

  // Theme menu action views
  for (i in 0 until menu.size()) {
    val menuItem = menu.getItem(i)
    val actionView = menuItem.actionView
    if (actionView is SearchView) {
      actionView.setColors(titleIconColors)
    }
    if (menu.getItem(i).icon != null) {
      menuItem.icon = menuItem.icon.tint(titleIconColors.toEnabledSl())
    }
  }
}

internal fun SearchView.setColors(tintColors: ActiveInactiveColors) {
  val cls = javaClass
  try {
    val mSearchSrcTextViewField = cls.getDeclaredField("mSearchSrcTextView")
    mSearchSrcTextViewField.isAccessible = true
    val mSearchSrcTextView = mSearchSrcTextViewField.get(this) as EditText
    mSearchSrcTextView.setTextColor(tintColors.activeColor)
    mSearchSrcTextView.setHintTextColor(tintColors.inactiveColor)
    mSearchSrcTextView.setCursorTint(tintColors.activeColor)

    var field = cls.getDeclaredField("mSearchButton")
    tintImageView(this, field, tintColors)
    field = cls.getDeclaredField("mGoButton")
    tintImageView(this, field, tintColors)
    field = cls.getDeclaredField("mCloseButton")
    tintImageView(this, field, tintColors)
    field = cls.getDeclaredField("mVoiceButton")
    tintImageView(this, field, tintColors)

    field = cls.getDeclaredField("mSearchPlate")
    field.isAccessible = true

    (field.get(this) as View).apply {
      setTintAuto(
          color = tintColors.activeColor,
          requestBackground = true,
          isDark = !tintColors.activeColor.isColorLight()
      )
    }

    field = cls.getDeclaredField("mSearchHintIcon")
    field.isAccessible = true

    (field.get(this) as Drawable).apply {
      field.set(
          this@setColors,
          this@apply.tint(tintColors.toEnabledSl())
      )
    }
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

@Throws(Exception::class)
internal fun tintImageView(
  target: Any,
  field: Field,
  tintColors: ActiveInactiveColors
) {
  field.isAccessible = true
  val imageView = field.get(target) as ImageView
  if (imageView.drawable != null) {
    imageView.setImageDrawable(imageView.drawable.tint(tintColors.toEnabledSl()))
  }
}

internal fun TextInputLayout.setHintColor(@ColorInt hintColor: Int) {
  try {
    val defaultHintColorField = TextInputLayout::class.java.findField(
        "defaultHintTextColor", "mDefaultTextColor"
    )
    defaultHintColorField.isAccessible = true
    defaultHintColorField.set(this, ColorStateList.valueOf(hintColor))
    val updateLabelStateMethod = TextInputLayout::class.java.getDeclaredMethod(
        "updateLabelState", Boolean::class.javaPrimitiveType, Boolean::class.javaPrimitiveType
    )
    updateLabelStateMethod.isAccessible = true
    updateLabelStateMethod.invoke(this, false, true)
  } catch (t: Throwable) {
    throw IllegalStateException(
        "Failed to set TextInputLayout hint (collapsed) color: " + t.localizedMessage, t
    )
  }
}

internal fun TextInputLayout.setAccentColor(@ColorInt accentColor: Int) {
  try {
    val focusedTextColor = TextInputLayout::class.java.findField(
        "focusedTextColor", "mFocusedTextColor"
    )
    focusedTextColor.isAccessible = true
    focusedTextColor.set(this, ColorStateList.valueOf(accentColor))
    val updateLabelStateMethod = TextInputLayout::class.java.getDeclaredMethod(
        "updateLabelState", Boolean::class.javaPrimitiveType, Boolean::class.javaPrimitiveType
    )
    updateLabelStateMethod.isAccessible = true
    updateLabelStateMethod.invoke(this, false, true)
  } catch (t: Throwable) {
    throw IllegalStateException(
        "Failed to set TextInputLayout accent (expanded) color: " + t.localizedMessage, t
    )
  }
}

internal fun TextInputLayout.setStrokeColor(@ColorInt accentColor: Int) {
  try {
    val disabledTextColor = TextInputLayout::class.java.findField("defaultStrokeColor")
    disabledTextColor.isAccessible = true
    disabledTextColor.set(this, accentColor)
    val updateLabelStateMethod = TextInputLayout::class.java.getDeclaredMethod(
        "updateLabelState", Boolean::class.javaPrimitiveType, Boolean::class.javaPrimitiveType
    )
    updateLabelStateMethod.isAccessible = true
    updateLabelStateMethod.invoke(this, false, true)
  } catch (t: Throwable) {
    throw IllegalStateException(
        "Failed to set TextInputLayout accent (expanded) color: " + t.localizedMessage, t
    )
  }
}

internal fun TextInputLayout.setStrokeColorHover(@ColorInt accentColor: Int) {
  try {
    val disabledTextColor = TextInputLayout::class.java.findField("hoveredStrokeColor")
    disabledTextColor.isAccessible = true
    disabledTextColor.set(this, accentColor)
    val updateLabelStateMethod = TextInputLayout::class.java.getDeclaredMethod(
        "updateLabelState", Boolean::class.javaPrimitiveType, Boolean::class.javaPrimitiveType
    )
    updateLabelStateMethod.isAccessible = true
    updateLabelStateMethod.invoke(this, false, true)
  } catch (t: Throwable) {
    throw IllegalStateException(
        "Failed to set TextInputLayout accent (expanded) color: " + t.localizedMessage, t
    )
  }
}

internal fun TextInputLayout.setStrokeColorFocused(@ColorInt accentColor: Int) {
  try {
    val disabledTextColor = TextInputLayout::class.java.findField("focusedStrokeColor")
    disabledTextColor.isAccessible = true
    disabledTextColor.set(this, accentColor)
    val updateLabelStateMethod = TextInputLayout::class.java.getDeclaredMethod(
        "updateLabelState", Boolean::class.javaPrimitiveType, Boolean::class.javaPrimitiveType
    )
    updateLabelStateMethod.isAccessible = true
    updateLabelStateMethod.invoke(this, false, true)
  } catch (t: Throwable) {
    throw IllegalStateException(
        "Failed to set TextInputLayout accent (expanded) color: " + t.localizedMessage, t
    )
  }
}

internal fun TextInputLayout.setDisabledColor(@ColorInt accentColor: Int) {
  try {
    val disabledTextColor = TextInputLayout::class.java.findField("disabledColor")
    disabledTextColor.isAccessible = true
    disabledTextColor.set(this, accentColor)
    val updateLabelStateMethod = TextInputLayout::class.java.getDeclaredMethod(
        "updateLabelState", Boolean::class.javaPrimitiveType, Boolean::class.javaPrimitiveType
    )
    updateLabelStateMethod.isAccessible = true
    updateLabelStateMethod.invoke(this, false, true)
  } catch (t: Throwable) {
    throw IllegalStateException(
        "Failed to set TextInputLayout accent (expanded) color: " + t.localizedMessage, t
    )
  }
}

val View.isAttachedToWindowCompat: Boolean get() = ViewCompat.isAttachedToWindow(this)

fun View.unsubscribeOnDetach(disposableFactory: () -> Disposable) {
  val attachedDisposables = ensureAttachedDisposables()
  if (isAttachedToWindowCompat) {
    val disposable = disposableFactory()
    if (isAttachedToWindowCompat) {
      attachedDisposables.disposables += disposable
    } else {
      disposable.dispose()
    }
  } else {
    attachedDisposables += disposableFactory
  }
}

fun Disposable.unsubscribeOnDetach(view: View): Disposable {
  view.unsubscribeOnDetach { this }
  return this
}

private fun View.ensureAttachedDisposables(): AttachedDisposables {
  var attachedDisposables = getTag(R.id.tag_attached_disposables) as AttachedDisposables?

  if (attachedDisposables == null) {
    attachedDisposables = AttachedDisposables()
    setTag(R.id.tag_attached_disposables, attachedDisposables)
    addOnAttachStateChangeListener(attachedDisposables)
  }

  return attachedDisposables
}

private class AttachedDisposables : View.OnAttachStateChangeListener {
  val disposables = CompositeDisposable()
  private val disposableFactory by lazy { mutableListOf<() -> Disposable>() }

  operator fun plusAssign(disposable: () -> Disposable) {
    disposableFactory += disposable
  }

  override fun onViewAttachedToWindow(v: View) {
    disposableFactory.apply {
      forEach { factory -> disposables += factory() }
      clear()
    }
  }

  override fun onViewDetachedFromWindow(v: View) = disposables.clear()
}

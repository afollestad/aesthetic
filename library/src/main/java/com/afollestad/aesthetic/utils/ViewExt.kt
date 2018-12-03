/**
 * Designed and developed by Aidan Follestad (@afollestad)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.afollestad.aesthetic.utils

import android.R.attr
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
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
  activeColor: Int,
  inactiveColor: Int
) {
  val colors = ColorStateList(
      arrayOf(
          intArrayOf(attr.state_enabled), intArrayOf(-attr.state_enabled)
      ),
      intArrayOf(activeColor, inactiveColor)
  )

  // The collapse icon displays when action views are expanded (e.g. SearchView)
  try {
    val field = Toolbar::class.findField("mCollapseIcon")
    val collapseIcon = field.get(this) as? Drawable
    if (collapseIcon != null) {
      field.set(this, collapseIcon.tint(colors))
    }
  } catch (e: Exception) {
    e.printStackTrace()
  }

  // Theme menu action views
  for (i in 0 until menu.size()) {
    val menuItem = menu.getItem(i)
    val actionView = menuItem.actionView
    if (actionView is SearchView) {
      actionView.setColors(activeColor, inactiveColor)
    }
    if (menu.getItem(i).icon != null) {
      menuItem.icon = menuItem.icon.tint(colors)
    }
  }
}

internal fun SearchView.setColors(
  activeColor: Int,
  inactiveColor: Int
) {
  val tintColors = ColorStateList(
      arrayOf(
          intArrayOf(attr.state_enabled), intArrayOf(-attr.state_enabled)
      ),
      intArrayOf(activeColor, inactiveColor)
  )

  try {
    val mSearchSrcTextViewField = this::class.findField("mSearchSrcTextView")
    mSearchSrcTextViewField.isAccessible = true
    val mSearchSrcTextView = mSearchSrcTextViewField.get(this) as EditText
    mSearchSrcTextView.setTextColor(activeColor)
    mSearchSrcTextView.setHintTextColor(inactiveColor)
    mSearchSrcTextView.setCursorTint(activeColor)

    var field = this::class.findField("mSearchButton")
    tintImageView(this, field, tintColors)
    field = this::class.findField("mGoButton")
    tintImageView(this, field, tintColors)
    field = this::class.findField("mCloseButton")
    tintImageView(this, field, tintColors)
    field = this::class.findField("mVoiceButton")
    tintImageView(this, field, tintColors)

    field = this::class.findField("mSearchPlate")
    (field.get(this) as View).apply {
      setTintAuto(
          color = activeColor,
          requestBackground = true,
          isDark = !activeColor.isColorLight()
      )
    }

    field = this::class.findField("mSearchHintIcon")

    (field.get(this) as Drawable).apply {
      field.set(this@setColors, this@apply.tint(tintColors))
    }
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

@Throws(Exception::class)
internal fun tintImageView(
  target: Any,
  field: Field,
  colors: ColorStateList
) {
  field.isAccessible = true
  val imageView = field.get(target) as ImageView
  if (imageView.drawable != null) {
    imageView.setImageDrawable(imageView.drawable.tint(colors))
  }
}

internal fun TextInputLayout.setHintColor(@ColorInt hintColor: Int) {
  try {
    val defaultHintColorField = TextInputLayout::class.findField(
        "defaultHintTextColor", "mDefaultTextColor"
    )
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
    val focusedTextColor = TextInputLayout::class.findField(
        "focusedTextColor", "mFocusedTextColor"
    )
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
    val disabledTextColor = TextInputLayout::class.findField("defaultStrokeColor")
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
    val disabledTextColor = TextInputLayout::class.findField("hoveredStrokeColor")
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
    val disabledTextColor = TextInputLayout::class.findField("focusedStrokeColor")
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
    val disabledTextColor = TextInputLayout::class.findField("disabledColor")
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

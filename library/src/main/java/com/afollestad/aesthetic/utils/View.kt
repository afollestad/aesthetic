package com.afollestad.aesthetic.utils

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorInt
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.afollestad.aesthetic.ActiveInactiveColors
import java.lang.reflect.Field

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
    overflowIcon = TintHelper.createTintedDrawable(overflowDrawable, color)
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
      field.set(
          this,
          TintHelper.createTintedDrawable(collapseIcon, titleIconColors.toEnabledSl())
      )
    }
  } catch (e: Exception) {
    e.printStackTrace()
  }

  // Theme menu action views
  for (i in 0 until menu.size()) {
    val item = menu.getItem(i)
    if (item.actionView is SearchView) {
      (item.actionView as SearchView).setColors(titleIconColors)
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
    TintHelper.setCursorTint(mSearchSrcTextView, tintColors.activeColor)

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
    TintHelper.setTintAuto(
        field.get(this) as View,
        tintColors.activeColor,
        true,
        !tintColors.activeColor.isColorLight()
    )

    field = cls.getDeclaredField("mSearchHintIcon")
    field.isAccessible = true
    field.set(
        this,
        TintHelper.createTintedDrawable(field.get(this) as Drawable, tintColors.toEnabledSl())
    )
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
    imageView.setImageDrawable(
        TintHelper.createTintedDrawable(imageView.drawable, tintColors.toEnabledSl())
    )
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

internal fun TextInputLayout.setDisabledColor(@ColorInt accentColor: Int) {
  try {
    val disabledTextColor = TextInputLayout::class.java.findField("disabledColor")
    disabledTextColor.isAccessible = true
    disabledTextColor.set(this, ColorStateList.valueOf(accentColor))
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
/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.support.annotation.CheckResult
import android.support.annotation.ColorInt
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.CheckedTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.TintableBackgroundView
import androidx.core.view.ViewCompat
import com.afollestad.aesthetic.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

/** @author Aidan Follestad (afollestad) */
internal object TintHelper {

  @SuppressLint("PrivateResource")
  @ColorInt
  private fun getDefaultRippleColor(
    context: Context,
    useDarkRipple: Boolean
  ): Int {
    // Light ripple is actually translucent black, and vice versa
    return context.color(
        if (useDarkRipple) R.color.ripple_material_light else R.color.ripple_material_dark
    )
  }

  private fun getDisabledColorStateList(
    @ColorInt normal: Int,
    @ColorInt disabled: Int
  ): ColorStateList {
    return ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_enabled), intArrayOf(android.R.attr.state_enabled)
        ),
        intArrayOf(disabled, normal)
    )
  }

  private fun setTintSelector(
    view: View,
    @ColorInt color: Int,
    darker: Boolean,
    useDarkTheme: Boolean
  ) {
    val isColorLight = color.isColorLight()
    val disabled = view.context.color(
        if (useDarkTheme) R.color.ate_button_disabled_dark
        else R.color.ate_button_disabled_light
    )
    val pressed = color.shiftColor(if (darker) 0.9f else 1.1f)
    val activated = color.shiftColor(if (darker) 1.1f else 0.9f)
    val rippleColor =
      getDefaultRippleColor(view.context, isColorLight)
    val textColor = view.context.color(
        if (isColorLight) R.color.ate_primary_text_light
        else R.color.ate_primary_text_dark
    )

    val sl: ColorStateList
    when (view) {
      is Button -> {
        sl = getDisabledColorStateList(color, disabled)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && view.getBackground() is RippleDrawable) {
          val rd = view.getBackground() as RippleDrawable
          rd.setColor(ColorStateList.valueOf(rippleColor))
        }

        // Disabled text color state for buttons, may get overridden later by ATE tags
        view.setTextColor(
            getDisabledColorStateList(
                textColor,
                view.getContext().color(
                    if (useDarkTheme) R.color.ate_button_text_disabled_dark
                    else R.color.ate_button_text_disabled_light
                )
            )
        )
      }
      is FloatingActionButton -> {
        // FloatingActionButton doesn't support disabled state?
        sl = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_pressed), intArrayOf(android.R.attr.state_pressed)
            ),
            intArrayOf(color, pressed)
        )

        view.rippleColor = rippleColor
        view.backgroundTintList = sl
        if (view.drawable != null)
          view.setImageDrawable(
              createTintedDrawable(view.drawable, textColor)
          )
        return
      }
      else -> sl = ColorStateList(
          arrayOf(
              intArrayOf(-android.R.attr.state_enabled), intArrayOf(android.R.attr.state_enabled),
              intArrayOf(android.R.attr.state_enabled, android.R.attr.state_pressed),
              intArrayOf(android.R.attr.state_enabled, android.R.attr.state_activated),
              intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked)
          ),
          intArrayOf(disabled, color, pressed, activated, activated)
      )
    }

    var drawable: Drawable? = view.background
    if (drawable != null) {
      drawable = createTintedDrawable(drawable, sl)
      view.setBackgroundCompat(drawable)
    }

    if (view is TextView && view !is Button) {
      view.setTextColor(
          getDisabledColorStateList(
              textColor,
              view.getContext().color(
                  if (isColorLight) R.color.ate_text_disabled_light
                  else R.color.ate_text_disabled_dark
              )
          )
      )
    }
  }

  @SuppressLint("PrivateResource")
  fun setTintAuto(
    view: View,
    @ColorInt color: Int,
    requestBackground: Boolean,
    isDark: Boolean
  ) {
    var background = requestBackground
    if (!background) {
      when (view) {
        is RadioButton -> setTint(view, color, isDark)
        is SeekBar -> setTint(view, color, isDark)
        is ProgressBar -> setTint(view, color)
        is EditText -> setTint(view, color, isDark)
        is CheckBox -> setTint(view, color, isDark)
        is ImageView -> setTint(view, color)
        is Switch -> setTint(view, color, isDark)
        is SwitchCompat -> setTint(view, color, isDark)
        is CheckedTextView -> setTint(view, color, isDark)
        else -> background = true
      }

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
          !background &&
          view.background is RippleDrawable
      ) {
        // Ripples for the above views (e.g. when you tap and hold a switch or checkbox)
        val rd = view.background as RippleDrawable
        val unchecked = view.context.color(
            if (isDark) R.color.ripple_material_dark else R.color.ripple_material_light
        )
        val checked = color.adjustAlpha(0.4f)
        val sl = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_activated, -android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_activated), intArrayOf(android.R.attr.state_checked)
            ),
            intArrayOf(unchecked, checked, checked)
        )
        rd.setColor(sl)
      }
    }
    if (background) {
      // Need to tint the background of a view
      if (view is FloatingActionButton || view is Button) {
        setTintSelector(view, color, false, isDark)
      } else if (view.background != null) {
        var drawable: Drawable? = view.background
        if (drawable != null) {
          if (view is TextInputEditText) {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
          } else {
            drawable =
                createTintedDrawable(drawable, color)
            view.setBackgroundCompat(drawable)
          }
        }
      }
    }
  }

  fun setTint(
    radioButton: RadioButton,
    @ColorInt color: Int,
    useDarker: Boolean
  ) {
    val sl = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked)
        ),
        intArrayOf(
            // Radio button includes own alpha for disabled state
            radioButton.context.color(
                if (useDarker)
                  R.color.ate_control_disabled_dark
                else
                  R.color.ate_control_disabled_light
            ).stripAlpha(),
            radioButton.context.color(
                if (useDarker) R.color.ate_control_normal_dark else R.color.ate_control_normal_light
            ),
            color
        )
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      radioButton.buttonTintList = sl
    } else {
      @SuppressLint("PrivateResource")
      val d = createTintedDrawable(
          radioButton.context.drawable(R.drawable.abc_btn_radio_material),
          sl
      )
      radioButton.buttonDrawable = d
    }
  }

  fun setTint(
    seekBar: SeekBar,
    @ColorInt color: Int,
    useDarker: Boolean
  ) {
    val s1 = getDisabledColorStateList(
        color,
        seekBar.context.color(
            if (useDarker)
              R.color.ate_control_disabled_dark
            else
              R.color.ate_control_disabled_light
        )
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      seekBar.thumbTintList = s1
      seekBar.progressTintList = s1
    } else {
      val progressDrawable =
        createTintedDrawable(seekBar.progressDrawable, s1)
      seekBar.progressDrawable = progressDrawable
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        val thumbDrawable =
          createTintedDrawable(seekBar.thumb, s1)
        seekBar.thumb = thumbDrawable
      }
    }
  }

  fun setTint(progressBar: ProgressBar, @ColorInt color: Int) {
    setTint(progressBar, color, false)
  }

  private fun setTint(
    progressBar: ProgressBar,
    @ColorInt color: Int,
    skipIndeterminate: Boolean
  ) {
    val sl = ColorStateList.valueOf(color)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      progressBar.progressTintList = sl
      progressBar.secondaryProgressTintList = sl
      if (!skipIndeterminate) {
        progressBar.indeterminateTintList = sl
      }
    } else {
      val mode = PorterDuff.Mode.SRC_IN
      if (!skipIndeterminate && progressBar.indeterminateDrawable != null) {
        progressBar.indeterminateDrawable.setColorFilter(color, mode)
      }
      if (progressBar.progressDrawable != null) {
        progressBar.progressDrawable.setColorFilter(color, mode)
      }
    }
  }

  private fun setTint(
    editText: EditText,
    @ColorInt color: Int,
    useDarker: Boolean
  ) {
    val editTextColorStateList = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_enabled), intArrayOf(
            android.R.attr.state_enabled, -android.R.attr.state_pressed,
            -android.R.attr.state_focused
        ), intArrayOf()
        ),
        intArrayOf(
            editText.context.color(
                if (useDarker) R.color.ate_text_disabled_dark else R.color.ate_text_disabled_light
            ),
            editText.context.color(
                if (useDarker) R.color.ate_control_normal_dark else R.color.ate_control_normal_light
            ),
            color
        )
    )
    if (editText is TintableBackgroundView) {
      ViewCompat.setBackgroundTintList(editText, editTextColorStateList)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      editText.backgroundTintList = editTextColorStateList
    }
    setCursorTint(editText, color)
  }

  fun setTint(
    box: CheckBox,
    @ColorInt color: Int,
    useDarker: Boolean
  ) {
    val sl = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked)
        ),
        intArrayOf(
            box.context.color(
                if (useDarker)
                  R.color.ate_control_disabled_dark
                else
                  R.color.ate_control_disabled_light
            ),
            box.context.color(
                if (useDarker) R.color.ate_control_normal_dark else R.color.ate_control_normal_light
            ),
            color
        )
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      box.buttonTintList = sl
    } else {
      @SuppressLint("PrivateResource")
      val drawable = createTintedDrawable(
          box.context.drawable(R.drawable.abc_btn_check_material), sl
      )
      box.buttonDrawable = drawable
    }
  }

  private fun setTint(image: ImageView, @ColorInt color: Int) {
    image.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
  }

  private fun modifySwitchDrawable(
    context: Context,
    from: Drawable,
    @ColorInt requestedTint: Int,
    thumb: Boolean,
    compatSwitch: Boolean,
    useDarker: Boolean
  ): Drawable? {
    val sl = getCheckableColorStateList(
        context = context,
        requestedTint = requestedTint,
        thumb = thumb,
        compatSwitch = compatSwitch,
        useDarker = useDarker
    )
    return createTintedDrawable(from, sl)
  }

  private fun getCheckableColorStateList(
    context: Context,
    @ColorInt requestedTint: Int,
    thumb: Boolean,
    compatSwitch: Boolean,
    useDarker: Boolean
  ): ColorStateList {
    var tint = requestedTint
    if (useDarker) {
      tint = tint.shiftColor(1.1f)
    }
    tint = tint.adjustAlpha(if (compatSwitch && !thumb) 0.5f else 1.0f)

    val disabled: Int
    var normal: Int
    if (thumb) {
      disabled = context.color(
          if (useDarker) R.color.ate_switch_thumb_disabled_dark
          else R.color.ate_switch_thumb_disabled_light
      )
      normal = context.color(
          if (useDarker) R.color.ate_switch_thumb_normal_dark
          else R.color.ate_switch_thumb_normal_light
      )
    } else {
      disabled = context.color(
          if (useDarker) R.color.ate_switch_track_disabled_dark
          else R.color.ate_switch_track_disabled_light
      )
      normal = context.color(
          if (useDarker) R.color.ate_switch_track_normal_dark
          else R.color.ate_switch_track_normal_light
      )
    }

    // Stock switch includes its own alpha
    if (!compatSwitch) {
      normal = normal.stripAlpha()
    }

    return ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_enabled), intArrayOf(
            android.R.attr.state_enabled, -android.R.attr.state_activated,
            -android.R.attr.state_checked
        ), intArrayOf(android.R.attr.state_enabled, android.R.attr.state_activated),
            intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked)
        ),
        intArrayOf(disabled, normal, tint, tint)
    )
  }

  fun setTint(
    switchView: Switch,
    @ColorInt color: Int,
    useDarker: Boolean
  ) {
    if (switchView.trackDrawable != null) {
      switchView.trackDrawable = modifySwitchDrawable(
          switchView.context,
          switchView.trackDrawable,
          color,
          false,
          false,
          useDarker
      )
    }
    if (switchView.thumbDrawable != null) {
      switchView.thumbDrawable = modifySwitchDrawable(
          switchView.context,
          switchView.thumbDrawable,
          color,
          true,
          false,
          useDarker
      )
    }
  }

  fun setTint(
    switchView: SwitchCompat,
    @ColorInt color: Int,
    useDarker: Boolean
  ) {
    if (switchView.trackDrawable != null) {
      switchView.trackDrawable = modifySwitchDrawable(
          switchView.context,
          switchView.trackDrawable,
          color,
          false,
          true,
          useDarker
      )
    }
    if (switchView.thumbDrawable != null) {
      switchView.thumbDrawable = modifySwitchDrawable(
          switchView.context,
          switchView.thumbDrawable,
          color,
          true,
          true,
          useDarker
      )
    }
  }

  fun setTint(
    textView: CheckedTextView,
    @ColorInt color: Int,
    useDarker: Boolean
  ) {
    val currentDrawable = textView.checkMarkDrawable ?: return
    textView.checkMarkDrawable = modifySwitchDrawable(
        context = textView.context,
        from = currentDrawable,
        requestedTint = color,
        thumb = false,
        compatSwitch = false,
        useDarker = useDarker
    )
  }

  // This returns a NEW Drawable because of the mutate() call. The mutate() call is necessary
  // because Drawables with the same resource have shared states otherwise.
  @CheckResult
  fun createTintedDrawable(drawable: Drawable?, @ColorInt color: Int): Drawable? {
    var result: Drawable = drawable ?: return null
    result = DrawableCompat.wrap(result.mutate())
    DrawableCompat.setTintMode(result, PorterDuff.Mode.SRC_IN)
    DrawableCompat.setTint(result, color)
    return drawable
  }

  // This returns a NEW Drawable because of the mutate() call. The mutate() call is necessary
  // because Drawables with the same resource have shared states otherwise.
  @CheckResult
  fun createTintedDrawable(
    drawable: Drawable?,
    sl: ColorStateList
  ): Drawable? {
    var result: Drawable = drawable ?: return null
    result = DrawableCompat.wrap(result.mutate())
    DrawableCompat.setTintList(result, sl)
    return result
  }

  fun setCursorTint(editText: EditText, @ColorInt color: Int) {
    try {
      val fCursorDrawableRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")
      fCursorDrawableRes.isAccessible = true
      val mCursorDrawableRes = fCursorDrawableRes.getInt(editText)
      val fEditor = TextView::class.java.getDeclaredField("mEditor")
      fEditor.isAccessible = true
      val editor = fEditor.get(editText)
      val clazz = editor.javaClass
      val fCursorDrawable = clazz.getDeclaredField("mCursorDrawable")
      fCursorDrawable.isAccessible = true
      val drawables = arrayOfNulls<Drawable>(2)
      drawables[0] = editText.context.drawable(mCursorDrawableRes)
      drawables[0] =
          createTintedDrawable(drawables[0], color)
      drawables[1] = editText.context.drawable(mCursorDrawableRes)
      drawables[1] =
          createTintedDrawable(drawables[1], color)
      fCursorDrawable.set(editor, drawables)
    } catch (e: Exception) {
      // TODO FIX    e.printStackTrace()
    }
  }
}
